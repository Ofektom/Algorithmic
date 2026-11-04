// Shared behaviour for the static site: bottom bar, the problem catalogue (data/catalog.json, so
// browsing never waits for the server), calls to the Java API on Render, search, tabs, copy/share
// and the local history/progress store.

const API = (window.ALGO_API || '').replace(/\/+$/, '');

// ── Small DOM helper: el('a.row-link', { href }, [children]) ──────────────────────────
function el(spec, attrs, children) {
    const [tag, ...classes] = spec.split('.');
    const node = document.createElement(tag || 'div');
    if (classes.length) node.className = classes.join(' ');
    Object.entries(attrs || {}).forEach(function([k, v]) {
        if (v === null || v === undefined || v === false) return;
        if (k === 'text') node.textContent = v;
        else if (k === 'on') Object.entries(v).forEach(function([ev, fn]) { node.addEventListener(ev, fn); });
        else node.setAttribute(k, v === true ? '' : v);
    });
    (children || []).forEach(function(c) {
        if (c !== null && c !== undefined) node.append(c instanceof Node ? c : document.createTextNode(c));
    });
    return node;
}

function problemUrl(name) { return '/problem/' + encodeURIComponent(name); }
function categoryUrl(name) { return '/category/' + encodeURIComponent(name); }

// ── Bottom bar (left rail on desktop) ──────────────────────────────────────────────
const ICONS = {
    home: 'M3 10.5 12 3l9 7.5V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1z',
    browse: 'M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z',
    history: 'M12 3a9 9 0 1 1-8.5 6H6a7 7 0 1 0 1.4-2.6L10 9H3V2l2.4 2.4A9 9 0 0 1 12 3zm-1 4h2v5.2l3.5 2.1-1 1.7L11 13.3z',
    settings: 'M19.4 13a7.6 7.6 0 0 0 0-2l2-1.6-2-3.4-2.4 1a7.4 7.4 0 0 0-1.7-1L15 3.5h-4l-.4 2.5a7.4 7.4 0 0 0-1.7 1l-2.4-1-2 3.4L6.6 11a7.6 7.6 0 0 0 0 2l-2 1.6 2 3.4 2.4-1a7.4 7.4 0 0 0 1.7 1l.4 2.5h4l.4-2.5a7.4 7.4 0 0 0 1.7-1l2.4 1 2-3.4zM12 15.5A3.5 3.5 0 1 1 12 8.5a3.5 3.5 0 0 1 0 7z',
    mic: 'M12 15a3 3 0 0 0 3-3V6a3 3 0 0 0-6 0v6a3 3 0 0 0 3 3zm5-3a5 5 0 0 1-10 0H5a7 7 0 0 0 6 6.93V21h2v-2.07A7 7 0 0 0 19 12z'
};

function icon(name, size) {
    return '<svg viewBox="0 0 24 24" width="' + size + '" height="' + size + '" aria-hidden="true"><path d="' + ICONS[name] + '"/></svg>';
}

function renderNav() {
    const active = document.body.dataset.nav || '';
    const tab = function(key, href, label) {
        const on = key === active;
        return '<a href="' + href + '" class="tab' + (on ? ' on' : '') + '"' + (on ? ' aria-current="page"' : '') + '>' +
            icon(key, 24) + '<span>' + label + '</span></a>';
    };
    const nav = document.createElement('nav');
    nav.className = 'tabbar';
    nav.setAttribute('aria-label', 'Main');
    nav.innerHTML =
        '<a href="/" class="rail-brand" aria-label="Algorithmic home"><span class="logo">A</span><span>Algorithmic</span></a>' +
        tab('home', '/', 'Home') + tab('browse', '/browse', 'Browse') +
        '<button type="button" id="voiceButton" class="ask" aria-label="Ask by voice">' +
        '<span class="ask-circle">' + icon('mic', 28) + '</span><span class="ask-label">Ask</span><kbd class="ask-kbd">Space</kbd></button>' +
        tab('history', '/history', 'History') + tab('settings', '/settings', 'Settings');
    document.body.appendChild(nav);
}
renderNav();

// ── Catalogue: static JSON, exported from the Java registry ───────────────────────
let catalogPromise = null;
function loadCatalog() {
    if (!catalogPromise) {
        catalogPromise = fetch('/data/catalog.json').then(function(r) {
            if (!r.ok) throw new Error('Could not load the problem list');
            return r.json();
        }).then(function(catalog) {
            catalog.all = [];
            catalog.categories.forEach(function(c) {
                c.problems.forEach(function(p) { p.category = c.name; catalog.all.push(p); });
            });
            return catalog;
        });
    }
    return catalogPromise;
}

function loadProblem(slug) {
    return fetch('/data/problems/' + encodeURIComponent(slug) + '.json').then(function(r) {
        if (!r.ok) throw new Error('Problem not found');
        return r.json();
    });
}

function normalize(text) {
    return (text || '').toLowerCase().replace(/[^a-z0-9]+/g, ' ').trim();
}

// "solve two sum", "show me binary search" → "two sum", "binary search"
function stripIntent(text) {
    return text.replace(/^(please |can you |could you )?(solve|show( me)?|open|find|give me|explain|what is|how (do i|to) (solve|do))( the)? (problem )?/, '').trim();
}

// Instant built-in matches from the catalogue: exact name/alias first, then word overlap
function searchCatalog(catalog, query, limit) {
    const q = stripIntent(normalize(query));
    if (!q) return [];
    const words = q.split(' ').filter(function(w) { return w.length > 1; });
    const scored = [];
    catalog.all.forEach(function(p) {
        const names = [p.name].concat(p.aliases || []).map(normalize);
        let score = 0;
        if (names.indexOf(q) !== -1) score = 1000;
        else {
            const hay = (names.join(' ') + ' ' + (p.keywords || []).join(' ')).split(' ');
            const nameWords = names[0].split(' ');
            words.forEach(function(w) {
                if (nameWords.indexOf(w) !== -1) score += 3;
                else if (hay.indexOf(w) !== -1) score += 1;
            });
            if (words.length && nameWords.every(function(w) { return words.indexOf(w) !== -1; })) score += 5;
        }
        if (score > 0) scored.push({ problem: p, score: score });
    });
    scored.sort(function(a, b) { return b.score - a.score; });
    return scored.slice(0, limit || 5).map(function(s) { return s.problem; });
}

function exactMatch(catalog, query) {
    const q = stripIntent(normalize(query));
    const hit = catalog.all.find(function(p) {
        return [p.name].concat(p.aliases || []).map(normalize).indexOf(q) !== -1;
    });
    if (hit) return { url: problemUrl(hit.name) };
    const cat = catalog.categories.find(function(c) { return normalize(c.name) === q; });
    return cat ? { url: categoryUrl(cat.name) } : null;
}

// ── Java API on Render (may be asleep: the first call can take up to a minute) ───────
const Server = {
    awake: false,
    warm() {
        const started = Date.now();
        return fetch(API + '/api/health', { cache: 'no-store' })
            .then(function(r) { Server.awake = r.ok; return r.ok; })
            .catch(function() { return false; })
            .finally(function() { Server.lastWarmMs = Date.now() - started; });
    }
};

// fetch() against the API that tells the page when the server seems to be waking up
async function api(path, options, onSlow) {
    let slowTimer = null;
    if (onSlow && !Server.awake) slowTimer = setTimeout(onSlow, 2500);
    try {
        const res = await fetch(API + path, options);
        Server.awake = true;
        return res;
    } finally {
        clearTimeout(slowTimer);
    }
}

function postJson(path, body, onSlow) {
    return api(path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    }, onSlow);
}

// ── Search and voice land here: a built-in page when it matches, otherwise a generated answer ──
async function openSolution(question) {
    question = (question || '').trim();
    if (!question) return;
    showLoading('Finding the solution…');
    try {
        const local = exactMatch(await loadCatalog(), question);
        if (local) { window.location.href = local.url; return; }
    } catch (e) { /* catalogue unavailable: ask the server */ }
    try {
        const res = await postJson('/api/resolve', { query: question },
            function() { showLoading('Waking up the server… this can take up to a minute the first time.'); });
        if (res.ok) {
            const target = await res.json();
            if (target.type === 'problem') { window.location.href = problemUrl(target.name); return; }
            if (target.type === 'category') { window.location.href = categoryUrl(target.name); return; }
        }
    } catch (err) {
        console.error('Resolve failed', err);
    }
    // sessionStorage rather than ?q= alone: spoken questions can be long
    try { sessionStorage.setItem('solutionQuestion', question); } catch (e) { /* storage unavailable */ }
    window.location.href = '/solution?q=' + encodeURIComponent(question);
}

function showLoading(message) {
    let overlay = document.getElementById('loadingOverlay');
    if (!overlay) {
        overlay = el('div.loading-overlay', { id: 'loadingOverlay', role: 'status' }, [el('div.spinner'), el('p')]);
        document.body.appendChild(overlay);
    }
    overlay.querySelector('p').textContent = message || 'Loading…';
    overlay.hidden = false;
}

function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.hidden = true;
}

window.addEventListener('pageshow', function(e) { if (e.persisted) hideLoading(); });

function search() {
    const input = document.getElementById('searchInput');
    if (input) openSolution(input.value);
}

// ── Local store: history and progress live in this browser only (no login) ──────────
const Store = {
    get(key, fallback) {
        try {
            const raw = localStorage.getItem(key);
            return raw ? JSON.parse(raw) : fallback;
        } catch (e) {
            return fallback;
        }
    },
    set(key, value) {
        try { localStorage.setItem(key, JSON.stringify(value)); } catch (e) { /* storage unavailable */ }
    },
    addHistory(entry) {
        const list = Store.get('algo.history', []).filter(function(e) { return e.url !== entry.url; });
        entry.ts = Date.now();
        list.unshift(entry);
        Store.set('algo.history', list.slice(0, 60));
    },
    history() { return Store.get('algo.history', []); },
    markRan(problemName) {
        const ran = Store.get('algo.ran', []);
        if (ran.indexOf(problemName) === -1) {
            ran.push(problemName);
            Store.set('algo.ran', ran);
        }
    },
    ran() { return Store.get('algo.ran', []); },
    setting(name, fallback) {
        const s = Store.get('algo.settings', {});
        return Object.prototype.hasOwnProperty.call(s, name) ? s[name] : fallback;
    },
    setSetting(name, value) {
        const s = Store.get('algo.settings', {});
        s[name] = value;
        Store.set('algo.settings', s);
    }
};

function timeAgo(ts) {
    const mins = Math.round((Date.now() - ts) / 60000);
    if (mins < 1) return 'just now';
    if (mins < 60) return mins + ' min ago';
    const hours = Math.round(mins / 60);
    if (hours < 24) return hours + ' h ago';
    const days = Math.round(hours / 24);
    return days === 1 ? 'yesterday' : days + ' days ago';
}

function plural(n, word) { return n + ' ' + word + (n === 1 ? '' : 's'); }

// Category tiles with progress from this browser's "ran" list (Home and Browse)
function renderTiles(grid, categories) {
    const ran = Store.ran();
    grid.replaceChildren();
    categories.forEach(function(c) {
        const done = c.problems.filter(function(p) { return ran.indexOf(p.name) !== -1; }).length;
        const bar = el('i');
        bar.style.width = (c.problems.length ? Math.round(100 * done / c.problems.length) : 0) + '%';
        grid.appendChild(el('a.tile', { href: categoryUrl(c.name) }, [
            el('span.ico', { 'aria-hidden': 'true', text: c.name.charAt(0) }),
            el('span.t', { text: c.name }),
            el('span.st', { text: plural(c.problems.length, 'problem') + (done ? ' · ' + done + ' ran' : '') }),
            el('span.bar', {}, [bar])
        ]));
    });
}

// ── Tabs: buttons with data-tab, panels with id "panel-<name>". The tab is kept in ?tab= ──
function initTabs(root, onChange) {
    const buttons = Array.from(root.querySelectorAll('[role="tab"]'));
    function select(name, push) {
        if (!buttons.some(function(b) { return b.dataset.tab === name; })) return;
        buttons.forEach(function(b) {
            const on = b.dataset.tab === name;
            b.setAttribute('aria-selected', on ? 'true' : 'false');
            b.tabIndex = on ? 0 : -1;
            const panel = document.getElementById('panel-' + b.dataset.tab);
            if (panel) panel.classList.toggle('on', on);
        });
        if (push) {
            const url = new URL(window.location.href);
            url.searchParams.set('tab', name);
            history.replaceState(null, '', url);
        }
        if (onChange) onChange(name);
    }
    buttons.forEach(function(b) {
        b.addEventListener('click', function() { select(b.dataset.tab, true); });
        b.addEventListener('keydown', function(e) {
            const step = e.key === 'ArrowRight' ? 1 : e.key === 'ArrowLeft' ? -1 : 0;
            if (!step) return;
            const visible = buttons.filter(function(x) { return x.offsetParent !== null; });
            const next = visible[(visible.indexOf(b) + step + visible.length) % visible.length];
            next.focus();
            select(next.dataset.tab, true);
        });
    });
    if (root.classList.contains('scroll')) {
        const update = function() {
            root.classList.toggle('at-end', root.scrollLeft + root.clientWidth >= root.scrollWidth - 4);
        };
        root.addEventListener('scroll', update);
        window.addEventListener('resize', update);
        update();
    }
    return select;
}

// ── Copy and share ───────────────────────────────────────────────────────────────
function copyText(text) {
    if (navigator.clipboard && window.isSecureContext) return navigator.clipboard.writeText(text);
    const area = el('textarea', { readonly: true, style: 'position:fixed;opacity:0' });
    area.value = text;
    document.body.appendChild(area);
    area.select();
    try { document.execCommand('copy'); } finally { area.remove(); }
    return Promise.resolve();
}

document.addEventListener('click', function(e) {
    const button = e.target.closest('[data-copy]');
    if (button) {
        const source = document.getElementById(button.dataset.copy);
        if (!source) return;
        const label = button.textContent;
        copyText(source.textContent).then(function() {
            button.textContent = 'Copied';
            button.classList.add('copied');
            setTimeout(function() { button.textContent = label; button.classList.remove('copied'); }, 1500);
        });
        return;
    }
    const share = e.target.closest('[data-share]');
    if (share) {
        const data = { title: document.title, url: window.location.href };
        if (navigator.share) navigator.share(data).catch(function() {});
        else copyText(data.url).then(function() { share.title = 'Link copied'; share.setAttribute('aria-label', 'Link copied'); });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') { e.preventDefault(); search(); }
        });
    }
    document.querySelectorAll('[data-search-go]').forEach(function(b) { b.addEventListener('click', search); });
});

// Start waking the API as soon as any page opens, so Run/Ask are ready by the time they're used
Server.warm();

if ('serviceWorker' in navigator) {
    window.addEventListener('load', function() {
        navigator.serviceWorker.register('/sw.js').catch(function() {});
    });
}

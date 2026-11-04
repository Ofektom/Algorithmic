// Shared behaviour for every page: search, loading overlay, copy buttons, tabs and the local
// history/progress store. Voice input (voice.js) goes through openSolution() too, so typing and
// speaking always land on the same result.

// Open the solution for a typed or spoken question: the built-in problem (or category) when the
// question confidently matches one, otherwise the generated-solution page.
async function openSolution(question) {
    question = (question || '').trim();
    if (!question) return;
    showLoading();
    try {
        const res = await fetch('/api/resolve', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ query: question })
        });
        if (res.ok) {
            const target = await res.json();
            if (target.type === 'problem' || target.type === 'category') {
                window.location.href = target.url;
                return;
            }
        }
    } catch (err) {
        console.error('Resolve failed', err);
    }
    // sessionStorage rather than ?q=: spoken questions can be long
    try {
        sessionStorage.setItem('solutionQuestion', question);
        window.location.href = '/solution';
    } catch (err) {
        window.location.href = '/solution?q=' + encodeURIComponent(question);
    }
}

function showLoading(message) {
    let overlay = document.getElementById('loadingOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.className = 'loading-overlay';
        overlay.setAttribute('role', 'status');
        overlay.innerHTML = '<div class="spinner"></div><p></p>';
        document.body.appendChild(overlay);
    }
    overlay.querySelector('p').textContent = message || 'Finding the solution…';
    overlay.hidden = false;
}

function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.hidden = true;
}

// Returning via the back button restores the page from cache with the overlay still up
window.addEventListener('pageshow', function(e) {
    if (e.persisted) hideLoading();
});

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
    // One entry per page, newest first
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

// ── Tabs: buttons with data-tab, panels with id "panel-<name>". The tab is kept in ?tab= ──
function initTabs(root, onChange) {
    const buttons = Array.from(root.querySelectorAll('[role="tab"]'));
    function select(name, push) {
        let found = false;
        buttons.forEach(function(b) {
            const on = b.dataset.tab === name;
            if (on) found = true;
            b.setAttribute('aria-selected', on ? 'true' : 'false');
            b.tabIndex = on ? 0 : -1;
        });
        if (!found) return;
        buttons.forEach(function(b) {
            const panel = document.getElementById('panel-' + b.dataset.tab);
            if (panel) panel.classList.toggle('on', b.dataset.tab === name);
        });
        if (push) {
            const url = new URL(window.location.href);
            url.searchParams.set('tab', name);
            history.replaceState(null, '', url);
        }
        if (onChange) onChange(name);
    }
    buttons.forEach(function(b, i) {
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
    // Scrollable tab rows fade on the right until the end is reached
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

// ── Copy buttons: data-copy="<id of element whose text to copy>" ──────────────────────
function copyText(text) {
    if (navigator.clipboard && window.isSecureContext) {
        return navigator.clipboard.writeText(text);
    }
    // Older browsers / plain-http pages: hidden textarea + execCommand
    const area = document.createElement('textarea');
    area.value = text;
    area.setAttribute('readonly', '');
    area.style.position = 'fixed';
    area.style.opacity = '0';
    document.body.appendChild(area);
    area.select();
    try { document.execCommand('copy'); } finally { area.remove(); }
    return Promise.resolve();
}

document.addEventListener('click', function(e) {
    const button = e.target.closest('[data-copy]');
    if (!button) return;
    const source = document.getElementById(button.dataset.copy);
    if (!source) return;
    const label = button.textContent;
    copyText(source.textContent).then(function() {
        button.textContent = 'Copied';
        button.classList.add('copied');
        setTimeout(function() {
            button.textContent = label;
            button.classList.remove('copied');
        }, 1500);
    });
});

// Share: the native sheet on phones, otherwise copy the link
document.addEventListener('click', function(e) {
    const button = e.target.closest('[data-share]');
    if (!button) return;
    const data = { title: document.title, url: window.location.href };
    if (navigator.share) {
        navigator.share(data).catch(function() {});
    } else {
        copyText(data.url).then(function() {
            button.setAttribute('aria-label', 'Link copied');
            button.title = 'Link copied';
        });
    }
});

// Search box: Enter or the Go button
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                search();
            }
        });
    }
    document.querySelectorAll('[data-search-go]').forEach(function(b) {
        b.addEventListener('click', search);
    });
});

// Service Worker registration for PWA
if ('serviceWorker' in navigator) {
    window.addEventListener('load', function() {
        navigator.serviceWorker.register('/sw.js').catch(function() {});
    });
}

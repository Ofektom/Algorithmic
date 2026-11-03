// Open the solution for a typed or spoken question. Search box and voice input share this, so both
// show the same "Loading…" and land on the same result: a built-in problem (or category) when the
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

function showLoading() {
    let overlay = document.getElementById('loadingOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.className = 'loading-overlay';
        overlay.setAttribute('role', 'status');
        overlay.innerHTML = '<div class="loading-spinner"></div><p>Loading…</p>';
        document.body.appendChild(overlay);
    }
    overlay.hidden = false;
}

// Returning via the back button restores this page from cache with the overlay still up
window.addEventListener('pageshow', function(e) {
    const overlay = document.getElementById('loadingOverlay');
    if (e.persisted && overlay) overlay.hidden = true;
});

function search() {
    const input = document.getElementById('searchInput');
    if (input) openSolution(input.value);
}

// Copy button on every code block (solution page fills its code later, so copy reads it on tap)
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.code-block').forEach(function(block) {
        const code = block.querySelector('code');
        if (!code) return;
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'copy-btn';
        button.textContent = 'Copy';
        button.setAttribute('aria-label', 'Copy code');
        button.addEventListener('click', function() {
            copyText(code.textContent).then(function() {
                button.textContent = 'Copied';
                button.classList.add('copied');
                setTimeout(function() {
                    button.textContent = 'Copy';
                    button.classList.remove('copied');
                }, 1500);
            });
        });
        block.appendChild(button);
    });
});

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

// Enter key support for search
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                search();
            }
        });
    }
});

// Service Worker registration for PWA
if ('serviceWorker' in navigator) {
    window.addEventListener('load', function() {
        navigator.serviceWorker.register('/sw.js')
            .then(function(registration) {
                console.log('ServiceWorker registration successful');
            })
            .catch(function(err) {
                console.log('ServiceWorker registration failed');
            });
    });
}


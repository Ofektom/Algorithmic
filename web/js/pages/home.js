// Home: recent searches, "Continue", and category tiles with progress — all without the server
(async function() {
    const history = Store.history();
    const chips = document.getElementById('recentChips');
    history.slice(0, 4).forEach(function(entry) {
        chips.appendChild(el('a.chip', { href: entry.url, text: entry.title }));
    });
    chips.hidden = history.length === 0;

    if (history.length) {
        const last = history[0];
        document.getElementById('continueLink').href = last.url;
        document.getElementById('continueTitle').textContent = last.title;
        document.getElementById('continueMeta').textContent =
            (last.type === 'generated' ? 'Generated solution' : (last.category || 'Built-in')) + ' · ' + timeAgo(last.ts);
        document.getElementById('continueSection').hidden = false;
    }

    try {
        const catalog = await loadCatalog();
        document.getElementById('seeAll').textContent = 'See all ' + catalog.categories.length;
        renderTiles(document.getElementById('categoryGrid'), catalog.categories.slice(0, 8));
    } catch (e) {
        document.getElementById('categoryGrid').replaceChildren(el('p.muted', { text: "Couldn't load the categories. Check your connection and refresh." }));
    }
})();


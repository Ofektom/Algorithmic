// Browse: every category, plus instant matches while typing (from the static catalogue)
(async function() {
    let catalog;
    try {
        catalog = await loadCatalog();
    } catch (e) {
        document.getElementById('categoryGrid').replaceChildren(el('p.muted', { text: "Couldn't load the categories. Check your connection and refresh." }));
        return;
    }
    document.getElementById('categoryCount').textContent = catalog.categories.length + (catalog.categories.length === 1 ? ' category' : ' categories');
    renderTiles(document.getElementById('categoryGrid'), catalog.categories);

    const input = document.getElementById('searchInput');
    const results = document.getElementById('liveResults');
    input.addEventListener('input', function() {
        const hits = input.value.trim().length >= 2 ? searchCatalog(catalog, input.value, 6) : [];
        results.replaceChildren.apply(results, hits.map(problemRow));
        results.hidden = hits.length === 0;
    });
})();


function problemRow(p) {
    return el('a.row-link', { href: problemUrl(p.name) }, [
        el('div.grow', {}, [el('div.t', { text: p.name }), el('div.st', { text: p.category })]),
        el('span.chev', { text: '›' })
    ]);
}

// Category: problems in one category with search, progress and filters
(async function() {
    const name = decodeURIComponent(location.pathname.replace(/^\/category\/?/, ''));
    const $ = function(id) { return document.getElementById(id); };
    let catalog;
    try {
        catalog = await loadCatalog();
    } catch (e) {
        $('problemList').replaceChildren(el('p.muted', { text: "Couldn't load this category. Check your connection and refresh." }));
        return;
    }
    const category = catalog.categories.find(function(c) { return c.name === name; });
    $('title').textContent = category ? category.name : name || 'Category';
    document.title = ($('title').textContent) + ' · Algorithmic';
    if (!category) {
        $('problemList').replaceChildren();
        $('notFound').hidden = false;
        return;
    }

    const ran = Store.ran();
    const list = $('problemList');
    const rows = category.problems.map(function(p) {
        const hasRun = ran.indexOf(p.name) !== -1;
        const row = el('a.row-link', { href: problemUrl(p.name) }, [
            el('div.grow', {}, [el('div.t', { text: p.name }), el('div.st', { text: p.shortDescription || '' })]),
            el('div.end', {}, [
                el('span.badge', { text: plural(p.methodCount, 'method') }),
                hasRun ? el('span.ran', { text: '✓ Ran' }) : null
            ])
        ]);
        row.dataset.name = p.name;
        row.dataset.search = (p.name + ' ' + (p.shortDescription || '') + ' ' + (p.aliases || []).join(' ')).toLowerCase();
        row.dataset.ran = hasRun ? '1' : '';
        return row;
    });
    list.replaceChildren.apply(list, rows);

    const done = rows.filter(function(r) { return r.dataset.ran; }).length;
    $('progressText').textContent = plural(rows.length, 'problem') + ' · ' + done + ' ran';
    $('progressBar').style.width = (rows.length ? Math.round(100 * done / rows.length) : 0) + '%';
    $('filterInput').placeholder = 'Search ' + category.name + '…';
    $('tools').hidden = false;

    let filter = 'all';
    function apply() {
        const q = $('filterInput').value.trim().toLowerCase();
        const ordered = filter === 'az'
            ? rows.slice().sort(function(a, b) { return a.dataset.name.localeCompare(b.dataset.name); })
            : rows;
        let shown = 0;
        ordered.forEach(function(row) {
            const ok = (!q || row.dataset.search.indexOf(q) !== -1)
                && (filter !== 'ran' || row.dataset.ran) && (filter !== 'todo' || !row.dataset.ran);
            row.hidden = !ok;
            if (ok) shown++;
            list.appendChild(row);
        });
        $('noMatches').hidden = shown > 0;
    }
    document.querySelectorAll('[data-filter]').forEach(function(chip) {
        chip.addEventListener('click', function() {
            filter = chip.dataset.filter;
            document.querySelectorAll('[data-filter]').forEach(function(c) {
                c.setAttribute('aria-pressed', c === chip ? 'true' : 'false');
            });
            apply();
        });
    });
    $('filterInput').addEventListener('input', apply);
})();

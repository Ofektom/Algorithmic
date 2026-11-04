(function() {
    const list = document.getElementById('historyList');
    function render() {
        const entries = Store.history();
        const ran = Store.ran();
        list.replaceChildren();
        entries.forEach(function(e) {
            const generated = e.type === 'generated';
            list.appendChild(el('a.row-link', { href: e.url }, [
                el('div.grow', {}, [
                    el('div.t', { text: e.title }),
                    el('div.st', { text: (generated ? 'Your question' : e.category || '') + ' · ' + timeAgo(e.ts) })
                ]),
                el('div.end', {}, [
                    el(generated ? 'span.badge.warn' : 'span.badge.ok', { text: generated ? 'Generated' : 'Built-in' }),
                    !generated && ran.indexOf(e.title) !== -1 ? el('span.ran', { text: '✓ Ran' }) : null
                ])
            ]));
        });
        document.getElementById('historyEmpty').hidden = entries.length > 0;
        document.getElementById('clearHistory').hidden = entries.length === 0;
    }
    document.getElementById('clearHistory').addEventListener('click', function() {
        Store.set('algo.history', []);
        render();
    });
    render();
})();

// Generated answer from the Java API (Groq / Claude Code). Built-in look-alikes come from the
// static catalogue, so they show even while the server is still waking.
(function() {
    let stored = '';
    try { stored = sessionStorage.getItem('solutionQuestion') || ''; } catch (e) { /* storage unavailable */ }
    const params = new URLSearchParams(location.search);
    const question = (params.get('q') || stored).trim();
    const $ = function(id) { return document.getElementById(id); };
    if (!question) { location.replace('/'); return; }

    // Keep the question in the URL so refresh, share and History reopen the same answer
    if (!params.get('q')) {
        const url = new URL(location.href);
        url.searchParams.set('q', question);
        history.replaceState(null, '', url);
    }
    if (document.referrer && new URL(document.referrer).origin === location.origin) {
        $('backButton').addEventListener('click', function(e) { e.preventDefault(); history.back(); });
    }
    const title = question.charAt(0).toUpperCase() + question.slice(1);
    $('question').textContent = title;
    document.title = title + ' · Algorithmic';

    const tabsEl = document.querySelector('.tabs');
    const selectTab = initTabs(tabsEl);

    function fill(id, value, tab) {
        const text = (value || '').trim();
        $(id).textContent = text;
        tabsEl.querySelector('[data-tab="' + tab + '"]').hidden = !text;
        return Boolean(text);
    }

    loadCatalog().then(function(catalog) {
        const found = searchCatalog(catalog, question, 3);
        found.forEach(function(p) {
            $('matchList').appendChild(el('a.row-link', { href: problemUrl(p.name) }, [
                el('div.grow', {}, [el('div.t', { text: p.name }), el('div.st', { text: p.category })]),
                el('span.badge.ok', { text: 'Built-in · runnable' })
            ]));
        });
        $('builtInMatches').hidden = found.length === 0;
    }).catch(function() { /* optional */ });

    async function loadSolution() {
        $('error').hidden = true;
        $('answer').hidden = true;
        tabsEl.hidden = false;
        $('loading').hidden = false;
        $('loadingText').textContent = 'Writing a solution… this can take a few seconds.';
        try {
            const res = await postJson('/api/ai/solution-with-inputs', { problemStatement: question, language: 'Java' },
                function() { $('loadingText').textContent = 'Waking up the server… the first answer after a quiet spell can take up to a minute.'; });
            const data = await res.json().catch(function() { return {}; });
            if (!res.ok || data.error) throw new Error(data.error || "Couldn't load a solution right now.");

            const hasCode = fill('code', data.code, 'solution');
            tabsEl.querySelector('[data-tab="solution"]').hidden = false;
            $('code').hidden = !hasCode;
            $('copyCode').hidden = !hasCode;
            $('noCode').hidden = hasCode;
            fill('explanation', data.explanation, 'explanation');
            fill('approach', data.solution, 'approach');
            fill('complexity', data.complexity, 'complexity');
            const problemText = (data.problem || '').trim();
            $('problemText').textContent = problemText;
            $('problemText').hidden = !problemText;
            $('problemMissing').hidden = Boolean(problemText);

            $('answer').hidden = false;
            selectTab('solution', false);
            Store.addHistory({ type: 'generated', title: title, url: '/solution?q=' + encodeURIComponent(question) });
        } catch (err) {
            tabsEl.hidden = true;
            $('errorMessage').textContent = err instanceof TypeError
                ? "Couldn't reach the server. Check your connection and try again." : err.message;
            $('error').hidden = false;
        } finally {
            $('loading').hidden = true;
        }
    }

    $('retry').addEventListener('click', loadSolution);
    loadSolution();
})();

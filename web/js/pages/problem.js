// Built-in problem: statement and code come from static JSON (instant); Run and Compare call the
// Java API on Render, which may need up to a minute to wake.
(async function() {
    const $ = function(id) { return document.getElementById(id); };
    const desktop = window.matchMedia('(min-width: 1024px)');
    const name = decodeURIComponent(location.pathname.replace(/^\/problem\/?/, ''));
    const params = new URLSearchParams(location.search);

    let problem;
    try {
        const catalog = await loadCatalog();
        const summary = catalog.all.find(function(p) { return p.name === name; })
            || catalog.all.find(function(p) { return normalize(p.name) === normalize(name); });
        if (!summary) throw new Error('not found');
        problem = await loadProblem(summary.slug);
    } catch (e) {
        $('title').textContent = name || 'Problem';
        $('content').hidden = true;
        $('notFound').hidden = false;
        document.querySelector('.source-row').hidden = true;
        return;
    }

    const methods = problem.methods || [];
    const labels = {};
    methods.forEach(function(m) { labels[m.name] = m.label || m.name; });
    const examples = problem.exampleInputs;
    const hasExamples = Array.isArray(examples) && examples.some(function(v) { return v !== null && v !== undefined; });
    let selected = methods.some(function(m) { return m.name === params.get('method'); }) ? params.get('method') : (methods[0] || {}).name;
    let lastInputs = null;

    // ── Header and Problem tab ──
    document.title = problem.name + ' · Algorithmic';
    $('title').textContent = problem.name;
    $('crumb').textContent = problem.category;
    $('backLink').href = categoryUrl(problem.category);
    $('backLink').setAttribute('aria-label', 'Back to ' + problem.category);
    $('description').textContent = problem.description || '';
    if (problem.related && problem.related.length) {
        $('relatedTitle').textContent = 'More in ' + problem.category;
        problem.related.forEach(function(r) {
            $('relatedList').appendChild(el('a.row-link', { href: problemUrl(r.name) }, [
                el('div.grow', {}, [el('div.t', { text: r.name }), el('div.st', { text: r.shortDescription || '' })]),
                el('span.chev', { text: '›' })
            ]));
        });
        $('relatedSection').hidden = false;
    }
    Store.addHistory({ type: 'problem', title: problem.name, category: problem.category, url: problemUrl(problem.name) });

    // ── Explanation and Complexity tabs (written, stored with the problem) ──
    const ex = problem.explanation || null;
    const exMethods = (ex && ex.methods) || {};
    function javaLabel(v) { return !v || v === 'any' ? 'Any Java' : 'Java ' + v + '+'; }
    if (ex) {
        document.querySelector('[data-tab="explanation"]').hidden = false;
        document.querySelector('[data-tab="complexity"]').hidden = false;
        $('exIdea').textContent = ex.idea || '';
        $('exAnalogy').textContent = ex.analogy || '';
        $('exAnalogy').hidden = !ex.analogy;
        $('exPattern').textContent = ex.pattern || '';
        methods.forEach(function(m) {
            const e = exMethods[m.name];
            if (!e) return;
            const head = el('div.ex-head', {}, [
                el('span.t', { text: labels[m.name] }),
                el('span.badge.java', { text: javaLabel(e.java) }),
                ex.recommended === m.name ? el('span.badge.ok', { text: 'Best to present' }) : null,
                el('span.grow'),
                el('span.small.muted.mono', { text: e.time + ' · ' + e.space })
            ]);
            const steps = el('ol.steps', {}, (e.steps || []).map(function(t) { return el('li', { text: t }); }));
            const view = el('button.link-btn', { type: 'button', text: 'See the code →',
                on: { click: function() { showMethod(m.name); selectTab('solution', true); window.scrollTo({ top: 0 }); } } });
            $('exMethods').appendChild(el('div.card.ex-method', {}, [head, el('p.small', { text: e.summary }), steps, view]));

            const best = ex.recommended === m.name;
            $('cxRows').appendChild(el('div.card.cx-row' + (best ? '.best' : ''), {}, [
                el('div.ex-head', {}, [el('span.t', { text: labels[m.name] }), best ? el('span.badge.ok', { text: 'Best to present' }) : null]),
                el('div.cx-grid', {}, [
                    el('div', {}, [el('p.eyebrow', { text: 'Time' }), el('p.cx-o', { text: e.time }), el('p.small.muted', { text: e.timeWhy || '' })]),
                    el('div', {}, [el('p.eyebrow', { text: 'Space' }), el('p.cx-o', { text: e.space }), el('p.small.muted', { text: e.spaceWhy || '' })])
                ])
            ]));
        });
        [['pitfalls', 'exPitfalls'], ['interview', 'exInterview']].forEach(function(pair) {
            const items = ex[pair[0]] || [];
            items.forEach(function(t) { $(pair[1]).appendChild(el('li', { text: t })); });
            $(pair[1] + 'Section').hidden = items.length === 0;
        });
        $('cxIntro').textContent = ex.recommended && labels[ex.recommended]
            ? 'In an interview, lead with ' + labels[ex.recommended] + ', and mention the others as trade-offs.'
            : 'How each approach scales.';
    }

    // ── Solution tab: switching method swaps the code in place ──
    function showMethod(methodName) {
        selected = methodName;
        const m = methods.find(function(x) { return x.name === methodName; }) || { name: methodName, code: '' };
        $('methodBadge').textContent = labels[m.name];
        $('methodName').textContent = m.name + '()';
        const e = exMethods[m.name];
        $('methodJava').textContent = e ? javaLabel(e.java) : '';
        $('methodJava').hidden = !e;
        $('methodSummary').textContent = e ? e.summary + ' ' + e.time + ' time, ' + e.space + ' space.' : '';
        $('methodSummary').hidden = !e;
        const hasCode = Boolean(m.code && m.code.trim());
        $('methodCode').textContent = m.code || '';
        $('methodCode').hidden = !hasCode;
        $('copyCode').hidden = !hasCode;
        $('noCode').hidden = hasCode;
        $('methodTabs').querySelectorAll('button').forEach(function(b) {
            b.setAttribute('aria-pressed', b.dataset.method === methodName ? 'true' : 'false');
        });
        if ($('methodSelect').value !== methodName) $('methodSelect').value = methodName;
        const url = new URL(location.href);
        url.searchParams.set('method', methodName);
        history.replaceState(null, '', url);
    }
    methods.forEach(function(m) {
        $('methodTabs').appendChild(el('button', { type: 'button', 'data-method': m.name, text: labels[m.name],
            on: { click: function() { showMethod(m.name); } } }));
        $('methodSelect').appendChild(el('option', { value: m.name, text: labels[m.name] }));
    });
    $('methodTabs').hidden = methods.length < 2;
    $('methodRow').hidden = methods.length < 2;
    $('resultActions').hidden = methods.length < 2;
    $('methodSelect').addEventListener('change', function() { showMethod(this.value); });
    showMethod(selected);

    // ── Tabs: Solution by default; on desktop Solution and Run share one view ──
    function onTab(tab) {
        $('split').classList.toggle('on', desktop.matches && (tab === 'solution' || tab === 'run'));
        $('runAction').hidden = desktop.matches || tab !== 'solution';
        if (tab === 'problem') loadExampleOutput();
    }
    const selectTab = initTabs(document.querySelector('.tabs'), onTab);
    let start = params.get('tab') || 'solution';
    if (desktop.matches && start === 'run') start = 'solution';
    selectTab(start, false);
    desktop.addEventListener('change', function() {
        const current = document.querySelector('.tabs [aria-selected="true"]').dataset.tab;
        selectTab(desktop.matches && current === 'run' ? 'solution' : current, false);
    });
    $('goRun').addEventListener('click', function() { selectTab('run', true); window.scrollTo({ top: 0 }); });

    // ── Inputs ──
    function formatValue(value, type) {
        if (Array.isArray(value) && /^(int|long|String|char)\[\]$/.test(type)) return value.join(',');
        if (value !== null && typeof value === 'object') return JSON.stringify(value);
        return String(value);
    }

    function parseInput(input, type) {
        input = input.trim();
        if (type === 'int[]' || type === 'long[]') {
            return input.replace(/^\[|\]$/g, '').split(/[,\s]+/).filter(Boolean).map(function(x) {
                const num = Number(x);
                if (!Number.isInteger(num)) throw new Error('"' + x + '" is not a whole number');
                return num;
            });
        } else if (type === 'String[]') {
            return input.replace(/^\[|\]$/g, '').split(',').map(function(s) { return s.trim().replace(/^["']|["']$/g, ''); });
        } else if (type === 'char[]') {
            if (input.includes(',')) return input.split(',').map(function(c) { return c.trim().charAt(0); });
            return input.split('').filter(function(c) { return c.trim().length > 0; });
        } else if (type === 'int' || type === 'long') {
            const num = Number(input);
            if (!Number.isInteger(num)) throw new Error('"' + input + '" is not a whole number');
            return num;
        } else if (type === 'double') {
            const num = parseFloat(input);
            if (isNaN(num)) throw new Error('"' + input + '" is not a number');
            return num;
        } else if (type === 'boolean') {
            if (!/^(true|false)$/i.test(input)) throw new Error('Use true or false');
            return input.toLowerCase() === 'true';
        } else if (type === 'String') {
            return input.replace(/^["']|["']$/g, '');
        } else if (type.indexOf('[]') !== -1 || /^[\[{]/.test(input)) {
            try { return JSON.parse(input); } catch (e) { throw new Error('Expected JSON, e.g. [ [1,2], [3,4] ]'); }
        }
        return input;
    }

    (problem.paramNames || []).forEach(function(paramName, i) {
        const type = problem.paramTypes[i];
        const input = el('input', { type: 'text', id: 'input_' + i, autocomplete: 'off', autocapitalize: 'off',
            autocorrect: 'off', spellcheck: 'false', enterkeyhint: 'go' });
        const hint = el('p.hint');
        if (hasExamples && examples[i] !== null && examples[i] !== undefined) {
            const text = formatValue(examples[i], type);
            hint.textContent = 'Example: ' + text;
            input.placeholder = 'e.g. ' + text;
        }
        input.addEventListener('input', clearError);
        input.addEventListener('keydown', function(e) { if (e.key === 'Enter') { e.preventDefault(); run(); } });
        $('fields').appendChild(el('div.field', {}, [
            el('label', { for: 'input_' + i }, [el('span', { text: paramName }), el('span.type', { text: type })]),
            input, hint
        ]));
    });
    $('fillExample').hidden = !hasExamples;
    $('fillExample').addEventListener('click', function() {
        problem.paramTypes.forEach(function(type, i) {
            if (examples[i] !== null && examples[i] !== undefined) $('input_' + i).value = formatValue(examples[i], type);
        });
        clearError();
    });
    $('clearInputs').addEventListener('click', function() {
        problem.paramTypes.forEach(function(t, i) { $('input_' + i).value = ''; });
        clearError();
    });

    function showError(message, index) {
        $('formError').textContent = message;
        $('formError').hidden = false;
        if (index !== undefined) {
            $('input_' + index).setAttribute('aria-invalid', 'true');
            $('input_' + index).focus();
        }
    }
    function clearError() {
        $('formError').hidden = true;
        document.querySelectorAll('#fields input').forEach(function(i) { i.removeAttribute('aria-invalid'); });
    }

    function readInputs() {
        const inputs = [];
        for (let i = 0; i < problem.paramTypes.length; i++) {
            const value = $('input_' + i).value.trim();
            if (!value) {
                showError('Fill in ' + problem.paramNames[i] + (hasExamples ? ' (or tap “Fill example”).' : '.'), i);
                return null;
            }
            try {
                inputs.push(parseInput(value, problem.paramTypes[i]));
            } catch (err) {
                showError(problem.paramNames[i] + ': ' + err.message, i);
                return null;
            }
        }
        return inputs;
    }

    function describe(inputs) {
        return problem.paramNames.map(function(n, i) { return n + ' = ' + formatValue(inputs[i], problem.paramTypes[i]); }).join(' · ');
    }

    // ── Running on the server ──
    async function execute(method, inputs) {
        const res = await postJson('/api/execute', { className: problem.className, methodName: method, inputs: inputs },
            function() { $('wakeNote').hidden = false; });
        $('wakeNote').hidden = true;
        if (!res.ok) throw new Error('The server returned ' + res.status);
        return res.json();
    }

    function formatTime(ms) {
        if (typeof ms !== 'number') return '';
        return ms < 1 ? ms.toFixed(3) + ' ms' : ms.toFixed(2) + ' ms';
    }

    function networkMessage(err) {
        return err instanceof TypeError ? "Couldn't reach the server. Check your connection and try again." : err.message;
    }

    async function run() {
        clearError();
        const inputs = readInputs();
        if (!inputs) return;
        lastInputs = inputs;
        const method = $('methodSelect').value || selected;
        $('runButton').disabled = true;
        $('runButton').textContent = 'Running…';
        $('compare').hidden = true;
        try {
            const data = await execute(method, inputs);
            $('result').hidden = false;
            if (data.success) {
                Store.markRan(problem.name);
                $('resultStatus').className = 'badge ok';
                $('resultStatus').textContent = '✓ Success';
                $('resultValue').textContent = data.result;
                $('resultValue').hidden = false;
                $('copyResult').hidden = false;
                $('resultError').hidden = true;
            } else {
                $('resultStatus').className = 'badge err';
                $('resultStatus').textContent = 'Error';
                $('resultValue').hidden = true;
                $('copyResult').hidden = true;
                $('resultError').textContent = data.error || 'Something went wrong.';
                $('resultError').hidden = false;
            }
            $('resultMeta').textContent = [formatTime(data.executionTime), labels[method]].filter(Boolean).join(' · ');
            $('result').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        } catch (err) {
            $('wakeNote').hidden = true;
            showError(networkMessage(err));
        } finally {
            $('runButton').disabled = false;
            $('runButton').textContent = '▶ Run';
        }
    }
    $('runButton').addEventListener('click', run);
    $('runAgain').addEventListener('click', run);

    $('compareButton').addEventListener('click', async function() {
        if (!lastInputs) return;
        const rows = $('compareRows');
        rows.replaceChildren();
        $('compareInput').textContent = describe(lastInputs);
        $('compareVerdict').hidden = true;
        $('compare').hidden = false;
        const outputs = [];
        for (const m of methods) {
            const out = el('td.mono', { text: '…' });
            const time = el('td');
            const tr = el('tr', {}, [el('td', {}, [el('b', { text: labels[m.name] })]), out, time]);
            rows.appendChild(tr);
            try {
                const data = await execute(m.name, lastInputs);
                out.textContent = data.success ? data.result : (data.error || 'Error');
                time.textContent = data.success ? formatTime(data.executionTime) : '';
                if (data.success) outputs.push({ tr: tr, value: data.result });
                else tr.classList.add('mismatch');
            } catch (e) {
                out.textContent = 'Failed';
                tr.classList.add('mismatch');
            }
        }
        const verdict = $('compareVerdict');
        const first = outputs.length ? outputs[0].value : null;
        if (outputs.length === methods.length && outputs.every(function(o) { return o.value === first; })) {
            verdict.className = 'notice ok';
            verdict.textContent = '✓ All ' + outputs.length + ' methods agree';
        } else {
            outputs.forEach(function(o) { if (o.value !== first) o.tr.classList.add('mismatch'); });
            verdict.className = 'notice warn';
            verdict.textContent = 'The methods gave different outputs. Some problems accept several correct answers; otherwise one method has a bug.';
        }
        verdict.hidden = false;
        $('compare').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    });

    // ── Problem tab example: the output is computed by running the first method ──
    let exampleLoaded = false;
    async function loadExampleOutput() {
        if (exampleLoaded || !hasExamples || !methods.length) return;
        exampleLoaded = true;
        $('exampleCard').hidden = false;
        $('exampleInput').textContent = describe(examples);
        $('exampleOutput').textContent = Server.awake ? '…' : '… (waking the server)';
        try {
            const res = await postJson('/api/execute', { className: problem.className, methodName: methods[0].name, inputs: examples });
            const data = await res.json();
            $('exampleOutput').textContent = data.success ? data.result : 'couldn’t be computed';
            $('exampleOutput').classList.toggle('muted', !data.success);
        } catch (e) {
            $('exampleOutput').textContent = 'couldn’t be computed (server unreachable)';
        }
    }
})();

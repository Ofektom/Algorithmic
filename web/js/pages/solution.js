// Generated answer from the Java API (Groq / Claude Code). Built-in look-alikes come from the
// static catalogue, so they show even while the server is still waking. Below the answer is a
// chat: say or type what's wrong ("return type should be String") and the answer is rewritten.
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

    // ── Answer ──────────────────────────────────────────────────────────

    const LANGUAGES = ['Java', 'C', 'C++', 'C#', 'Python', 'JavaScript', 'TypeScript', 'Go', 'Kotlin', 'Swift'];
    let language = Store.setting('solutionLanguage', 'Java');
    if (LANGUAGES.indexOf(language) === -1) language = 'Java';
    let lastCode = '';
    // Every correction so far, oldest first, so a later one doesn't undo an earlier one
    const corrections = [];
    // The server caps corrections at 4000 characters; keep the newest that fit
    const MAX_CORRECTION_CHARS = 3500;

    function render(data) {
        const hasCode = fill('code', data.code, 'solution');
        tabsEl.querySelector('[data-tab="solution"]').hidden = false;
        $('code').hidden = !hasCode;
        $('copyCode').hidden = !hasCode;
        $('noCode').hidden = hasCode;
        // Older servers send no program; the tab then stays hidden
        const hasProgram = fill('program', data.program, 'program');
        $('programHint').hidden = !(hasCode && hasProgram);
        fill('explanation', data.explanation, 'explanation');
        fill('approach', data.solution, 'approach');
        fill('complexity', data.complexity, 'complexity');
        const problemText = (data.problem || '').trim();
        $('problemText').textContent = problemText;
        $('problemText').hidden = !problemText;
        $('problemMissing').hidden = Boolean(problemText);
        lastCode = (data.code || '').trim();
        selectTab('solution', false);
    }

    async function request(onSlow) {
        const body = { problemStatement: question, language: language };
        if (corrections.length) {
            body.instructions = correctionText();
            body.previousCode = lastCode;
        }
        const res = await postJson('/api/ai/solution-with-inputs', body, onSlow);
        const data = await res.json().catch(function() { return {}; });
        if (!res.ok || data.error) throw new Error(data.error || "Couldn't load a solution right now.");
        return data;
    }

    function correctionText() {
        const kept = [];
        let size = 0;
        for (let i = corrections.length - 1; i >= 0; i--) {
            size += corrections[i].length + 6;
            if (size > MAX_CORRECTION_CHARS && kept.length) break;
            kept.unshift(corrections[i]);
        }
        return kept.map(function(c, i) { return (i + 1) + '. ' + c; }).join('\n');
    }

    function errorText(err) {
        return err instanceof TypeError
            ? "Couldn't reach the server. Check your connection and try again." : err.message;
    }

    async function loadSolution() {
        $('error').hidden = true;
        $('answer').hidden = true;
        $('composer').hidden = true;
        tabsEl.hidden = false;
        $('loading').hidden = false;
        $('loadingText').textContent = 'Writing a solution… this can take a few seconds.';
        try {
            render(await request(function() {
                $('loadingText').textContent = 'Waking up the server… the first answer after a quiet spell can take up to a minute.';
            }));
            $('answer').hidden = false;
            $('composer').hidden = false;
            autoGrow();
            Store.addHistory({ type: 'generated', title: title, url: '/solution?q=' + encodeURIComponent(question) });
        } catch (err) {
            tabsEl.hidden = true;
            $('errorMessage').textContent = errorText(err);
            $('error').hidden = false;
        } finally {
            $('loading').hidden = true;
        }
    }

    // ── Correction chat ─────────────────────────────────────────────────

    let busy = false;

    function bubble(kinds, text) {
        $('chatThread').hidden = false;
        const b = el('div.bubble.' + kinds.join('.'), { text: text });
        $('chatThread').appendChild(b);
        return b;
    }

    // "write it in Python" also switches the language the next question is answered in
    function languageAskedFor(text) {
        const m = text.match(/\b(?:in|to|use|using|into)\s+(c\+\+|c#|javascript|typescript|python|kotlin|swift|java|golang|go|c)(?![\w+#])/i);
        if (!m) return null;
        const wanted = m[1].toLowerCase() === 'golang' ? 'go' : m[1].toLowerCase();
        return LANGUAGES.find(function(l) { return l.toLowerCase() === wanted; }) || null;
    }

    async function sendCorrection(text) {
        text = (text || '').trim();
        if (!text || busy) return;
        busy = true;
        stopDictation(false);
        $('chatInput').value = '';
        autoGrow();
        setComposerBusy(true);
        bubble(['me'], text);
        const typing = bubble(['ai', 'typing'], 'Updating the solution');
        corrections.push(text);
        const previousLanguage = language;
        const asked = languageAskedFor(text);
        if (asked) language = asked;
        $('answer').classList.add('updating');
        try {
            const data = await request(function() { typing.textContent = 'Waking up the server, then updating'; });
            render(data);
            if (asked) Store.setSetting('solutionLanguage', asked);
            typing.remove();
            bubble(['ai'], ((data.changes || '').trim() || 'Updated the solution.') + '\nThe new code is in the Solution tab above.');
            tabsEl.scrollIntoView({ behavior: 'smooth', block: 'start' });
        } catch (err) {
            corrections.pop();
            language = previousLanguage;
            typing.remove();
            bubble(['ai', 'err'], errorText(err) + ' Your message is back in the box, so you can send it again.');
            $('chatInput').value = text;
            autoGrow();
        } finally {
            $('answer').classList.remove('updating');
            busy = false;
            setComposerBusy(false);
        }
    }

    function setComposerBusy(on) {
        $('chatSend').disabled = on;
        $('chatMic').disabled = on || !Recognition;
        $('chatInput').disabled = on;
    }

    function autoGrow() {
        const box = $('chatInput');
        box.style.height = 'auto';
        box.style.height = Math.min(box.scrollHeight, 140) + 'px';
    }

    $('composer').addEventListener('submit', function(e) {
        e.preventDefault();
        sendCorrection($('chatInput').value);
    });
    $('chatInput').addEventListener('input', autoGrow);
    $('chatInput').addEventListener('keydown', function(e) {
        // Enter sends; Shift+Enter is a new line, for pasting starter code
        if (e.key === 'Enter' && !e.shiftKey && !e.isComposing) {
            e.preventDefault();
            sendCorrection($('chatInput').value);
        }
    });

    // ── Saying the correction ───────────────────────────────────────────

    const Recognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    let dictation = null;
    let dictationTimer = null;

    if (!Recognition) {
        $('chatMic').disabled = true;
        $('chatMic').title = 'Voice input is not supported in this browser (try Chrome, Edge or Safari)';
    }

    function stopDictation(send) {
        clearTimeout(dictationTimer);
        if (!dictation) return;
        const rec = dictation;
        dictation = null;
        rec.sendOnEnd = send;
        try { rec.stop(); } catch (e) { /* already stopped */ }
    }

    function startDictation() {
        if (!Recognition || busy) return;
        const rec = new Recognition();
        rec.continuous = true;
        rec.interimResults = true;
        rec.lang = Store.setting('voiceLang', 'en-US');
        rec.sendOnEnd = true;
        const before = $('chatInput').value.trim();
        rec.onresult = function(event) {
            const finals = [];
            let interim = '';
            for (let i = 0; i < event.results.length; i++) {
                const t = event.results[i][0].transcript.trim();
                if (!t) continue;
                if (event.results[i].isFinal) finals.push(t); else interim += t + ' ';
            }
            const heard = (typeof mergeSegments === 'function' ? mergeSegments(finals) : finals.join(' ')) + ' ' + interim;
            $('chatInput').value = (before + ' ' + heard).replace(/\s+/g, ' ').trim();
            autoGrow();
            // Same pause rule as the Ask button: stop and send after a short silence
            clearTimeout(dictationTimer);
            if (Store.setting('autoSend', true)) dictationTimer = setTimeout(function() { stopDictation(true); }, 2500);
        };
        rec.onerror = function(event) {
            if (event.error === 'not-allowed' || event.error === 'service-not-allowed') {
                rec.sendOnEnd = false;
                bubble(['ai', 'err'], 'Microphone access is blocked. Allow it in your browser settings, or type instead.');
            }
        };
        rec.onend = function() {
            if (dictation === rec) dictation = null;
            clearTimeout(dictationTimer);
            $('chatMic').classList.remove('listening');
            $('chatMic').setAttribute('aria-label', 'Say what to change');
            if (rec.sendOnEnd && $('chatInput').value.trim()) sendCorrection($('chatInput').value);
        };
        try {
            rec.start();
            dictation = rec;
            $('chatMic').classList.add('listening');
            $('chatMic').setAttribute('aria-label', 'Stop and send');
        } catch (e) {
            console.error('Dictation failed to start', e);
        }
    }

    $('chatMic').addEventListener('click', function() {
        if (dictation) stopDictation(true); else startDictation();
    });

    $('retry').addEventListener('click', loadSolution);
    loadSolution();
})();

// Voice-first input. The Ask button in the bottom bar (a floating pill on desktop) opens this
// sheet over whatever page is showing. Say a problem, pause, and it opens the solution through
// openSolution() (app.js), exactly like a typed search: built-in when it matches, generated if not.
class VoiceNavigation {
    // Silence after the last words before the question counts as finished
    static SILENCE_MS = 2500;
    static MATCH_DELAY_MS = 350;

    constructor(button) {
        this.button = button;
        this.recognition = null;
        this.isListening = false;
        this.finalText = '';
        this.interimText = '';
        this.silenceTimer = null;
        this.matchTimer = null;
        this.matchQuery = '';
        this.cancelled = false;
        this.sheet = null;
        this.scrim = null;
        this.init();
    }

    init() {
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        if (!SpeechRecognition) {
            this.button.disabled = true;
            this.button.title = 'Voice input is not supported in this browser (try Chrome, Edge or Safari)';
            this.button.setAttribute('aria-label', this.button.title);
            return;
        }

        this.recognition = new SpeechRecognition();
        // Continuous + interim: long questions survive pauses, and the user sees what was heard
        this.recognition.continuous = true;
        this.recognition.interimResults = true;
        this.recognition.lang = Store.setting('voiceLang', 'en-US');

        this.recognition.onresult = (event) => {
            this.interimText = '';
            for (let i = event.resultIndex; i < event.results.length; i++) {
                const text = event.results[i][0].transcript;
                if (event.results[i].isFinal) {
                    this.finalText += text + ' ';
                } else {
                    this.interimText += text;
                }
            }
            this.renderTranscript();
            this.scheduleMatches();
            if (Store.setting('autoSend', true)) this.resetSilenceTimer();
        };

        this.recognition.onerror = (event) => {
            if (event.error === 'no-speech' || event.error === 'aborted') return;
            const messages = {
                'not-allowed': 'Microphone access is blocked. Allow it in your browser settings and try again.',
                'service-not-allowed': 'Microphone access is blocked. Allow it in your browser settings and try again.',
                'audio-capture': 'No microphone was found.',
                'network': 'Speech recognition needs a network connection.'
            };
            this.cancelled = true;
            this.setStatus(messages[event.error] || 'Voice error: ' + event.error, true);
        };

        // Fires after stop(), the silence timeout, or when the browser ends the session itself
        this.recognition.onend = () => {
            this.isListening = false;
            clearTimeout(this.silenceTimer);
            this.updateListening();
            if (this.cancelled) return;
            const question = this.question();
            if (question) {
                this.submit(question);
            } else {
                this.setStatus("Didn't catch that. Tap Ask and try again.", true);
            }
        };

        this.updateListening();
        this.button.addEventListener('click', () => this.toggle());
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.sheet && !this.sheet.hidden) {
                this.cancel();
                return;
            }
            // Space starts talking anywhere outside a text field (desktop shortcut)
            if (e.code === 'Space' && !e.repeat && !isTyping(e.target) && !(this.sheet && !this.sheet.hidden)) {
                e.preventDefault();
                this.start();
            }
        });
    }

    question() {
        return (this.finalText + ' ' + this.interimText).replace(/\s+/g, ' ').trim();
    }

    toggle() {
        if (this.isListening) {
            this.finish();
        } else {
            this.start();
        }
    }

    start() {
        if (!this.recognition || this.isListening) return;
        this.finalText = '';
        this.interimText = '';
        this.matchQuery = '';
        this.cancelled = false;
        this.openSheet();
        this.setStatus('Listening…');
        this.renderTranscript();
        this.renderMatches([]);
        try {
            this.recognition.start();
            this.isListening = true;
            this.updateListening();
        } catch (err) {
            console.error('Voice start failed', err);
            this.setStatus('Could not start listening. Try again.', true);
        }
    }

    // Stop listening; onend then submits what was heard
    finish() {
        if (this.recognition && this.isListening) {
            this.recognition.stop();
        } else if (this.question()) {
            this.submit(this.question());
        }
    }

    cancel() {
        this.cancelled = true;
        clearTimeout(this.silenceTimer);
        clearTimeout(this.matchTimer);
        if (this.recognition && this.isListening) this.recognition.abort();
        this.closeSheet();
    }

    resetSilenceTimer() {
        clearTimeout(this.silenceTimer);
        this.silenceTimer = setTimeout(() => this.finish(), VoiceNavigation.SILENCE_MS);
    }

    submit(question) {
        this.closeSheet();
        if (typeof openSolution === 'function') {
            openSolution(question);
        } else {
            // An installed app may run an older cached app.js for one launch; /search lands on the same result
            window.location.href = '/search?q=' + encodeURIComponent(question);
        }
    }

    // Live matches: built-in problems for what has been heard so far
    scheduleMatches() {
        clearTimeout(this.matchTimer);
        this.matchTimer = setTimeout(() => this.loadMatches(), VoiceNavigation.MATCH_DELAY_MS);
    }

    async loadMatches() {
        const query = this.question();
        if (query.length < 3 || query === this.matchQuery) return;
        this.matchQuery = query;
        try {
            const res = await fetch('/api/search?q=' + encodeURIComponent(query));
            if (!res.ok || query !== this.matchQuery) return;
            const problems = await res.json();
            this.renderMatches(Array.isArray(problems) ? problems.slice(0, 3) : []);
        } catch (e) {
            // Matches are a convenience; the question still goes through on pause
        }
    }

    // ── UI ──────────────────────────────────────────────────────────────

    openSheet() {
        if (!this.sheet) {
            this.scrim = document.createElement('div');
            this.scrim.className = 'scrim';
            this.scrim.addEventListener('click', () => this.cancel());

            this.sheet = document.createElement('div');
            this.sheet.className = 'sheet voice-sheet';
            this.sheet.setAttribute('role', 'dialog');
            this.sheet.setAttribute('aria-modal', 'true');
            this.sheet.setAttribute('aria-label', 'Ask by voice');
            this.sheet.innerHTML =
                '<div class="grab"></div>' +
                '<p class="voice-status" aria-live="polite"></p>' +
                '<div class="wave" aria-hidden="true">' + '<i></i>'.repeat(16) + '</div>' +
                '<p class="transcript"><span class="final"></span><span class="interim"></span>' +
                '<span class="placeholder">Say a problem, e.g. “two sum” or “reverse a linked list”</span></p>' +
                '<div class="matches" hidden><p class="h2">Best matches</p><div class="list"></div></div>' +
                '<div class="btn-row">' +
                '<button type="button" class="btn secondary voice-cancel">Cancel</button>' +
                '<button type="button" class="btn primary voice-done">Show solution</button>' +
                '</div>';
            this.sheet.querySelector('.voice-cancel').addEventListener('click', () => this.cancel());
            this.sheet.querySelector('.voice-done').addEventListener('click', () => this.finish());
            document.body.appendChild(this.scrim);
            document.body.appendChild(this.sheet);
        }
        this.sheet.querySelector('.voice-done').hidden = false;
        this.scrim.hidden = false;
        this.sheet.hidden = false;
    }

    closeSheet() {
        if (this.sheet) this.sheet.hidden = true;
        if (this.scrim) this.scrim.hidden = true;
    }

    setStatus(message, isError = false) {
        if (!this.sheet) this.openSheet();
        const status = this.sheet.querySelector('.voice-status');
        status.textContent = message;
        status.classList.toggle('error', isError);
        if (isError) this.sheet.querySelector('.voice-done').hidden = !this.question();
    }

    renderTranscript() {
        if (!this.sheet) return;
        this.sheet.querySelector('.final').textContent = this.finalText;
        this.sheet.querySelector('.interim').textContent = this.interimText;
        this.sheet.querySelector('.placeholder').hidden = Boolean(this.question());
    }

    renderMatches(problems) {
        if (!this.sheet) return;
        const box = this.sheet.querySelector('.matches');
        const list = box.querySelector('.list');
        list.innerHTML = '';
        problems.forEach((p) => {
            const a = document.createElement('a');
            a.className = 'row-link';
            a.href = '/problem/' + encodeURIComponent(p.name);
            a.innerHTML = '<div class="grow"><div class="t"></div><div class="st"></div></div><span class="badge ok">Built-in</span>';
            a.querySelector('.t').textContent = p.name;
            a.querySelector('.st').textContent = p.category;
            a.addEventListener('click', () => {
                this.cancelled = true;
                if (this.isListening) this.recognition.abort();
            });
            list.appendChild(a);
        });
        box.hidden = problems.length === 0;
    }

    updateListening() {
        this.button.classList.toggle('listening', this.isListening);
        if (this.sheet) this.sheet.classList.toggle('listening', this.isListening);
        this.button.title = this.isListening ? 'Tap to finish' : 'Ask by voice';
        this.button.setAttribute('aria-label', this.button.title);
    }
}

function isTyping(el) {
    return el && (el.isContentEditable || /^(INPUT|TEXTAREA|SELECT)$/.test(el.tagName));
}

function initVoice() {
    let button = document.getElementById('voiceButton');
    if (!button) {
        // Pages without the bottom bar (e.g. an old cached page) still get voice input
        button = document.createElement('button');
        button.id = 'voiceButton';
        button.className = 'ask';
        button.type = 'button';
        button.innerHTML = '<span class="ask-circle">🎤</span><span class="ask-label">Ask</span>';
        document.body.appendChild(button);
    }
    try {
        window.voiceNav = new VoiceNavigation(button);
    } catch (e) {
        console.error('Voice init failed', e);
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initVoice);
} else {
    initVoice();
}

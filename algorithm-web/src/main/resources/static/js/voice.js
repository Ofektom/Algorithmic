// VoiceNavigation - read a question (or say "solve two sum") and go straight to its solution.
// The transcript goes through openSolution() (app.js), exactly like a typed search.
class VoiceNavigation {
    // Silence after the last words before we treat the question as finished
    static SILENCE_MS = 2500;

    constructor(button) {
        this.button = button;
        this.recognition = null;
        this.isListening = false;
        this.finalText = '';
        this.interimText = '';
        this.silenceTimer = null;
        this.cancelled = false;
        this.panel = null;
        this.init();
    }

    init() {
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        if (!SpeechRecognition) {
            this.button.disabled = true;
            this.button.title = 'Voice input is not supported in this browser (try Chrome, Edge or Safari)';
            this.button.style.opacity = '0.5';
            return;
        }

        this.recognition = new SpeechRecognition();
        // Continuous + interim: long questions survive pauses, and the user sees what was heard
        this.recognition.continuous = true;
        this.recognition.interimResults = true;
        this.recognition.lang = 'en-US';

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
            this.resetSilenceTimer();
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

        // Fires after stop(), silence timeout, or when the browser ends the session on its own
        this.recognition.onend = () => {
            this.isListening = false;
            clearTimeout(this.silenceTimer);
            this.updateButton();
            if (this.cancelled) return;
            const question = (this.finalText + ' ' + this.interimText).trim();
            if (question) {
                this.resolve(question);
            } else {
                this.setStatus("Didn't catch that. Tap the mic and try again.", true);
            }
        };

        this.updateButton();
        this.button.addEventListener('click', () => this.toggle());
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.panel && !this.panel.hidden) this.cancel();
        });
    }

    toggle() {
        if (this.isListening) {
            this.finish();
        } else {
            this.start();
        }
    }

    start() {
        this.finalText = '';
        this.interimText = '';
        this.cancelled = false;
        this.openPanel();
        this.setStatus('Listening… read the question, then pause or tap Done.');
        this.renderTranscript();
        try {
            this.recognition.start();
            this.isListening = true;
            this.updateButton();
        } catch (err) {
            console.error('Voice start failed', err);
            this.setStatus('Could not start listening. Try again.', true);
        }
    }

    finish() {
        if (this.recognition && this.isListening) this.recognition.stop();
    }

    cancel() {
        this.cancelled = true;
        if (this.recognition && this.isListening) this.recognition.abort();
        this.closePanel();
    }

    resetSilenceTimer() {
        clearTimeout(this.silenceTimer);
        this.silenceTimer = setTimeout(() => this.finish(), VoiceNavigation.SILENCE_MS);
    }

    resolve(question) {
        // Same path as the search box: one "Loading…" until the solution opens
        this.closePanel();
        if (typeof openSolution === 'function') {
            openSolution(question);
        } else {
            // An installed app may still run an older cached app.js for one launch; /search lands
            // on the same result
            window.location.href = '/search?q=' + encodeURIComponent(question);
        }
    }

    // ── UI ──────────────────────────────────────────────────────────────

    openPanel() {
        if (!this.panel) {
            this.panel = document.createElement('div');
            this.panel.className = 'voice-panel';
            this.panel.setAttribute('role', 'dialog');
            this.panel.setAttribute('aria-live', 'polite');
            this.panel.innerHTML =
                '<p class="voice-status"></p>' +
                '<p class="voice-transcript"><span class="voice-final"></span><span class="voice-interim"></span></p>' +
                '<div class="voice-actions">' +
                '<button type="button" class="btn btn-secondary voice-cancel">Cancel</button>' +
                '<button type="button" class="btn btn-primary voice-done">Done</button>' +
                '</div>';
            this.panel.querySelector('.voice-cancel').addEventListener('click', () => this.cancel());
            this.panel.querySelector('.voice-done').addEventListener('click', () => this.finish());
            document.body.appendChild(this.panel);
        }
        this.setDoneVisible(true);
        this.panel.hidden = false;
    }

    closePanel() {
        if (this.panel) this.panel.hidden = true;
    }

    setStatus(message, isError = false) {
        if (!this.panel) this.openPanel();
        const status = this.panel.querySelector('.voice-status');
        status.textContent = message;
        status.classList.toggle('voice-status-error', isError);
        if (isError) this.setDoneVisible(false);
    }

    setDoneVisible(visible) {
        if (this.panel) this.panel.querySelector('.voice-done').hidden = !visible;
    }

    renderTranscript() {
        if (!this.panel) return;
        this.panel.querySelector('.voice-final').textContent = this.finalText;
        this.panel.querySelector('.voice-interim').textContent = this.interimText;
    }

    updateButton() {
        this.button.classList.toggle('listening', this.isListening);
        this.button.title = this.isListening ? 'Tap to finish' : 'Read a question or say "solve two sum"';
        this.button.setAttribute('aria-label', this.button.title);
    }
}

function initVoice() {
    let button = document.getElementById('voiceButton');
    if (!button) {
        // Pages without their own button still get voice input
        button = document.createElement('button');
        button.id = 'voiceButton';
        button.className = 'voice-button';
        button.type = 'button';
        button.textContent = '🎤';
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

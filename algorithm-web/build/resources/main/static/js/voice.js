// VoiceNavigation - enhanced for direct LeetCode problem voice access + AI fallback hint
class VoiceNavigation {
    constructor() {
        this.recognition = null;
        this.isListening = false;
        this.categories = [];
        this.init();
    }

    init() {
        if (!('SpeechRecognition' in window) && !('webkitSpeechRecognition' in window)) {
            console.warn('Web Speech API not supported');
            this.showError('Voice navigation is not supported in this browser.');
            return;
        }

        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        this.recognition = new SpeechRecognition();
        this.recognition.continuous = false;
        this.recognition.interimResults = false;
        this.recognition.lang = 'en-US';

        this.recognition.onresult = (event) => {
            const transcript = event.results[0][0].transcript.trim();
            console.log('Voice →', transcript);
            this.processVoiceInput(transcript);
        };

        this.recognition.onerror = (event) => {
            console.error('Speech error:', event.error);
            this.showError('Voice error: ' + event.error);
            this.isListening = false;
            this.updateVoiceButton();
        };

        this.recognition.onend = () => {
            this.isListening = false;
            this.updateVoiceButton();
        };
    }

    async loadData() {
        try {
            const els = document.querySelectorAll('.category-card h3, [data-category]');
            this.categories = Array.from(els).map(el => el.textContent.trim()).filter(Boolean);
        } catch (e) {
            console.warn('Could not load categories from DOM', e);
        }

        if (!window.PROBLEMS || !Array.isArray(window.PROBLEMS) || window.PROBLEMS.length === 0) {
            console.warn('window.PROBLEMS not found or empty → voice problem matching limited');
        }
    }

    startListening() {
        if (!this.recognition) return;
        if (this.isListening) {
            this.stopListening();
            return;
        }
        try {
            this.isListening = true;
            this.updateVoiceButton();
            this.recognition.start();
        } catch (err) {
            console.error('Start failed', err);
            this.isListening = false;
            this.updateVoiceButton();
            this.showError('Could not start listening');
        }
    }

    stopListening() {
        if (this.recognition && this.isListening) {
            this.recognition.stop();
        }
    }

    processVoiceInput(transcript) {
        const text = this.normalize(transcript);

        // ── Intent: solve / show / open / go to / answer / solution ──
        const intentPatterns = [
            /(?:solve|answer|show|open|go to|view|display|what is)\s+(?:the\s+)?(?:problem\s+)?(.+)/i,
            /(?:solve|find|give me|show me)\s+(.+)/i,
        ];

        for (const pattern of intentPatterns) {
            const m = text.match(pattern);
            if (m && m[1]) {
                const query = m[1].trim();
                const match = this.findBestProblemMatch(query);
                if (match) {
                    this.navigateToProblem(match);
                    return;
                }
            }
        }

        // Common LeetCode spoken names (quick wins)
        const commonMappings = {
            'two sum': 'two sum',
            'add two numbers': 'add two numbers',
            'longest substring': 'longest substring without repeating characters',
            'median of two sorted arrays': 'median of two sorted arrays',
            'merge intervals': 'merge intervals',
            'merge two sorted lists': 'merge two sorted lists',
            // add more...
        };

        for (const [spoken, canonical] of Object.entries(commonMappings)) {
            if (text.includes(spoken)) {
                const match = this.findBestProblemMatch(canonical);
                if (match) {
                    this.navigateToProblem(match);
                    return;
                }
            }
        }

        // Category fallback
        const categoryMatch = this.categories.find(cat =>
            this.fuzzyMatch(text, this.normalize(cat)) || this.normalize(cat).includes(text)
        );
        if (categoryMatch) {
            this.navigateToCategory(categoryMatch);
            return;
        }

        // Final fallback: search (AI will kick in if no results)
        this.searchForProblem(transcript);
    }

    normalize(s) {
        return (s || '').toLowerCase().trim().replace(/\s+/g, ' ');
    }

    findBestProblemMatch(query) {
        if (!window.PROBLEMS?.length) return null;

        const q = this.normalize(query);

        let match = window.PROBLEMS.find(p => this.normalize(p.title || p.name) === q);
        if (match) return match;

        match = window.PROBLEMS.find(p => {
            const t = this.normalize(p.title || p.name);
            return t.includes(q) || q.includes(t);
        });
        if (match) return match;

        const qTokens = new Set(q.split(' '));
        let bestScore = 0;
        let best = null;

        for (const p of window.PROBLEMS) {
            const pt = this.normalize(p.title || p.name);
            const ptTokens = pt.split(' ');
            const score = ptTokens.reduce((acc, tok) => acc + (qTokens.has(tok) ? 1 : 0), 0);
            if (score > bestScore) {
                bestScore = score;
                best = p;
            }
        }

        if (bestScore >= 2 || (bestScore >= 1 && q.length > 12)) return best;

        for (const p of window.PROBLEMS) {
            const t = this.normalize(p.title || p.name);
            if (this.fuzzyMatch(q, t)) return p;
        }

        return null;
    }

    navigateToProblem(problem) {
        const slug = problem.slug || problem.name || problem.title;
        const safeSlug = encodeURIComponent(slug);
        window.location.href = `/problem/${safeSlug}`;
    }

    navigateToCategory(category) {
        window.location.href = `/category/${encodeURIComponent(category)}`;
    }

    async searchForProblem(query) {
        try {
            // Quick feedback for voice users
            this.showMessage('Searching for "' + query + '"... (AI ready if needed)');

            const res = await fetch(`/api/search?q=${encodeURIComponent(query)}`);
            if (res.ok) {
                const problems = await res.json();
                if (problems?.length > 0) {
                    this.navigateToProblem(problems[0]);
                    return;
                }
            }
        } catch (err) {
            console.error('Search API failed', err);
        }

        // Fallback to search page (AI fallback will trigger there)
        window.location.href = `/search?q=${encodeURIComponent(query)}`;
    }

    fuzzyMatch(a, b) {
        if (a.length === 0 || b.length === 0) return false;
        const longer = a.length > b.length ? a : b;
        const shorter = a.length > b.length ? b : a;
        const dist = this.levenshteinDistance(longer, shorter);
        const ratio = (longer.length - dist) / longer.length;
        return ratio > 0.68;
    }

    levenshteinDistance(a, b) {
        const matrix = Array(b.length + 1).fill().map(() => Array(a.length + 1).fill(0));
        for (let i = 0; i <= a.length; i++) matrix[0][i] = i;
        for (let j = 0; j <= b.length; j++) matrix[j][0] = j;

        for (let j = 1; j <= b.length; j++) {
            for (let i = 1; i <= a.length; i++) {
                if (a[i - 1] === b[j - 1]) {
                    matrix[j][i] = matrix[j - 1][i - 1];
                } else {
                    matrix[j][i] = Math.min(
                        matrix[j - 1][i - 1] + 1,
                        matrix[j][i - 1] + 1,
                        matrix[j - 1][i] + 1
                    );
                }
            }
        }
        return matrix[b.length][a.length];
    }

    updateVoiceButton() {
        const btn = document.getElementById('voiceButton');
        if (!btn) return;
        if (this.isListening) {
            btn.classList.add('listening');
            btn.innerHTML = 'Listening...';
            btn.title = 'Click to stop';
        } else {
            btn.classList.remove('listening');
            btn.innerHTML = '🎤 Voice';
            btn.title = 'Start voice navigation';
        }
    }

    showError(msg) {
        const div = document.createElement('div');
        div.className = 'voice-error';
        div.textContent = msg;
        Object.assign(div.style, {
            position: 'fixed', top: '20px', right: '20px',
            background: '#f44336', color: 'white', padding: '12px 18px',
            borderRadius: '8px', zIndex: '1001', boxShadow: '0 4px 12px #0003'
        });
        document.body.appendChild(div);
        setTimeout(() => div.remove(), 4500);
    }

    // NEW: Temporary message for better UX during fallback
    showMessage(msg, duration = 4000) {
        const div = document.createElement('div');
        div.textContent = msg;
        Object.assign(div.style, {
            position: 'fixed', bottom: '80px', left: '50%', transform: 'translateX(-50%)',
            background: '#333', color: 'white', padding: '12px 24px',
            borderRadius: '8px', zIndex: '1001', boxShadow: '0 4px 12px #0006',
            fontSize: '1.1rem'
        });
        document.body.appendChild(div);
        setTimeout(() => div.remove(), duration);
    }
}

// Global init
let voiceNav = null;

function initVoice() {
    try {
        voiceNav = new VoiceNavigation();
        voiceNav.loadData();
    } catch (e) {
        console.error('Voice init failed', e);
    }
}

function setupVoiceButton() {
    const btn = document.getElementById('voiceButton');
    if (!btn || btn.hasAttribute('data-voice-init')) return;

    btn.setAttribute('data-voice-init', 'true');
    btn.addEventListener('click', () => {
        if (voiceNav) voiceNav.startListening();
        else alert('Voice navigation not available. Try refreshing.');
    });
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        initVoice();
        setupVoiceButton();
    });
} else {
    initVoice();
    setupVoiceButton();
}

document.addEventListener('DOMContentLoaded', setupVoiceButton);
setTimeout(setupVoiceButton, 300);
window.addEventListener('load', setupVoiceButton);
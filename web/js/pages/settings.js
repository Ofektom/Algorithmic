(function() {
    const $ = function(id) { return document.getElementById(id); };
    function saved(text) { $('savedNote').textContent = text || 'Saved'; }

    let theme = 'system';
    try { theme = localStorage.getItem('algo.theme') || 'system'; } catch (e) { /* storage unavailable */ }
    $('theme').value = theme;
    $('theme').addEventListener('change', function() {
        const value = this.value;
        try {
            if (value === 'system') localStorage.removeItem('algo.theme');
            else localStorage.setItem('algo.theme', value);
        } catch (e) { /* storage unavailable */ }
        if (value === 'system') document.documentElement.removeAttribute('data-theme');
        else document.documentElement.setAttribute('data-theme', value);
        saved();
    });

    $('autoSend').checked = Store.setting('autoSend', true);
    $('autoSend').addEventListener('change', function() { Store.setSetting('autoSend', this.checked); saved(); });

    $('voiceLang').value = Store.setting('voiceLang', 'en-US');
    $('voiceLang').addEventListener('change', function() {
        Store.setSetting('voiceLang', this.value);
        if (window.voiceNav && window.voiceNav.recognition) window.voiceNav.recognition.lang = this.value;
        saved();
    });

    $('resetData').addEventListener('click', function() {
        Store.set('algo.history', []);
        Store.set('algo.ran', []);
        saved('History and progress cleared');
    });
})();

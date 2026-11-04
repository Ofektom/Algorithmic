// Where the Java API runs (Render). Change this line if the Render address changes.
window.ALGO_API = 'https://algorithmicx.onrender.com';

// Local testing only: http://localhost:3000/?api=http://localhost:8080 points the site at another API
(function() {
    if (!/^(localhost|127\.0\.0\.1)$/.test(location.hostname)) return;
    try {
        const param = new URLSearchParams(location.search).get('api');
        if (param) sessionStorage.setItem('algo.api', param);
        window.ALGO_API = sessionStorage.getItem('algo.api') || window.ALGO_API;
    } catch (e) { /* storage unavailable */ }
})();

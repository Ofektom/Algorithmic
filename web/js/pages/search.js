// Old links such as /search?q=two+sum land on the same result as a typed search
(function() {
    const q = new URLSearchParams(location.search).get('q');
    if (q) openSolution(q); else location.replace('/');
})();

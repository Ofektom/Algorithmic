// Service worker: keeps the app usable while the Render free instance wakes up from sleep.
//
// - Pages: network first. If the server hasn't answered within WAKE_TIMEOUT_MS (it is probably
//   asleep), show the last copy of that page, or the "waking up" page, which reloads itself as soon
//   as the server responds. A slow page is never cut off: if it arrives first, it is shown.
// - CSS/JS: their URLs carry a content hash (style-<hash>.css), so a cached copy never goes stale:
//   cache first, which also keeps cached pages styled while the server sleeps.
// - API calls and other POSTs always go straight to the network.
const CACHE_NAME = 'algorithm-web-v5';
const WAKING_PAGE = '/waking.html';
const WAKE_TIMEOUT_MS = 6000;
const PRECACHE = [WAKING_PAGE, '/manifest.json', '/icon-192.png', '/favicon.ico'];

self.addEventListener('install', function(event) {
  // Take over from an older worker immediately instead of waiting for every app window to close
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then(function(cache) {
      // One by one: a failed file (e.g. offline at install) shouldn't block installing the worker
      return Promise.all(PRECACHE.map(function(url) {
        return cache.add(url).catch(function() {});
      }));
    })
  );
});

self.addEventListener('activate', function(event) {
  event.waitUntil(
    caches.keys().then(function(names) {
      return Promise.all(names
        .filter(function(name) { return name !== CACHE_NAME; })
        .map(function(name) { return caches.delete(name); }));
    }).then(function() {
      return self.clients.claim();
    })
  );
});

function isHashedAsset(url) {
  return /^\/(css|js)\/.+-[0-9a-f]{32}\.(css|js)$/.test(url.pathname);
}

function store(request, response) {
  if (response && response.ok && response.type === 'basic') {
    const copy = response.clone();
    caches.open(CACHE_NAME).then(function(cache) { cache.put(request, copy); });
  }
  return response;
}

function pageWithWakeFallback(request) {
  const network = fetch(request).then(function(response) { return store(request, response); });
  const fallback = new Promise(function(resolve) {
    setTimeout(function() {
      caches.match(request).then(function(cached) {
        resolve(cached || caches.match(WAKING_PAGE));
      });
    }, WAKE_TIMEOUT_MS);
  });
  // Whichever comes first: the real page, or (after the timeout) a cached/waking page.
  // A network failure (offline) goes straight to the fallback instead of waiting.
  return Promise.race([
    network.catch(function() {
      return caches.match(request).then(function(cached) { return cached || caches.match(WAKING_PAGE); });
    }),
    fallback
  ]).then(function(response) {
    return response || network;
  });
}

self.addEventListener('fetch', function(event) {
  const request = event.request;
  const url = new URL(request.url);
  if (request.method !== 'GET' || url.origin !== self.location.origin) return;
  // The waking page checks the real server with this query; never answer it from cache
  if (url.searchParams.has('__wake')) return;

  if (request.mode === 'navigate') {
    event.respondWith(pageWithWakeFallback(request));
  } else if (isHashedAsset(url)) {
    event.respondWith(caches.match(request).then(function(cached) {
      return cached || fetch(request).then(function(response) { return store(request, response); });
    }));
  } else {
    event.respondWith(
      fetch(request)
        .then(function(response) { return store(request, response); })
        .catch(function() { return caches.match(request); })
    );
  }
});

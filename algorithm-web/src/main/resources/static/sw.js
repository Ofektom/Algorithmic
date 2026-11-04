// Service Worker for PWA
// Network first, cache as offline fallback: an installed app always gets the latest deploy when online,
// so there's no need to reinstall or to bump the cache name for updates.
const CACHE_NAME = 'algorithm-web-v4';
const urlsToCache = [
  '/',
  '/css/style.css',
  '/js/app.js',
  '/js/voice.js',
  '/manifest.json'
];

self.addEventListener('install', function(event) {
  // Take over from an older worker immediately instead of waiting for every app window to close
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then(function(cache) {
      // One by one: a failed asset (e.g. offline at install) shouldn't block installing the worker
      return Promise.all(urlsToCache.map(function(url) {
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
      // Control already-open pages too, so they stop being served from the old cache
      return self.clients.claim();
    })
  );
});

self.addEventListener('fetch', function(event) {
  const request = event.request;
  // Only same-origin GETs; API POSTs (resolve, solutions) always go straight to the network
  if (request.method !== 'GET' || new URL(request.url).origin !== self.location.origin) {
    return;
  }
  event.respondWith(
    fetch(request)
      .then(function(response) {
        // Refresh the offline copy of the app shell with what the server just sent
        const path = new URL(request.url).pathname;
        if (response.ok && urlsToCache.indexOf(path) !== -1) {
          const copy = response.clone();
          caches.open(CACHE_NAME).then(function(cache) { cache.put(request, copy); });
        }
        return response;
      })
      .catch(function() {
        return caches.match(request);
      })
  );
});

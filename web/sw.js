// Service worker for the installable app. The site is static and always up on Vercel, so pages and
// data are network first with an offline fallback; the API on Render is never cached.
const CACHE_NAME = 'algorithmic-static-v1';

self.addEventListener('install', function() { self.skipWaiting(); });

self.addEventListener('activate', function(event) {
  event.waitUntil(
    caches.keys().then(function(names) {
      return Promise.all(names.filter(function(n) { return n !== CACHE_NAME; }).map(function(n) { return caches.delete(n); }));
    }).then(function() { return self.clients.claim(); })
  );
});

self.addEventListener('fetch', function(event) {
  const request = event.request;
  if (request.method !== 'GET' || new URL(request.url).origin !== self.location.origin) return;
  event.respondWith(
    fetch(request).then(function(response) {
      if (response.ok) {
        const copy = response.clone();
        caches.open(CACHE_NAME).then(function(cache) { cache.put(request, copy); });
      }
      return response;
    }).catch(function() {
      return caches.match(request).then(function(cached) { return cached || caches.match('/'); });
    })
  );
});

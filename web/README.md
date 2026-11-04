# Algorithmic static site (Vercel)

The pages people see. They're plain HTML, CSS and JavaScript with no build step, served by Vercel,
so they load instantly even while the Java API on Render is asleep.

| Part | Where it comes from |
|---|---|
| Problem list, statements, solution code | `data/` (JSON exported from the Java registry, committed by the "Export static data" workflow) |
| Run, Compare, generated answers, fallback search | The Java API: `window.ALGO_API` in `js/config.js` |
| History, progress, settings | The browser's localStorage |

## Deploy on Vercel (once)

1. vercel.com → **Add New → Project** → import `Ofektom/Algorithmic`.
2. **Root Directory:** `web`. **Framework Preset:** Other. Leave Build and Output settings empty.
3. **Deploy.** Every push to `main` then redeploys; other branches get preview URLs.

The API allows calls from any `*.vercel.app` address. For a custom domain, add it on Render as
`CORS_ALLOWED_ORIGINS`, e.g. `https://algorithmic.vercel.app,https://algo.example.com`.

## Changing things

- Pages: edit the files here. `vercel.json` maps `/problem/<name>` and `/category/<name>` to their pages.
- Problems or solutions: change the Java code; the workflow re-exports `data/` on push.
- API address: `js/config.js`.

## Run locally

```
cd web && python3 -m http.server 3000
```

Then open http://localhost:3000/?api=http://localhost:8080 to point at a local API. The simple server
doesn't apply `vercel.json`, so open problems from the list rather than typing `/problem/...` URLs.

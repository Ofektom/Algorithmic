# Algorithm Web Application

Spring Boot MVC web application for testing and learning algorithms.

## Features

- **97 Algorithm Problems** across 17 categories
- **Interactive Execution** - Run algorithms with custom inputs
- **Progressive Web App (PWA)** - Works on mobile devices without installation
- **Responsive Design** - Works on desktop, tablet, and mobile
- **Search Functionality** - Find algorithms by name, description, or keywords

## Running Locally

```bash
./gradlew :algorithm-web:bootRun
```

Then open http://localhost:8080

## Building

```bash
./gradlew :algorithm-web:bootJar
```

The JAR will be in `algorithm-web/build/libs/algorithm-web-1.0.0.jar`

## Deploying to Render

1. Push your code to GitHub
2. Connect your repository to Render
3. Render will automatically detect the `render.yaml` or `Procfile`
4. The app will be deployed and accessible via Render's URL

## How questions are answered

Typed or spoken, a question that confidently matches a built-in problem opens that problem. Anything
else gets a generated solution from the first available engine:

1. **Claude Code** — only where the app runs on a machine with the `claude` CLI installed and signed in
   with a claude.ai subscription (`claude auth status` shows `"authMethod": "claude.ai"`). This is your
   own copy; it never uses an API key (`ANTHROPIC_API_KEY` is stripped from the CLI's environment).
2. **Groq** (`GROQ_API_KEY`) — everywhere else, including the public Render site, and as the fallback
   if Claude Code fails or times out.

Users only ever see "Loading…" and the result; which engine answered is in the server log.

| Variable | Default | Purpose |
|---|---|---|
| `GROQ_API_KEY` | — | Groq engine; required on the public site |
| `CLAUDE_CODE_ENABLED` | `true` | Set `false` to never use Claude Code |
| `CLAUDE_CODE_COMMAND` | `claude` | Full path if `claude` isn't on the app's `PATH` |
| `APP_ACCESS_PASSWORD` | — | When set, every page asks for this password (any username) |

## Your own copy, from your phone

Run the app on your computer and reach it privately with [Tailscale](https://tailscale.com) (free for
personal use), so only your devices can use your Claude subscription:

1. On the computer: make sure `claude auth status` shows you signed in, then start the app with a password:
   ```bash
   APP_ACCESS_PASSWORD='choose-a-password' ./gradlew :algorithm-web:bootRun
   ```
2. Install Tailscale on the computer and the phone, and sign in to the same account on both.
3. Serve the app over HTTPS inside your tailnet. Chrome only allows the microphone (voice input) and
   "Install app" on HTTPS pages, so a plain `http://` address would silently disable both:
   ```bash
   tailscale serve --bg 8080
   ```
   The first time, Tailscale may ask you to enable HTTPS certificates for your tailnet in its admin
   console. The command prints the address, e.g. `https://<computer-name>.<tailnet>.ts.net`.
4. On the phone, open that `https://` address in Chrome and enter the password when asked. Use Chrome's
   "Install app" to add it to your home screen; voice input works in the installed app too.

Keep the computer awake while you use it. Don't set `APP_ACCESS_PASSWORD` or install Claude Code on the
public Render deployment; it should keep using Groq.

## PWA Icons

You need to create two icon files:
- `algorithm-web/src/main/resources/static/icon-192.png` (192x192 pixels)
- `algorithm-web/src/main/resources/static/icon-512.png` (512x512 pixels)

These can be simple algorithm-themed icons or logos.

## Notes

- The `ProblemRegistryService` currently has skeleton implementations for all categories. You may want to complete it with all 97 problems from `Main.java` for full functionality.


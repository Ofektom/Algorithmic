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

## PWA Icons

You need to create two icon files:
- `algorithm-web/src/main/resources/static/icon-192.png` (192x192 pixels)
- `algorithm-web/src/main/resources/static/icon-512.png` (512x512 pixels)

These can be simple algorithm-themed icons or logos.

## Notes

- The `ProblemRegistryService` currently has skeleton implementations for all categories. You may want to complete it with all 97 problems from `Main.java` for full functionality.
- H2 database is included but not currently used. It's available for future features like user preferences or execution history.


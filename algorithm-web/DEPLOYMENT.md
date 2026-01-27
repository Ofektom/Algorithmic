# Deployment Guide for Render.com

## Prerequisites

1. GitHub account with your code pushed
2. Render.com account (free tier available)
3. Groq API key (get from https://console.groq.com/)

## Step 1: Prepare Your Code

✅ **Already Done:**
- `application.properties` uses environment variables (no hardcoded keys)
- `render.yaml` is configured for Java deployment
- `.gitignore` excludes sensitive files

## Step 2: Deploy to Render

### Option A: Using render.yaml (Recommended)

1. **Push your code to GitHub**
   ```bash
   git add .
   git commit -m "Prepare for Render deployment"
   git push origin main
   ```

2. **Go to Render Dashboard**
   - Visit https://dashboard.render.com
   - Click "New +" → "Blueprint"

3. **Connect Repository**
   - Connect your GitHub repository
   - Render will detect `render.yaml` automatically

4. **Set Environment Variables**
   - Go to your service → "Environment" tab
   - Add these environment variables:
     ```
     GROQ_API_KEY = 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
     GROQ_API_URL = https://api.groq.com/openai/v1/chat/completions
     GROQ_MODEL = llama-3.1-8b-instant
     ```

5. **Deploy**
   - Render will automatically:
     - Run `./gradlew :algorithm-web:bootJar`
     - Start with `java -jar algorithm-web/build/libs/algorithm-web-1.0.0.jar`
     - Your app will be live at `https://your-app-name.onrender.com`

### Option B: Manual Setup

1. **Create New Web Service**
   - Click "New +" → "Web Service"
   - Connect your GitHub repository

2. **Configure Build Settings**
   - **Name:** algorithm-web
   - **Environment:** Java
   - **Build Command:** `./gradlew :algorithm-web:bootJar`
   - **Start Command:** `java -jar algorithm-web/build/libs/algorithm-web-1.0.0.jar`

3. **Set Environment Variables**
   - Go to "Environment" tab
   - Add:
     - `GROQ_API_KEY` = (your API key)
     - `GROQ_API_URL` = `https://api.groq.com/openai/v1/chat/completions`
     - `GROQ_MODEL` = `llama-3.1-8b-instant`
     - `PORT` = (auto-set by Render, but defaults to 8080)

4. **Deploy**
   - Click "Create Web Service"
   - Render will build and deploy automatically

## Step 3: Verify Deployment

1. **Check Build Logs**
   - Should see: "BUILD SUCCESSFUL"
   - JAR file created: `algorithm-web-1.0.0.jar`

2. **Check Runtime Logs**
   - Should see: "Started AlgorithmWebApplication"
   - Server running on port (Render sets PORT env var)

3. **Test Your App**
   - Visit your Render URL
   - Should see the algorithm testing system homepage

## Local Development Setup

For local development, create `application-local.properties`:

```bash
# Copy the example file
cp algorithm-web/src/main/resources/application-local.properties.example \
   algorithm-web/src/main/resources/application-local.properties
```

Then edit `application-local.properties` with your API key.

Spring Boot will automatically load `application-local.properties` if it exists (it's gitignored).

## Environment Variables Reference

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `GROQ_API_KEY` | ✅ Yes | None | Your Groq API key |
| `GROQ_API_URL` | ❌ No | `https://api.groq.com/openai/v1/chat/completions` | Groq API endpoint |
| `GROQ_MODEL` | ❌ No | `llama-3.1-8b-instant` | Model to use |
| `PORT` | ❌ No | `8080` | Server port (auto-set by Render) |

## Troubleshooting

### Build Fails
- Check Java version (needs Java 17)
- Verify `./gradlew` has execute permissions
- Check build logs for specific errors

### App Won't Start
- Verify `GROQ_API_KEY` is set in Render dashboard
- Check runtime logs for errors
- Ensure PORT environment variable is set (Render does this automatically)

### API Calls Fail
- Verify `GROQ_API_KEY` is correct
- Check API key hasn't expired
- Review application logs for API errors

## Security Notes

✅ **Good Practices:**
- API keys are in environment variables (not in code)
- `application-local.properties` is gitignored
- `gradle.properties` is gitignored
- No credentials in GitHub

❌ **Never:**
- Commit API keys to GitHub
- Hardcode credentials in `application.properties`
- Share API keys in logs or screenshots


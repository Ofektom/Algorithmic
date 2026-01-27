# Use official OpenJDK 17 image (same as your JAVA_VERSION: 17)
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Gradle files first (for caching)
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY algorithm-web ./algorithm-web

# Give execute permission to gradlew
RUN chmod +x ./gradlew

# Build the JAR
RUN ./gradlew :algorithm-web:bootJar --no-daemon

# Expose the port Render expects
EXPOSE 8080

# Run the app (adjust JAR path if version changes)
CMD ["java", "-jar", "algorithm-web/build/libs/algorithm-web-1.0.0.jar"]
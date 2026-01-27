# Use Eclipse Temurin (reliable OpenJDK 17 replacement)
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files first (for layer caching)
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY algorithm-web ./algorithm-web

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application (run Gradle task)
RUN ./gradlew :algorithm-web:bootJar --no-daemon

# Expose Render's expected port
EXPOSE 8080

# Run the JAR (update version if your JAR name changes)
CMD ["java", "-jar", "algorithm-web/build/libs/algorithm-web-1.0.0.jar"]
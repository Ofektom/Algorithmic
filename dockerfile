FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy Gradle wrapper + root files (for multi-module setup)
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Copy all submodules (add more if you have others)
COPY algorithm-core ./algorithm-core
COPY algorithm-web ./algorithm-web

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the JAR
RUN ./gradlew :algorithm-web:bootJar --no-daemon

# Expose port
EXPOSE 8080

# Run the app (adjust JAR name/version if needed)
CMD ["java", "-jar", "algorithm-web/build/libs/algorithm-web-1.0.0.jar"]
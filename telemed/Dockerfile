# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

# Optional: Create a volume (not critical for basic apps)
VOLUME /tmp

# ARG to specify the jar file built by Maven/Gradle
ARG JAR_FILE=target/*.jar  # Use build/libs/*.jar if using Gradle

# Copy the jar into the container
COPY ${JAR_FILE} app.jar

# Run the app
ENTRYPOINT ["java","-jar","/app.jar"]

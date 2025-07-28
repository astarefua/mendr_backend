# --- Stage 1: Build the application ---
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Package the application
RUN mvn clean package -DskipTests

# --- Stage 2: Run the application ---
FROM openjdk:17-jdk-slim

# Create a working directory
WORKDIR /app

# Copy the packaged jar from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

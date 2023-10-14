# Use a specific Ubuntu base image
FROM ubuntu:latest AS build

# Update the package repository and install openjdk-17-jdk
RUN apt-get update && apt-get install -y openjdk-17-jdk

# Copy your source code and build the application
WORKDIR /app
COPY . .
RUN apt-get install -y maven
RUN mvn clean install

# Use a smaller base image for the final application
FROM openjdk:17-jdk-slim

# Expose port 8080 (if your application listens on this port)
EXPOSE 8080

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/todolist-1.0.0.jar /app.jar

# Set the entry point
ENTRYPOINT ["java", "-jar", "/app.jar"]

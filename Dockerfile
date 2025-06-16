# Stage 1: Build the project
FROM openjdk:21-jdk-slim AS build

# Install Git and Maven
RUN apt-get update && apt-get install -y git maven

# Set working directory
WORKDIR /app

# Clone the repository
RUN git clone https://github.com/yauyao/redis-pub-sub-demo.git .

# Build the project using Maven
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]

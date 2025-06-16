#!/bin/bash

# Check if the Docker network 'redis-net' exists
if docker network inspect redis-net >/dev/null 2>&1; then
  echo "Docker network 'redis-net' exists. Deleting it..."
  docker network rm redis-net
fi

# Create the Docker network 'redis-net'
echo "Creating Docker network 'redis-net'..."
docker network create redis-net

# Start the Redis container
echo "Starting Redis container..."
docker run -d --name redis --network redis-net -p 6379:6379 redis:latest

# Build the Spring Boot app container
echo "Building Spring Boot app container..."
docker build -t redis-pub-sub-demo .

# Start the Spring Boot app container with the correct environment variable and network settings
echo "Starting Spring Boot app container..."
docker run --network redis-net -e REDIS_URL=redis -p 8080:8080 redis-pub-sub-demo

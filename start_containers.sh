#!/bin/bash

# Remove old containers if they exist
docker rm -f redis redis-sub-pub-demo 2>/dev/null

# Start the Redis container
echo "Starting Redis container..."
docker run -d --name redis -p 6379:6379 redis:latest

# Build the Spring Boot app container
echo "Building Spring Boot app container..."
docker build -t redis-pub-sub-demo .

# Start Spring Boot app
docker run -d --name redis-sub-pub-demo -e REDIS_URL=192.168.0.8 -p 8080:8080 redis-pub-sub-demo

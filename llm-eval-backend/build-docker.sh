#!/bin/bash

# Build script for LLM Eval Backend Docker image
# Usage: ./build-docker.sh [tag]

# Default tag if not provided
TAG=${1:-latest}
IMAGE_NAME="llm-eval-backend"

echo "🏗️  Building Docker image: ${IMAGE_NAME}:${TAG}"

# Build the Docker image
docker build -t ${IMAGE_NAME}:${TAG} .

if [ $? -eq 0 ]; then
    echo "✅ Successfully built ${IMAGE_NAME}:${TAG}"
    
    # Show image details
    echo "📊 Image details:"
    docker images ${IMAGE_NAME}:${TAG}
    
    echo ""
    echo "🚀 To run the container:"
    echo "docker run -p 8080:8080 --env-file .env ${IMAGE_NAME}:${TAG}"
    echo ""
    echo "🐳 To use with docker-compose:"
    echo "export BACKEND_IMAGE=${IMAGE_NAME}:${TAG}"
    echo "docker-compose up"
else
    echo "❌ Build failed!"
    exit 1
fi 
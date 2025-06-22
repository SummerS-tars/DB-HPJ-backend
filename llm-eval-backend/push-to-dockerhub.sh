#!/bin/bash

# Push script for LLM Eval Backend to Docker Hub
# Usage: ./push-to-dockerhub.sh <your-dockerhub-username> [tag]

if [ $# -eq 0 ]; then
    echo "❌ Error: Please provide your Docker Hub username"
    echo "Usage: $0 <your-dockerhub-username> [tag]"
    echo "Example: $0 johnsmith latest"
    exit 1
fi

DOCKERHUB_USERNAME=$1
TAG=${2:-latest}
LOCAL_IMAGE="llm-eval-backend:latest"
REMOTE_IMAGE="$DOCKERHUB_USERNAME/llm-eval-backend:$TAG"

echo "🏷️  Tagging image..."
echo "   From: $LOCAL_IMAGE"
echo "   To:   $REMOTE_IMAGE"

# Tag the image
docker tag $LOCAL_IMAGE $REMOTE_IMAGE

if [ $? -ne 0 ]; then
    echo "❌ Failed to tag image"
    exit 1
fi

echo "✅ Image tagged successfully"

# Check if user is logged in
echo "🔐 Checking Docker Hub login..."
if ! docker info | grep -q "Username:"; then
    echo "🔑 Please login to Docker Hub:"
    docker login
    if [ $? -ne 0 ]; then
        echo "❌ Login failed"
        exit 1
    fi
fi

echo "📤 Pushing image to Docker Hub..."
docker push $REMOTE_IMAGE

if [ $? -eq 0 ]; then
    echo "✅ Successfully pushed $REMOTE_IMAGE"
    echo ""
    echo "🚀 Your image is now available at:"
    echo "   https://hub.docker.com/r/$DOCKERHUB_USERNAME/llm-eval-backend"
    echo ""
    echo "🐳 To use in docker-compose, set:"
    echo "   export BACKEND_IMAGE=$REMOTE_IMAGE"
    echo "   docker-compose up"
    echo ""
    echo "🔧 Or run directly:"
    echo "   docker run -p 8080:8080 $REMOTE_IMAGE"
else
    echo "❌ Push failed!"
    exit 1
fi 
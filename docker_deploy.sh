#!/bin/bash

echo "Building the Angular client Docker image..."
cd ./TheLibraryA || { echo "Failed to navigate to ./TheLibraryA"; exit 1; }
docker build -t the_library-angular-client:latest .
if [ $? -ne 0 ]; then
  echo "Failed to build the Angular client Docker image"
  exit 1
fi
echo "Angular client Docker image built"

echo "Building the Spring server Docker image..."
cd ../TheLibraryj || { echo "Failed to navigate to ./TheLibraryj"; exit 1; }
docker build -t the_library-spring-server:latest .
if [ $? -ne 0 ]; then
  echo "Failed to build the Spring server Docker image"
  exit 1
fi
echo "Spring server Docker image built"
cd ..
echo "Deploying the Docker stack..."
docker stack deploy --compose-file docker-compose.yml library_stack
if [ $? -ne 0 ]; then
  echo "Failed to deploy the Docker stack"
  exit 1
fi
echo "Docker stack deployed"
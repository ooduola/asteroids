#!/bin/bash

# Variables
DB_CONTAINER_NAME="my_postgres"
DB_IMAGE="postgres:latest"
DB_PORT=5432
DB_USER="myuser"
DB_PASSWORD="mypassword"
DB_NAME="mydb"

# Function to check if Docker is installed
check_docker_installed() {
  if ! command -v docker &> /dev/null
  then
    echo "Docker is not installed. Please install Docker and try again."
    exit 1
  fi
}

# Function to check if the PostgreSQL container is running
check_postgres_running() {
  if [ "$(docker ps -q -f name=${DB_CONTAINER_NAME})" ]; then
    echo "PostgreSQL container is already running."
  else
    echo "PostgreSQL container is not running. Starting container..."
    start_postgres_container
  fi
}

# Function to start the PostgreSQL container
start_postgres_container() {
  if [ "$(docker ps -aq -f status=exited -f name=${DB_CONTAINER_NAME})" ]; then
    # Cleanup
    docker rm ${DB_CONTAINER_NAME}
  fi
  docker run -d \
    --name ${DB_CONTAINER_NAME} \
    -p ${DB_PORT}:5432 \
    -e POSTGRES_USER=${DB_USER} \
    -e POSTGRES_PASSWORD=${DB_PASSWORD} \
    -e POSTGRES_DB=${DB_NAME} \
    ${DB_IMAGE}
  echo "PostgreSQL container started."
}

# Function to check if PostgreSQL is ready
check_postgres_ready() {
  echo "Waiting for PostgreSQL to be ready..."
  until docker exec ${DB_CONTAINER_NAME} pg_isready -U ${DB_USER} -d ${DB_NAME}
  do
    sleep 1
  done
  echo "PostgreSQL is ready."
}

# Function to start the Scala application
start_scala_app() {
  echo "Starting Asteroids server..."
  sbt run
}

# Main script
check_docker_installed
check_postgres_running
check_postgres_ready
start_scala_app

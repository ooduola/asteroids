#!/bin/bash

# Exit on any error
set -e

# Print the current working directory
echo "Current directory: $(pwd)"

# Print sbt version
sbt --version

# Run the tests
echo "Running tests..."
sbt test

# Notify completion
echo "Tests completed successfully."


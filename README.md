# Asteroids Backend Service

This project provides a backend service for accessing and managing information about asteroids. It allows clients to:

- **List Asteroids**: Retrieve a list of asteroids with essential information.
- **Search by Date Range**: Filter asteroids based on a specified date range.
- **View Details**: Fetch detailed information about a specific asteroid.
- **Sorting**: Sort asteroids by their name.
- **Adding to favourite**: Adding asteroids with essential information.
- **Show a list of favourite**: Fetch a list of favourite asteroids with essential information.
- **Display details of favourite**: Display details of favourite asteroids.

## Technologies

The backend service is implemented using Scala and relies on the following key components:

- **Http4s**: For building the HTTP server and handling HTTP requests.
- **Circe**: For JSON encoding and decoding.
- **Cats Effect**: For handling effects and asynchronous operations.
- **Caffeine**: For caching API responses to improve performance.
- **Doobie**: For database interaction.
- **Flyway**: For database migrations.
- **Log4cats**: For logging.

## Setup

### Dependencies

Ensure you have the following dependencies in your build file (`build.sbt`):

## Running Tests

### Run the tests with:
```
./Test.sh
```

## Running the Application

### Run the application with:
Ensure Docker is installed and running before executing the Run.sh script. This script will start a PostgreSQL container and then start the Asteroids application.

```
./Run.sh
```

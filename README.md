# Courier Tracking API

This project is a Spring Boot application designed to track courier locations and their entries into store proximity zones.

## Prerequisites

*   Java 21 or higher
*   Apache Maven

## Setup & Build

1.  Clone the repository:
    ```bash
    git clone https://github.com/ecvsgl/CourierTracking.git
    cd CourierTracking
    ```
2.  Build the project using Maven:
    ```bash
    ./mvnw clean install
    ```
    or if you have Maven locally,

    ```bash
    mvn clean install 
    ```

## Running the Application

You can run the application using the Maven Spring Boot plugin:

```bash
./mvnw clean spring-boot:run
# or
# mvn clean spring-boot:run
```
The application will start on `http://localhost:8080` by default.

## H2 Database Console

The application uses an in-memory H2 database. You can access its console when the application is running:

*   **URL**: `http://localhost:8080/h2-console`
*   **JDBC URL**: `jdbc:h2:mem:migrosdb`
*   **Username**: `sa`
*   **Password**: (leave blank)

## API Endpoints

The following endpoints are available under the base path `/api/courier`:

*   **`POST /location`**: Updates a single courier's location.
    *   Request Body: `CourierLocationUpdateRequest`
      ```json
      {
        "courierId": "string",
        "lat": 0.0,
        "lng": 0.0,
        "timestamp": "2024-05-25T12:00:00Z"
      }
      ```
    *   Response: `CourierLocationUpdateResponse`

*   **`POST /locations`**: Updates locations for a batch of couriers.
    *   Request Body: `List<CourierLocationUpdateRequest>`
    *   Response: `List<CourierLocationUpdateResponse>`

*   **`GET /{courierId}/distance`**: Calculates and returns the total travel distance for a specific courier.
    *   Path Variable: `courierId` (String)
    *   Response: `double` (representing total distance in meters)

## Configuration

Key application properties can be found in `src/main/resources/application.properties`:

*   `couriertracking.mock.initialize`: (default: `true`) Set to `true` to initialize with mock store data from `stores.json` on startup.
*   `couriertracking.reentry.cooldown.minutes`: (default: `1`) The cooldown period in minutes before a courier re-entering a store's proximity is logged again.
*   `couriertracking.store_proximity_radius.meters`: (default: `100`) The radius in meters around a store that defines its proximity zone.


# Courier Tracking API

This project is a Spring Boot application designed to track courier locations and their entries into store proximity zones.

## Prerequisites

*   Java 21 or higher
*   Apache Maven 3.xx

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
        "courierId": "someCourierId",
        "latitude": 41.0,
        "longitude": 29.0,
        "timestamp": "2025-05-25T10:13:20.406572"
      }
      ```
    *   Response: `CourierLocationUpdateResponse`

      ```json
      {
        "courierId": "someCourierId",
        "latitude": 41.0,
        "longitude": 29.0,
        "timestamp": "2025-05-25T10:13:20.406572",
        //indicates if a locationUpdate has indeed triggered a storeEntry event
        "triggeredStoreEntry": false 
      }
      ```

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

## Testing 

Unless specified otherwise, the app initializes itself with Mock data. 

Couriers such as with ids: "MOCK_COURIER_ID_2" are being added. Mock data ensures some of the mocked data is close to stores. 
However, re-entry cooldown is not accounted for to keep mock initialization simple.

### 1. Testing Courier Location Updates

You can send location updates for couriers using the `POST /api/courier/location` (for a single update) or `POST /api/courier/locations` (for batch updates) endpoints. 
This is useful for adding data before testing the distance query, or for general testing of the location update functionality.

Use a tool like Postman for testing.

**Example: Single Location Update**

This example sends a location for `MOCK_COURIER_ID_2` (you can use any ID; if it's a new ID, the courier will be created implicitly by the location update).
Send request to `POST /api/courier/location`

With request body:

```bash
    {
        "courierId": "MOCK_COURIER_ID_2",
        "lat": 41.0000,
        "lng": 29.0000,
        "timestamp": "2025-05-25T11:00:00Z"
    }
```


**Example: Batch Location Updates**

This example sends two location updates for `MOCK_COURIER_ID_2`.
Send request to `POST /api/courier/locations`

With request body:

```bash
[
  {
    "courierId": "MOCK_COURIER_ID_2",
    "lat": 41.0000,
    "lng": 29.0000,
    "timestamp": "2025-05-25T11:00:00Z"
  },
  {
    "courierId": "MOCK_COURIER_ID_2",
    "lat": 41.0010,
    "lng": 29.0010,
    "timestamp": "2025-05-25T11:01:00Z"
  }
]
```

### 2. Testing Total Travel Distance

After some locations have been logged for a courier (either via the mock data initialization or by POSTing updates as shown above), you can query their total travel distance using the `GET /api/courier/{courierId}/distance` endpoint.

**Example: Get Total Distance for a Courier**

Replace `MOCK_COURIER_ID_2` with the ID of the courier you want to query.


This will return a `double` value representing the total distance traveled by the courier in meters.

*   If the courier has only one location entry, the API will return a 404, as one point is not enough for distance calculation.
*   If the courier is not found (has no location entries at all), the API will return a 404 Not Found status.
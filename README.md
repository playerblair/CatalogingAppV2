## Manga Cataloging Application using Spring Boot
A backend for a manga cataloging application using Spring Boot and MongoDB. This application uses the Jikan API, available at: jikan.moe

### Technology Stack
- Java 21
- Spring Boot 3.4.2
- Spring Data Mongo
- MongoDB
- MongoExpress
- Docker Compose
- Maven
- Testcontainers (for testing)

### Prerequisites
- JDK 21 or higher
- Maven
- Docker
- Git

### Endpoints
#### GET "/manga/list"
#### GET "/manga/search"
parameter: query - the search query
#### POST "/manga/add"
parameter: id - the id of manga to add from search results
#### PUT "/manga/update-progress"
request body (example below): 
```json
{
    "malId": 11,
    "progress": "FINISHED",
    "chaptersRead": 700,
    "volumesRead": 72,
    "rating": 10
}
```
#### PUT "/manga/update-collection"
request body (example below):
```json
{
    "malId": 11,
    "digitalCollection": true,
    "physicalCollection": true,
    "volumesAvailable": 72,
    "volumesOwned": 2,
    "volumesAcquired": [1, 2],
    "volumesEdition": "Paperback"
}
```

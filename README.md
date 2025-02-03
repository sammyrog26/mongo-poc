# MongoDB CRUD & Aggregation API

## Overview
This project is a Spring Boot-based REST API that performs basic CRUD operations and aggregations on a MongoDB database. It allows users to interact with MongoDB collections using various endpoints for fetching, creating, updating, deleting, and aggregating documents.

## Features
- Retrieve a list of databases and collections.
- Perform CRUD (Create, Read, Update, Delete) operations on documents.
- Aggregate documents with match, sort, group, and limit operations.

## Technologies Used
- Java
- Spring Boot
- MongoDB
- Lombok
- Swagger (OpenAPI Documentation)
- Apache Commons Lang

## API Endpoints

### Database & Collection Operations
| Method | Endpoint | Description |
|--------|---------|-------------|
| GET | `/api/getDatabases` | Fetch all database names. |
| GET | `/api/getCollections/{databaseName}` | Fetch collection names from a given database. |

### CRUD Operations
| Method | Endpoint | Description |
|--------|---------|-------------|
| GET | `/api/getDocuments/{databaseName}/{collectionName}` | Retrieve all documents from a collection. |
| POST | `/api/createDocument/{databaseName}/{collectionName}` | Insert a document into a collection. |
| PUT | `/api/updateDocument/{databaseName}/{collectionName}?filterKey=&filterValue=` | Update a document in a collection based on a filter. |
| DELETE | `/api/deleteDocument/{databaseName}/{collectionName}?filterKey=&filterValue=` | Delete a document from a collection based on a filter. |

### Aggregation Operations
| Method | Endpoint | Description |
|--------|---------|-------------|
| GET | `/api/aggregate/match/{databaseName}/{collectionName}?matchField=&matchValue=` | Perform a match aggregation. |
| GET | `/api/aggregate/sort/{databaseName}/{collectionName}?sortField=&sortOrder=` | Perform a sort aggregation. |
| GET | `/api/aggregate/sortAndLimit/{databaseName}/{collectionName}?sortField=&sortOrder=&limit=` | Perform a sort aggregation with a limit. |
| GET | `/api/aggregate/group/{databaseName}/{collectionName}?groupField=` | Perform a group aggregation. |
| POST | `/api/aggregate/group/age/{databaseName}/{collectionName}?groupField=` | Group documents by a field where age >= 20. |

## How to Run
1. Install and run MongoDB.
2. Clone this repository.
3. Ensure you have Java and Gradle installed.
4. Configure MongoDB connection in `application.properties`.
5. Run the application:
   ```bash
   ./gradlew bootRun
   ```
6. Access the API via Swagger UI:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## Author
Samay Shrigadiwar
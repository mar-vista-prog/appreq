# Request Management Application

Spring Boot 3.1.5 / Java 21 service for managing application requests with a strict state machine, audit logging, optimistic locking, and a REST API.

## What this app does

- Creates and updates requests
- Validates business rules in the domain layer
- Supports request lifecycle transitions such as verify, accept, reject, publish, and delete
- Records every state change in an audit log
- Persists data in an H2 in-memory database for local development and tests

## Main features

- Hexagonal architecture with ports and adapters
- Domain-driven design with business rules enforced in `ApplicationRequest`
- Optimistic locking via `@Version` to detect concurrent modifications
- Pagination and filtering by name/state using Spring Data `Page<T>`
- Unique publication IDs generated from a database sequence
- Clear separation between domain, application, and infrastructure layers

## State machine

```text
CREATED -> VERIFIED | DELETED
VERIFIED -> REJECTED | ACCEPTED
ACCEPTED -> REJECTED | PUBLISHED
REJECTED, PUBLISHED, DELETED -> terminal
```

## REST API

Base URL: `http://localhost:8080/api/application-requests`

Available endpoints:

- `POST /api/application-requests` - create a new request
- `GET /api/application-requests` - list requests with pagination and filters
- `GET /api/application-requests/{id}` - get a single request
- `PATCH /api/application-requests/{id}` - partially update the request body
- `POST /api/application-requests/{id}/verify` - verify the request
- `POST /api/application-requests/{id}/approve` - approve the request
- `POST /api/application-requests/{id}/reject` - reject the request
- `POST /api/application-requests/{id}/publish` - publish the request
- `DELETE /api/application-requests/{id}` - delete the request

Legacy aliases still work for compatibility: `/api/requests` and `/api/requests/{id}/body` / `/api/requests/{id}/accept`.

Example request payloads and test calls are available in `requests.http`.

## Quick start

```bash
mvn test
mvn spring-boot:run
```

The application will start on port `8080`.

## Project structure

```text
src/main/java/com/codinggame/applicationrequest/
├── domain/
│   ├── model/              # aggregates and value objects
│   └── exception/          # domain exceptions
├── application/
│   ├── dto/                # application DTOs
│   ├── port/
│   │   ├── in/             # input ports and commands
│   │   └── out/            # outbound contracts
│   ├── service/            # use case orchestration
│   └── mapper/             # mappers between domain and DTOs
└── infrastructure/
    └── adapter/
        ├── in/
        │   └── rest/
        │       ├── controller/
        │       ├── dto/
        │       └── handler/
        └── out/
            └── persistence/
                ├── entity/
                ├── repository/
                └── ApplicationRequestRepositoryAdapter.java
```

## Testing

The project includes 133 automated tests covering:

- domain state transitions
- validation rules
- service orchestration
- pagination and filtering
- optimistic locking behavior

## Notes

- The codebase follows a layered architecture with a clear separation between domain, application, and infrastructure concerns.
- The H2 database is intended for local development and testing; it is not a production persistence setup.

# Assumptions and Design Decisions

This document captures the main architectural and technical decisions used in the current implementation of the request-management application.

## Architecture

### Hexagonal architecture with ports and adapters

Assumption: the application is organized around a domain core and infrastructure adapters.

Rationale:
- keeps business rules in the domain layer
- makes the service easier to test
- avoids coupling the domain model to Spring or JPA directly

### Domain-driven design

Assumption: state transitions and validation rules belong to the domain model rather than to controllers or services.

Implementation:
- `ApplicationRequest` owns the transition rules
- services orchestrate persistence and audit-log recording
- domain exceptions represent invalid input or invalid business state

## State machine

Assumption: request lifecycle is intentionally strict and follows a small finite-state model.

Current transitions:

```text
CREATED -> VERIFIED | DELETED
VERIFIED -> REJECTED | ACCEPTED
ACCEPTED -> REJECTED | PUBLISHED
REJECTED, PUBLISHED, DELETED -> terminal
```

Rationale:
- prevents unsupported transitions
- keeps the flow predictable
- makes the behavior easy to test and document

## Persistence and storage

### H2 in-memory database for local development

Assumption: the application uses H2 in-memory persistence for local runs and automated tests.

Rationale:
- no external database dependency is required
- test setup remains fast and self-contained
- the database is not part of the deliverable

### Hibernate-managed schema

Assumption: JPA entities are sufficient for the current scope, and Hibernate creates the schema automatically.

Implementation:
- `spring.jpa.hibernate.ddl-auto=create-drop` is used for development mode

## Identifiers

### UUIDs for request IDs

Assumption: each request receives a UUID as its business identifier.

Rationale:
- avoids collisions across environments
- keeps the identifier opaque and non-sequential

### Database sequence for publication IDs

Assumption: publication IDs are numeric and generated from a database sequence.

Rationale:
- makes published requests easy to reference
- preserves uniqueness without relying on the request UUID

## Concurrency and versioning

Assumption: optimistic locking is used to detect concurrent updates.

Implementation:
- `ApplicationRequestJpaEntity` carries a version column
- repository adapters propagate the version value between the domain object and the persistence layer
- concurrent save failures are surfaced as a clear conflict error

Rationale:
- avoids pessimistic locking in a simple CRUD-style workflow
- provides a reasonable concurrency model for this application size

## Audit logging

Assumption: audit entries are immutable and created for every state transition.

Implementation:
- `AuditLogEntry` is persisted separately from the request aggregate
- each transition creates a new log entry
- no update/delete flow is required for historical entries

## API and DTO design

Assumption: the REST layer uses DTOs and command objects rather than exposing the domain model directly.

Implementation:
- controllers receive command objects such as create/update/reject requests
- services return DTOs to the API layer
- the application layer is responsible for mapping between domain and DTO structures

## Pagination

Assumption: list responses use Spring Data's standard `Page<T>` as the API contract for paginated results.

Rationale:
- aligns with Spring Boot conventions and Spring Data repositories
- keeps the API consistent with standard pagination metadata
- reduces custom adapter logic in the application layer

## Testing strategy

Assumption: the project is validated with a mix of unit and service-level tests.

Current test coverage includes:
- domain-model behavior and state transitions
- validation rules and edge cases
- service orchestration and persistence mapping
- pagination and filtering paths

## Build and tooling assumptions

Assumption: Lombok is part of the project toolchain and is configured for Java 21 compilation.

Rationale:
- reduces boilerplate in services, controllers, and adapters
- keeps the codebase easier to read without sacrificing maintainability
- requires explicit annotation-processing setup in Maven for reliable compilation

## 🌐 REST API Design

### 16. RESTful Endpoints

**Assumption:** Each operation is exposed as a REST endpoint using resource-oriented URLs, plural resource names, and HTTP verbs.

**Endpoints:**
- `POST /api/application-requests` - Create
- `GET /api/application-requests/{id}` - Retrieve
- `GET /api/application-requests?page=1&size=10&name=xyz&state=CREATED` - List with filters
- `PATCH /api/application-requests/{id}` - Partially update content
- `POST /api/application-requests/{id}/verify` - Verify
- `POST /api/application-requests/{id}/approve` - Approve
- `POST /api/application-requests/{id}/reject` - Reject
- `POST /api/application-requests/{id}/publish` - Publish
- `DELETE /api/application-requests/{id}` - Delete

Legacy aliases remain available for compatibility: `/api/requests`, `/api/requests/{id}/body`, and `/api/requests/{id}/accept`.

**Rationale:**
- Resource-oriented URLs are easier to understand than action-based endpoints
- `POST` already implies creation, so `/create` is unnecessary
- `PATCH` is better than `PUT` for partial updates of a single field such as `body`
- State transitions are expressed as sub-resources such as `/verify` and `/approve`
- Query parameters keep listing and filtering concerns explicit and simple

### 17. HTTP Status Codes

**Assumption:** Returning appropriate HTTP status codes for different scenarios.

**Standard Usage:**
- `200 OK` - Successful read or partial update
- `201 Created` - Successful create
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `409 Conflict` - State transition or concurrent modification error
- `500 Internal Server Error` - Unexpected errors

## 📦 Package Structure

### 18. Clear Layer Separation

**Assumption:** Strict package organization reflecting hexagonal architecture and separation of concerns.

**Structure:**
```
com.codinggame.applicationrequest/
├── domain/
│   ├── model/           # Aggregates and state model
│   └── exception/       # Domain-specific exceptions
├── application/
│   ├── dto/             # Application DTOs
│   ├── port/
│   │   ├── in/          # Input ports and command contracts
│   │   └── out/         # Output ports and repository contracts
│   ├── service/         # Use case orchestration
│   └── mapper/          # Mappers between domain and DTOs
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
                └── adapter-like implementation classes
```

**Benefits:**
- Enforces architectural boundaries between business logic and infrastructure
- Keeps REST/http concerns separate from domain rules
- Makes dependency direction easy to reason about

## 🔍 Validation Strategy

### 19. Layered Validation

**Assumption:** Validation happens at multiple layers.

**Layers:**
1. **HTTP Layer** - Jakarta Validation annotations on DTOs (`@NotBlank`, `@NotNull`)
2. **Domain Layer** - Business rule validation in aggregate methods
3. **Service Layer** - Optional validation for use case contracts

**Rationale:**
- Defense in depth
- HTTP layer catches malformed input early
- Domain layer ensures invariants
- Not redundant - different responsibilities

**Example:**
```
Request → @Valid on @RequestBody → Domain model validation → Service orchestration
```

### 20. Blank String Handling

**Assumption:** Blank strings (null, empty, whitespace-only) are treated the same.

**Implementation:**
```java
if (name == null || name.isBlank()) {
    throw new InvalidRequestNameException(...);
}
```

**Rationale:**
- Whitespace-only is not useful
- Consistent with JSR-380 `@NotBlank` semantics

## 🎯 Future Extensibility

### 21. Ready for Optional Features (Not Implemented)

**Designed but not implemented:**
- **Event sourcing** - `domain/event/` structure exists
- **Domain events** - ApplicationRequest can publish events (future work)
- **API versioning** - Package structure allows `/v2/requests`
- **Role-based access** - Can be added to controller without changing service
- **Request templates** - New aggregate type can be added

## ⚙️ Configuration

### 22. Spring Boot Configuration

**Assumption:** Using Spring Boot conventions for configuration.

**Files:**
- `application.yml` - Main configuration
- `application-test.yml` - Test overrides
- Properties for H2, JPA, and SQL initialization

**Convention:**
- Properties not explicitly set use Spring defaults
- Embedded H2 used without external configuration

## 🚫 Limitations & Trade-offs

### 23. No Authentication/Authorization

**Assumption:** Application does not implement security/auth.

**Rationale:**
- Not specified in requirements
- Can be added via Spring Security without changing domain logic
- Hexagonal architecture makes this a pure infrastructure concern

### 24. No Soft Deletes

**Assumption:** Delete operations are physical (data removed from DB).

**Rationale:**
- Simpler state machine (DELETED is terminal state)
- Audit log provides delete history
- If compliance requires soft deletes, audit log serves that purpose

**Alternative considered:** Soft deletes with `is_deleted` column - rejected as complicating state machine

### 25. Single-Tenancy

**Assumption:** Application serves single tenant (no multi-tenancy).

**Rationale:**
- Not specified in requirements
- Can be added at infrastructure layer (row-level security)

### 26. No API Rate Limiting

**Assumption:** No rate limiting or throttling implemented.

**Rationale:**
- Not specified in requirements
- Can be added via Spring Cloud Gateway or Spring Security

## 📚 Documentation

### 27. Inline Comments Only for Non-Obvious Code

**Assumption:** Code is self-documenting; comments only for complex logic.

**Rationale:**
- Method names express intent
- Domain logic is domain language
- Comments easily become stale
- README.md has high-level overview

**Exception:** Schema initialization comments may be used if SQL setup becomes non-obvious

## 🔄 Version and Release Management

### 28. Single Version Across Application

**Assumption:** No separate versioning for API, domain, or components.

**Configuration:**
- Version in `pom.xml`: 1.0.0-SNAPSHOT
- All modules inherit this version

**Rationale:**
- Monolithic application (not microservices)
- Simpler deployment and versioning

## Summary

All decisions prioritize:
1. **Correctness** - Business rules enforced in domain
2. **Maintainability** - Clear separation, testable design
3. **Simplicity** - Not over-engineering; hexagonal architecture without unnecessary complexity
4. **Extensibility** - Structure allows future enhancements without breaking current design

These assumptions create a solid foundation for the Request Management Application that can grow and evolve while maintaining architectural integrity.

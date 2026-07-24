# MediCare API

Hospital management backend built with **Java 25 + Spring Boot 4 + Gradle**. It manages users and roles (Admin, Doctor, Patient), medical appointments with availability validation, clinical history in MongoDB, and integration with an external drug API (OpenFDA) protected by a Circuit Breaker.

## Tech Stack

| Component | Technology |
|---|---|
| Framework | Spring Boot 4.0.6 |
| Language | Java 25 |
| Build tool | Gradle 8.x+ |
| Relational DB | PostgreSQL (Docker) |
| NoSQL DB | MongoDB (Docker) |
| Migrations | Flyway |
| Security | Spring Security + JWT |
| DTO mapping | MapStruct |
| Resilience | Resilience4j Circuit Breaker |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| Logging | Log4j2 |
| Testing | JUnit 5 + Mockito + MockMvc (H2 for tests) |

## Prerequisites

- JDK 25 (`java -version`)
- Docker + Docker Compose v2 (`docker --version`)

## Getting Started

### 1. Start the databases

The `compose.yaml` file lives inside the `medicare/` module:

```bash
cd medicare
docker compose up -d
docker compose ps   # both services should show "Up"
```

### 2. Run the application

```bash
./gradlew bootRun
```

The API starts at `http://localhost:8080`. Flyway runs the migrations automatically on startup.

### 3. Explore the API

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **Postman:** import `Medicare-API.postman_collection.json` (repo root)

## Running Tests

Tests run on an in-memory **H2** database — Docker is **not** required:

```bash
./gradlew test
```

## Authentication Flow

1. `POST /api/auth/register` → creates a PATIENT and returns a JWT
2. `POST /api/auth/login` → returns a JWT
3. Send the token in every protected request: `Authorization: Bearer <token>`

## Endpoints

### Auth (public)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a patient, returns JWT |
| POST | `/api/auth/login` | Log in, returns JWT |

### Admin (ADMIN role)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/users` | Create a user with any role |
| POST | `/api/admin/doctors` | Create a doctor profile |
| GET | `/api/admin/doctors` | List all doctors |
| GET | `/api/admin/patients` | List all patients |

### Appointments

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/api/appointments` | PATIENT | Book an appointment (409 if the doctor is busy) |
| GET | `/api/appointments/mine` | PATIENT/DOCTOR | List my appointments |
| PATCH | `/api/appointments/{id}/cancel` | PATIENT | Cancel (> 24h in advance, owner only) |

### Medical Notes

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/api/appointments/{id}/note` | DOCTOR | Add a clinical note (assigned doctor only) |
| GET | `/api/patients/{id}/records` | PATIENT/ADMIN | Get the patient's clinical history |

## Example Usage (curl)

```bash
# Register a patient
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@test.com", "password": "password1234"}'

# Log in
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "john@test.com", "password": "password1234"}'

# Book an appointment (replace <TOKEN> and doctor id)
curl -X POST http://localhost:8080/api/appointments \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"doctorId": 1, "dateTime": "2026-12-01T10:00:00"}'
```

## Error Format

Errors follow **RFC 7807 (Problem Details)**:

```json
{
  "type": "about:blank",
  "title": "Appointment Conflict",
  "status": 409,
  "detail": "Appointment already exists",
  "instance": "/api/appointments"
}
```

## Project Structure

```
medicare/
├── compose.yaml            # PostgreSQL + MongoDB
├── build.gradle
└── src/
    ├── main/java/com/higuitar/medicare/
    │   ├── config/         # Security, OpenAPI, JPA auditing
    │   ├── controller/     # REST endpoints
    │   ├── dto/            # request/response records
    │   ├── exception/      # Custom exceptions + RFC 7807 handler
    │   ├── integration/    # OpenFDA client (Circuit Breaker)
    │   ├── model/          # JPA entities + MongoDB documents
    │   ├── repository/     # jpa/ and mongo/ repositories
    │   ├── security/       # JWT filter and service
    │   ├── service/        # Business logic (interfaces + impl/)
    │   └── util/mapper/    # MapStruct mappers
    ├── main/resources/
    │   ├── application.yaml
    │   ├── log4j2-spring.xml
    │   └── db/migration/   # Flyway migrations
    └── test/               # Unit + integration tests (H2)
```

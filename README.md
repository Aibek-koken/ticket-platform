# Smart Ticket Platform

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Redis-Reservations-DC382D?logo=redis&logoColor=white" alt="Redis"/>
  <img src="https://img.shields.io/badge/Apache_Kafka-Events-231F20?logo=apachekafka&logoColor=white" alt="Kafka"/>
  <img src="https://img.shields.io/badge/PostgreSQL-Flyway-4169E1?logo=postgresql&logoColor=white" alt="PostgreSQL"/>
</p>

**High-load ticketing learning backend built around temporary reservations, optimistic locking, and event-driven purchase processing.**

The project models one of the hardest ticketing problems: many users competing for the same seat while slow follow-up work should not block the purchase request.

## High-load building blocks

- **Redis TTL reservation** — a seat can be held temporarily for 15 minutes.
- **Optimistic locking** — the `Seat` entity uses JPA `@Version` to detect conflicting updates.
- **Kafka events** — successful purchases publish keyed `ticket-events`.
- **Async processing** — a Kafka consumer handles the slow post-purchase flow separately from the HTTP request.
- **PostgreSQL + Flyway** — versioned relational schema for events, seats, and orders.
- **Docker Compose** — local Redis and Kafka infrastructure.
- **OpenAPI / Swagger** — API exploration through Springdoc.

## Architecture

```mermaid
flowchart LR
    A[Client] --> B[Ticket REST API]
    B --> C[TicketService]
    C --> D[(Redis<br/>15 min reservation)]
    C --> E[(PostgreSQL<br/>seat state + @Version)]
    C --> F[Kafka Producer]
    F --> G[ticket-events]
    G --> H[Kafka Consumer]
    H --> I[PDF / email workflow]
```

## Main endpoints

```text
POST /api/v1/tickets/reserve/{seatId}
POST /api/v1/tickets/buy/{seatId}
```

## Run locally

Start Redis and Kafka:

```bash
docker compose up -d
```

Make sure PostgreSQL is available at `localhost:5432` with database `ticket_db`, then run:

```bash
./gradlew bootRun
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

> This repository is a learning prototype for high-load and event-driven patterns. The current Kafka consumer simulates PDF generation and email delivery rather than integrating real external services.

# WalletX — Fintech Wallet REST API

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=flat-square)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square)
![Redis](https://img.shields.io/badge/Redis-7-red?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=flat-square)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow?style=flat-square)

A production-grade fintech wallet backend built with Spring Boot. Supports user 
registration, JWT-secured wallet operations (credit/debit), paginated transaction 
history, Redis-cached balances, and full Swagger documentation.

---

## Features

- User registration with BCrypt password hashing
- JWT-based stateless authentication
- Wallet creation (auto-assigned on registration)
- Credit / Debit operations with transactional integrity
- Full transaction ledger with pagination
- Redis caching on wallet balance (5-min TTL, auto-evicted on mutation)
- Spring Retry on all write operations (3 attempts, 500ms backoff)
- Global exception handler with consistent API error responses
- Swagger UI for live API exploration
- Dockerised — runs with one command

---

## Architecture

```
┌──────────────┐     JWT      ┌─────────────────────────────────────┐
│   Client     │ ──────────▶  │         Spring Boot App             │
│  (Postman/   │              │                                     │
│   Frontend)  │              │  AuthController  WalletController  │
└──────────────┘              │       │                │            │
                              │  UserService     WalletService     │
                              │       │           │        │        │
                              │  UserRepo    WalletRepo  TxnRepo   │
                              └───────┼───────────┼────────┼───────┘
                                      │           │        │
                                   MySQL       MySQL    Redis
                                  (users)    (wallets) (cache)
                                           (transactions)
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (JJWT) |
| Database | MySQL 8 + Spring Data JPA / Hibernate |
| Cache | Redis 7 + Spring Cache |
| Retry | Spring Retry |
| Docs | Swagger / OpenAPI 3 (SpringDoc) |
| Build | Maven |
| Container | Docker + Docker Compose |
| Testing | JUnit 5 + Mockito |

---

## Getting Started

### Prerequisites
- Java 17
- Maven
- Docker & Docker Compose

### Run with Docker (recommended)

```bash
git clone https://github.com/YOUR_USERNAME/walletx.git
cd walletx
docker-compose up --build
```

App starts at `http://localhost:8080`

### Run locally

```bash
# Start MySQL and Redis first, then:
mvn spring-boot:run
```

---

## API Reference

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Auth

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/users/register` | Register new user |
| POST | `/api/v1/auth/login` | Login and receive JWT |

### Wallet

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/wallets/{walletId}/balance` | ✅ | Get wallet balance |
| POST | `/api/v1/wallets/credit` | ✅ | Credit wallet |
| POST | `/api/v1/wallets/debit` | ✅ | Debit wallet |
| GET | `/api/v1/wallets/{walletId}/transactions` | ✅ | Paginated transaction history |

### Sample Request — Login

```json
POST /api/v1/auth/login
{
  "email": "melvin@test.com",
  "password": "test123"
}
```

### Sample Response

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "melvin@test.com",
    "fullName": "Melvin Christ",
    "walletId": "WLT-00001"
  }
}
```

---

## Key Design Decisions

**Transactional integrity** — every credit/debit uses `@Transactional` with rollback on failure, ensuring the ledger and wallet balance are never out of sync.

**Redis cache eviction** — balance reads are cached with a 5-minute TTL. Any write operation (`@CacheEvict`) immediately invalidates the cached entry, so stale reads are impossible.

**Stateless auth** — JWT tokens carry all session state. No server-side sessions, making the service horizontally scalable.

**Consistent API responses** — every endpoint returns `ApiResponse<T>` with `success`, `message`, and `data` fields, making frontend integration straightforward.

---

## Author

**Melvin Christ** — Java Backend Developer  
[LinkedIn](https://www.linkedin.com/in/melvin-christ/) · [GitHub](https://github.com/YOUR_USERNAME)

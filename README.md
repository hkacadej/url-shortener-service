#  URL Shortening Service

A secure and efficient URL shortening service built with **Spring Boot**, **JWT-based authentication**, and **Kafka** for asynchronous operations.

## Overview

This service allows authenticated users to:

* Shorten long URLs.
* Retrieve original URLs from shortened versions.
* Track clicks for each shortened URL.
* View and manage all shortened URLs.
* Customize or reset expiration times.

All operations—except login and registration—are **secured** using **Spring Security** and **JWT**.

## Authentication

The service exposes two **public endpoints** for user management:

* `POST /api/auth/v1/register` — Register a new user.
* `POST /api/auth/v1/login` — Authenticate with email and password to receive a JWT token.

>  All other endpoints require a valid JWT token in the `Authorization: Bearer <token>` header.

## Core Features

### Authenticated Access

Only authenticated users can:

* Create new shortened URLs.
* Access all existing shortened URLs.
* View and overwrite expiration times.
* View click statistics for any URL.

### Intelligent URL Deduplication

* If the submitted long URL already exists and hasn’t expired, the existing short URL is returned.
* Its expiration time is **reset** (refreshed).

### Expiration Handling

Each shortened URL includes an **expiration time**. After expiration, it becomes invalid until recreated or reset.

### Click Tracking via Kafka

Every time a short URL is accessed, the system increments the click count **asynchronously** using **Apache Kafka**.

### Swagger UI

For demo purposes, all endpoints are documented and exposed (without auth) via **Swagger UI**:

* Visit: [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)

---

## API Endpoints

### Authentication

#### `POST /api/auth/v1/register`

Registers a new user.

**Request Body:**

```json
{
  "email": "user@example.com",
  "name": "John Doe",
  "password": "securePassword"
}
```

**Response:**

```json
{
  "accessToken": "jwt-token-here"
}
```

---

#### `POST /api/auth/v1/login`

Logs in an existing user.

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "securePassword"
}
```

**Response:**

```json
{
  "accessToken": "jwt-token-here"
}
```

---

### ️ URL Shortening

#### `POST /api/v1/shorten`

Shortens a given long URL or reuses an existing one.

**Headers:**

```
Authorization: Bearer <JWT>
```

**Request Body:**

```json
{
  "url": "https://www.example.com/very/long/url"
}
```

**Response:**

```json
{
  "shortUrl": "http://localhost:8080/r/abc123",
  "expirationDate": "2025-06-30T12:00:00Z"
}
```

---

#### `GET /api/v1/urls`

Returns all shortened URLs for the authenticated user.

**Headers:**

```
Authorization: Bearer <JWT>
```

**Response:**

```json
[
  {
    "shortUrl": "http://localhost:8080/r/abc123",
    "originalUrl": "https://example.com",
    "clickCount": 42
  },
  {
    "shortUrl": "http://localhost:8080/r/xyz789",
    "originalUrl": "https://another-example.com",
    "clickCount": 12
  }
]
```

---

###  URL Redirect

#### `GET /r/{id}`

Redirects to the original long URL corresponding to the shortened ID and triggers click count increment via Kafka.

**Path Parameter:**

* `id` — The shortened ID (e.g., `abc123`)

---

## ️ Security Summary

| Endpoint                | Authentication Required       |
| ----------------------- | ----------------------------- |
| `/api/auth/v1/register` | ❌ No                          |
| `/api/auth/v1/login`    | ❌ No                          |
| `/api/v1/shorten`       | ✅ Yes                         |
| `/api/v1/urls`          | ✅ Yes                         |
| `/r/{id}`               | ❌ No                          |
| `/swagger-ui/**`        | ❌ No (for demonstration only) |

---

## ️ Tech Stack

* **Spring Boot** - Core backend framework
* **Spring Security + JWT** - Authentication & authorization
* **Apache Kafka** - Asynchronous click tracking
* **Swagger / OpenAPI** - API documentation
* **PostgreSQL** - URL persistence 
* **Docker** - Containerization 
* **Angular** - UI

---



## Angular UI Overview

The minimal Angular frontend provides:

* **Login/Register pages**
* **Form to submit long URLs**
* **Display of shortened URL with copy functionality**
* **Clickable links that redirect to original URLs**
* **List of all shortened URLs** with click stats

> The frontend communicates securely with the backend using JWTs.


### Access Swagger UI

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

# Deployment Steps

This application depends on **PostgreSQL** and **Kafka** to run properly.

You can either:

* Run the entire system **automatically** using the provided `docker-compose.yml`,
* Or **manually start** the required containers and configure your environment.

---

## ⚙️ Prerequisites

1. Create a `.env` file in the root directory of your project with the following environment variables:

```env
JWT_SECRET=TXlTZWNyZXRLZXlNeVNlY3JldEtleQ==
JWT_EXPIRATION=600000
DB_URL=jdbc:postgresql://postgres-db:5432/urlshortener
DB_USER=myuser
DB_PASS=mypassword
KAFKA_SERVER=kraft-kafka:9092
KAFKA_GROUP_ID=kraft-group
URL_EXP_MIN=5
URL_ORIGIN=http://localhost:8080
URL_ENDPOINT=/r/
CORS_ORIGIN=http://localhost
```

> **Note:**
>
> * Adjust these properties to match your environment or deployment setup.
> * `JWT_SECRET` is a base64-encoded secret key for signing JWT tokens.
> * `DB_URL` points to the PostgreSQL container hostname `postgres-db` (as defined in Docker Compose).
> * `KAFKA_SERVER` points to Kafka’s advertised hostname and port.

---

## 🚀 Running with Docker Compose (Recommended)

From the root directory of the project, run:

```bash
docker-compose up --build -d
```

* This will build (if necessary) and start all required services:

    * **PostgreSQL** database
    * **Kafka** broker
    * Your application backend and frontend
* Containers will run in the background (`-d` for detached mode).

To stop all containers, run:

```bash
docker-compose down
```

---

## 🛠 Manual Startup

If you want more control or need to start dependencies manually, follow these steps:

### 1. Start Kafka Container

```bash
docker run -d \
  --name kraft-kafka \
  -p 9092:9092 \
  -p 9093:9093 \
  -e KAFKA_KRAFT_BROKER_ID=1 \
  -e KAFKA_CFG_NODE_ID=1 \
  -e KAFKA_CFG_PROCESS_ROLES=broker,controller \
  -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=true \
  -e KAFKA_CFG_LOG_DIRS=/bitnami/kafka/data \
  -v kraft-logs:/bitnami/kafka/data \
  bitnami/kafka:3.6
```

### 2. Start PostgreSQL Container

```bash
docker run -d \
  --name postgres-db \
  -p 5432:5432 \
  -e POSTGRES_DB=urlshortener \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=mypassword \
  -v pgdata:/var/lib/postgresql/data \
  postgres:15
```

---

## ⚙️ Adjust Environment Variables for Local Setup

If running manually (without Docker Compose), update your `.env` or environment file to reflect local hostnames (You can use directly local.env):

```env
JWT_SECRET=TXlTZWNyZXRLZXlNeVNlY3JldEtleQ==
JWT_EXPIRATION=600000
DB_URL=jdbc:postgresql://localhost:5432/urlshortener
DB_USER=myuser
DB_PASS=mypassword
KAFKA_SERVER=http://localhost:9092
KAFKA_GROUP_ID=kraft-group
URL_EXP_MIN=5
URL_ORIGIN=http://localhost:8080
URL_ENDPOINT=/r/
CORS_ORIGIN=http://localhost:4200
```

---

## 🎉 Final Step: Run Application and Frontend

Once Kafka and PostgreSQL are running and your environment variables are properly set, start your backend and Angular frontend applications:

* Backend (URL shortening service) will connect to Kafka and PostgreSQL using the configured environment.
* Angular frontend will interact with the backend via the exposed endpoints.

---


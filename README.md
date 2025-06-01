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
* **PostgreSQL / MySQL** - URL persistence (your choice)
* **Docker** - Containerization (optional)
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

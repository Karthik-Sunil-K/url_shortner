
# URL Shortener Service

A simple URL shortening service built with Spring Boot, Spring Data JPA, and PostgreSQL. This service allows users to shorten long URLs, track clicks, and fetch statistics for each shortened URL.

---

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Setup & Installation](#setup--installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [API Testing](#api-testing)
- [Sample Usage](#sample-usage)
- [Project Structure](#project-structure)
- [License](#license)

---

## Features
- Generate a short URL for any valid original URL
- Redirect to original URL using the short key
- Track click counts for each URL
- Retrieve statistics for each shortened URL
- Display remaining URL creation capacity

---

## Tech Stack
- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- HikariCP (Connection Pool)
- Maven (Build tool)
- Render / Docker (Deployment)

---

## Setup & Installation

Clone the repository:

```bash
git clone https://github.com/yourusername/url-shortener.git
cd url-shortener
```

Build the project using Maven:

```bash
mvn clean install
```

Ensure PostgreSQL is running and create a database:

```sql
CREATE DATABASE url_shortener_db;
```

---

## Configuration

Configure database credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/url_shortener_db
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Running the Application

**Locally:**

```bash
mvn spring-boot:run
```

Service will start on [http://localhost:10000](http://localhost:10000)

**Deployed on Render:**

Available at [https://url-shortner-isa7.onrender.com](https://url-shortner-isa7.onrender.com)

---

## API Endpoints

| Method | Endpoint | Description | Parameters | Response |
|--------|---------|------------|-----------|---------|
| POST   | `/api/url/create` | Create a short URL | `originalUrl` (query param) | JSON with `id`, `shortKey`, `originalUrl`, `clickCount` |
| GET    | `/api/url/{shortKey}` | Redirect to original URL | `shortKey` (path) | HTTP 302 redirect |
| GET    | `/api/url/total` | Get remaining URL creation capacity | None | long value of remaining URLs |
| GET    | `/api/url/stats/{shortKey}` | Get statistics of a short URL | `shortKey` (path) | JSON with `shortKey`, `originalUrl`, `clickCount` |

---

## API Testing

### Manual Testing with Browser or Postman

- **Create Short URL:**  
`POST http://localhost:10000/api/url/create?originalUrl=https://www.linkedin.com/feed/`

- **Redirect:**  
`GET http://localhost:10000/api/url/{shortKey}` → Redirects to original URL

- **Get Stats:**  
`GET http://localhost:10000/api/url/stats/{shortKey}`

- **Remaining URLs:**  
`GET http://localhost:10000/api/url/total`

### Using cURL

```bash
# Create short URL
curl -X POST "http://localhost:10000/api/url/create?originalUrl=https://www.linkedin.com/feed/"

# Redirect
curl -v "http://localhost:10000/api/url/{shortKey}"

# Get Stats
curl "http://localhost:10000/api/url/stats/{shortKey}"

# Remaining URLs
curl "http://localhost:10000/api/url/total"
```

### Automated Testing (JUnit + MockMvc)

```java
@WebMvcTest(UrlMappingController.class)
class UrlMappingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlMappingService service;

    @Test
    void testCreateShortUrl() throws Exception {
        when(service.createShortUrl("https://example.com"))
                .thenReturn(new UrlMapping(1L, "abc123", "https://example.com", 0L));

        mockMvc.perform(post("/api/url/create")
                .param("originalUrl", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortKey").value("abc123"));
    }

    @Test
    void testRedirectToOriginal() throws Exception {
        var mapping = new UrlMapping(1L, "abc123", "https://example.com", 0L);
        when(service.getByShortKey("abc123")).thenReturn(Optional.of(mapping));

        mockMvc.perform(get("/api/url/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }
}
```

---

## Sample Usage

**Create Short URL**

```http
POST /api/url/create?originalUrl=https://www.linkedin.com/feed/
```

**Response:**

```json
{
  "id": 1,
  "shortKey": "abc123",
  "originalUrl": "https://www.linkedin.com/feed/",
  "clickCount": 0
}
```

**Visit Short URL**

```http
GET /api/url/abc123
```

Redirects to: `https://www.linkedin.com/feed/`

**Get Stats**

```http
GET /api/url/stats/abc123
```

**Response:**

```json
{
  "shortKey": "abc123",
  "originalUrl": "https://www.linkedin.com/feed/",
  "clickCount": 1
}
```

**Check Remaining URLs**

```http
GET /api/url/total
```

**Response:**

```
14776335
```

---

## Project Structure

```
src/main/java/com/example/shorturl/
├── controller/
│   └── UrlMappingController.java
├── model/
│   └── UrlMapping.java
├── repository/
│   └── UrlMappingRepository.java
├── service/
│   └── UrlMappingService.java
└── ShorturlApplication.java
```

---

## License

This project is licensed under the MIT License.

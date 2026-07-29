# LocalHelp Backend

RESTful API backend for **LocalHelp** — a community platform connecting people who need help with local jobs/tasks to people who can help.

## Tech Stack

- **Java 21**
- **Spring Boot 3.5** — Web, Validation, Data JPA, Security, OAuth2 Resource Server, WebSocket, Cache
- **MySQL 8** — primary datastore
- **Redis** — caching layer
- **Firebase Admin SDK** — authentication (Firebase Auth) & Firebase Cloud Storage
- **Cloudinary** — media/image uploads
- **Spring Cloud OpenFeign** — inter-service HTTP clients
- **MapStruct** — DTO/entity mapping
- **springdoc-openapi** — OpenAPI/Swagger UI
- **Docker / Docker Compose** — containerized runtime

## Project Structure

```
src/main/java/vn/localhelp/core
├── config          # Spring configuration (Security, Firebase, Redis, CORS, etc.)
├── controller       # REST controllers (Auth, Job, Category, Conversation, Finance, Review, User, ...)
├── domain
│   ├── entity        # JPA entities
│   ├── request        # Request DTOs
│   ├── response        # Response DTOs
│   └── mapper         # MapStruct mappers
├── repository        # Spring Data JPA repositories
├── service          # Business logic
├── specification      # JPA Specifications for dynamic queries
└── util            # Utility classes
```

## Getting Started

### Prerequisites

- JDK 21+
- Maven (or use the included `mvnw` wrapper)
- Docker & Docker Compose (recommended for running MySQL/Redis locally)

### 1. Clone the repository

```bash
git clone https://github.com/thanhthbm/local-help-backend.git
cd local-help-backend
```

### 2. Configure environment variables

Create a `.env` file in the project root (see `application.properties` for the full list of variables consumed). At minimum you'll need:

```env
SPRING_APPLICATION_NAME=local-help-backend
SERVER_PORT=3636

SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3307/local_help_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect

SERVER_ERROR_INCLUDE_MESSAGE=always
SERVER_ERROR_INCLUDE_BINDING_ERRORS=always

FIREBASE_AUTH_URL=https://identitytoolkit.googleapis.com/v1
FIREBASE_API_KEY=your_firebase_api_key
FIREBASE_CREDENTIAL_PATH=env/your-firebase-adminsdk.json
FIREBASE_STORAGE_BUCKET=your_project.appspot.com

SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
UPLOAD_PRESET=localhelp_preset

SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
```

Place your Firebase Admin SDK service-account JSON under `env/` (this directory is git-ignored).

### 3. Run infrastructure (MySQL + Redis)

```bash
docker compose up -d mysql redis
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:3636`.

### Run with Docker Compose (full stack)

```bash
docker compose up -d
```

## API Documentation

Once the application is running, Swagger UI is available at:

```
http://localhost:3636/swagger-ui.html
```

## Building

```bash
./mvnw clean package
```

## Running Tests

```bash
./mvnw test
```

## License

This project is proprietary and intended for the LocalHelp platform.

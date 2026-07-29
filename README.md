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

## Phân tích thiết kế hệ thống

### 1. Kiến trúc tổng quan

Backend được tổ chức theo **kiến trúc phân lớp (layered architecture)** truyền thống của Spring Boot:

```
Client (Android app)
      │  HTTPS (REST) / WSS (STOMP)
      ▼
Controller           → nhận request, validate DTO, không chứa business logic
      │
      ▼
Service               → xử lý nghiệp vụ, transaction, gọi các service ngoài (Firebase/Cloudinary)
      │
      ▼
Repository (Spring Data JPA) → truy vấn MySQL, kết hợp Specification cho filter động
      │
      ▼
MySQL 8
```

Giao tiếp giữa các tầng đi qua DTO (`request`/`response`) được MapStruct map sang/từ entity, giúp tách biệt hoàn toàn model dùng cho API và model dùng cho database — entity không bao giờ bị serialize thẳng ra ngoài.

### 2. Xác thực & phân quyền

Hệ thống **không tự quản lý mật khẩu** mà ủy quyền toàn bộ việc đăng nhập/đăng ký cho **Firebase Authentication**:

1. Android app đăng nhập qua Firebase SDK, nhận về Firebase ID Token (JWT).
2. Token được gửi lên backend qua header `Authorization: Bearer <token>`.
3. `FirebaseAuthFilter` (một `OncePerRequestFilter` chạy trước `UsernamePasswordAuthenticationFilter`) xác minh token bằng Firebase Admin SDK, tra `firebase_uid` trong bảng `users` để lấy hồ sơ và `role`, rồi nạp `CustomUserDetails` vào `SecurityContext`.
4. `SecurityConfiguration` khai báo các endpoint public (`/api/auth/**`, `GET /api/jobs/**`, `/icons/**`, `/images/**`...), còn lại yêu cầu đã xác thực; phân quyền chi tiết theo `ROLE_USER` / `ROLE_ADMIN` dùng `@PreAuthorize` (bật qua `@EnableMethodSecurity`).
5. Kết nối WebSocket (STOMP) cũng được xác thực Firebase token riêng tại thời điểm `CONNECT` trong `WebSocketConfig`, độc lập với filter chain HTTP.

Cách tiếp cận này giúp backend không phải lưu trữ/băm mật khẩu, tận dụng các cơ chế bảo mật (OTP, social login, rate-limit) có sẵn của Firebase, đồng thời vẫn giữ được authorization theo domain (role, ownership) ở phía Spring Security.

### 3. Mô hình dữ liệu

Các entity chính và quan hệ giữa chúng:

```
User 1───* Job (creator)          User 1───* Job (helper)
User 1───* JobApplication (helper)      Job 1───* JobApplication
JobApplication 1───* Progress            Job 1───* JobImage
Job 1───1 Review                    User 1───* Review (reviewer / reviewee)
Category 1───* Job                  User 1───* Notifications
User *───* User qua Conversation (user1, user2)
```

Một vài quyết định thiết kế đáng chú ý:

- **`Job` là entity trung tâm**: một job có thể nhận nhiều `JobApplication` (nhiều helper ứng tuyển) nhưng chỉ một application được `creator` chấp nhận. Vòng đời job (`JobStatus`) và vòng đời application (`JobProgress`) được tách thành hai enum riêng vì một job có thể bị nhiều helper "APPLIED" cùng lúc trong khi bản thân job vẫn ở trạng thái `OPEN`.
- **`Progress` là bảng lịch sử (audit trail)**: mỗi lần `JobApplication` đổi trạng thái (`APPLIED → ACCEPTED → ON_THE_WAY → WORKING → PENDING_PAYMENT → COMPLETED`, hoặc `CANCELLED`/`REJECTED`) sẽ sinh thêm một bản ghi `Progress`, phục vụ hiển thị timeline cho người dùng thay vì chỉ lưu trạng thái hiện tại.
- **`Review` ràng buộc 1-1 với `Job`** (`job_id` UNIQUE) để đảm bảo mỗi công việc chỉ được đánh giá đúng một lần; `reviewer`/`reviewee` tách riêng để tính điểm uy tín (`reputationScore`) hai chiều (người thuê và người làm đều có thể được đánh giá).
- **`Conversation` dùng UUID (String) làm khóa chính** thay vì ID tăng dần, vì ID này được dùng trực tiếp làm document ID trên Firebase Firestore — nơi thực sự lưu trữ tin nhắn chat. UUID vừa đảm bảo duy nhất toàn cục vừa tránh bị đoán ID.
- **`JobImage` phân loại theo `ImageType`** (`REQUEST` cho ảnh mô tả công việc, `PROOF` cho ảnh minh chứng hoàn thành), dùng chung một bảng thay vì tách hai bảng vì chung schema và vòng đời (cascade theo `Job`).

### 4. Thời gian thực (Realtime)

Hệ thống tách bạch rõ hai loại dữ liệu realtime, mỗi loại dùng một cơ chế phù hợp:

- **Chat 1-1**: xử lý trực tiếp qua **Firebase Firestore SDK** trên Android, backend chỉ tạo/quản lý `Conversation` (metadata) chứ không đứng giữa relay tin nhắn — giảm tải cho backend và tận dụng khả năng đồng bộ offline sẵn có của Firestore.
- **Thông báo hệ thống (notification)**: đẩy qua **WebSocket (STOMP over SockJS)**, endpoint `/ws-notifications`, dùng broker `/topic` (broadcast) và `/user` (gửi riêng theo user) — ví dụ khi có người ứng tuyển, khi job được chấp nhận, v.v.

### 5. Caching

**Redis** được dùng như cache layer (Spring Cache abstraction, TTL mặc định 30 phút, serialize JSON qua `GenericJackson2JsonRedisSerializer`) cho các dữ liệu đọc nhiều/ít đổi như danh mục (`Category`) và các truy vấn tổng hợp ở `FinanceService`, giúp giảm tải truy vấn MySQL cho các API được gọi lại nhiều lần.

### 6. Lưu trữ media

Ảnh (avatar, ảnh mô tả job, ảnh minh chứng hoàn thành) được upload lên **Cloudinary**, backend chỉ lưu URL trong `avatar_url` / `job_images.image_url` chứ không lưu file nhị phân — giúp backend stateless, dễ scale ngang, và tận dụng CDN + resize ảnh có sẵn của Cloudinary. Firebase Storage được cấu hình song song cho một số luồng liên quan trực tiếp tới Firebase.

### 7. Triển khai (Deployment)

Ứng dụng được đóng gói bằng Docker, chạy cùng MySQL và Redis qua `docker-compose.yml`. Image backend được build và publish lên GitHub Container Registry (`ghcr.io/thanhthbm/local-help-backend`), triển khai theo mô hình 3 container độc lập (backend / mysql / redis) trên cùng một Docker network, với `depends_on` + healthcheck đảm bảo backend chỉ khởi động sau khi MySQL sẵn sàng.

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

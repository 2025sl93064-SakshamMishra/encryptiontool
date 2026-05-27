# KT Encryption-Decryption Tool

A full-stack encryption/decryption tool built as a college FSAD project. The backend is a REST API that supports text encryption, email encryption, file export encryption, reporting, and user authentication — all protected with JWT.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security 7 + JWT (jjwt 0.12.6) |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| Excel | Apache POI 5.3.0 |
| Email | Spring Mail + Gmail SMTP |
| Utilities | Lombok |

---

## Requirements Implemented

| # | Feature | Status |
|---|---|---|
| 1 | User Registration & Login (JWT auth) | Done |
| 2 | Text Encryption (multi-algorithm) | Done |
| 3 | Text Decryption (multi-algorithm) | Done |
| 4 | Email — send encrypted message | Done |
| 5 | Email — decrypt received message | Done |
| 6 | Excel export — export + encrypt user data | Done |
| 7 | Excel export — decrypt .enc back to .xlsx | Done |
| 8 | Reports — summary counts | Done |
| 9 | Reports — full operation history | Done |
| 10 | Reports — file record listing | Done |

---

## Supported Encryption Algorithms

| Algorithm | Key | Mode | Use case |
|---|---|---|---|
| AES-256-CBC | 256-bit | CBC + random IV | Default for all operations |
| AES-128-CBC | 128-bit | CBC + random IV | Lighter alternative |
| 3DES-CBC | 168-bit | CBC + random IV | Legacy support |
| RSA-2048 | 2048-bit keypair | PKCS1 v1.5 | Asymmetric text encryption |

All symmetric algorithms prepend a random IV to the ciphertext before Base64 encoding. RSA chunks input into 245-byte blocks to handle the block size limit.

---

## Project Structure

```
src/main/java/com/example/encryptiontool/
│
├── config/
│   ├── AesConfig.java               # SecretKey beans for AES-128 and AES-256
│   ├── RsaConfig.java               # RSA KeyPair bean
│   └── SecurityConfig.java          # Spring Security filter chain, JWT wiring
│
├── controller/
│   ├── BaseController.java          # Shared getLoggedInUser() for all controllers
│   ├── authentication/
│   │   └── AuthController.java      # POST /auth/signup, POST /auth/login
│   ├── encryption/
│   │   └── TextEncryptionController.java  # POST /api/encrypt/text, POST /api/decrypt/text
│   ├── email/
│   │   └── EmailController.java     # POST /api/email/send-encrypted, POST /api/email/decrypt
│   ├── export/
│   │   └── ExportController.java    # GET /api/export/encrypt, POST /api/export/decrypt
│   └── report/
│       └── ReportController.java    # GET /api/reports/summary, /history, /files
│
├── dto/
│   ├── ApiResponse.java             # Generic response envelope {success, message, data}
│   ├── AuthResponse.java            # Login response {token}
│   ├── LoginRequest.java
│   ├── SignupRequest.java
│   ├── TextEncryptRequest.java      # {text, algorithm}
│   ├── TextDecryptRequest.java      # {encryptedText, algorithm}
│   ├── EmailEncryptRequest.java     # {toEmail, subject, body, algorithm}
│   ├── EmailDecryptRequest.java     # {encryptedText, algorithm}
│   ├── ReportSummaryDto.java        # {totalOperations, encryptions, decryptions, emailsSent, exports}
│   ├── HistoryResponseDto.java      # Maps EncryptionHistory → JSON
│   └── FileRecordResponseDto.java   # Maps FileRecord → JSON
│
├── exception/
│   ├── AppException.java            # RuntimeException with HttpStatus
│   └── GlobalExceptionHandler.java  # @RestControllerAdvice — catches all exceptions → ApiResponse
│
├── model/
│   ├── User.java                    # users table
│   ├── EncryptionHistory.java       # encryption_history table
│   ├── FileRecord.java              # file_records table
│   ├── AlgorithmType.java           # Enum: AES_128, AES_256, TRIPLE_DES, RSA
│   ├── OperationType.java           # Enum: ENCRYPT_TEXT, DECRYPT_TEXT, ENCRYPT_FILE, ...
│   ├── OperationStatus.java         # Enum: SUCCESS, FAILED
│   └── FileStatus.java             # Enum: ENCRYPTED, DECRYPTED
│
├── repository/
│   ├── UserRepository.java
│   ├── EncryptionHistoryRepository.java
│   └── FileRecordRepository.java
│
├── security/
│   ├── JwtUtil.java                 # generateToken, isTokenValid, extractEmail
│   ├── JwtAuthFilter.java           # OncePerRequestFilter — validates Bearer token
│   └── CustomUserDetailsService.java
│
└── Service/
    ├── authentication/
    │   ├── UserService.java
    │   └── UserServiceImpl.java     # BCrypt password hash, JWT generation
    ├── encryption/
    │   ├── EncryptionServiceRouter.java   # Routes AlgorithmType → correct strategy
    │   ├── EncryptionHistoryService.java  # Saves audit rows, truncates to 100 chars
    │   └── strategy/
    │       ├── EncryptionStrategy.java    # Interface: encrypt(String), decrypt(String)
    │       ├── AesEncryptionStrategy.java
    │       ├── TripleDesEncryptionStrategy.java
    │       └── RsaEncryptionStrategy.java
    ├── email/
    │   └── EmailEncryptionService.java    # Encrypt body → send via JavaMailSender
    ├── export/
    │   └── ExcelExportService.java        # Build 3-sheet xlsx → AES-256 encrypt → .enc
    └── report/
        └── ReportService.java             # Query history + file records → DTOs
```

---

## Database Tables

### `users`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | Auto increment |
| email | VARCHAR | Unique |
| name | VARCHAR | |
| password | VARCHAR | BCrypt hashed |
| role | VARCHAR | Default: USER |
| verified | BOOLEAN | Default: false |
| verification_token | VARCHAR | |
| created_at | DATETIME | Set on insert |

### `encryption_history`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK | → users.id |
| operation_type | VARCHAR(30) | OperationType enum value |
| algorithm_type | VARCHAR(20) | AlgorithmType enum value |
| input_name | VARCHAR(100) | Truncated to 100 chars |
| output_name | VARCHAR(100) | Truncated to 100 chars |
| status | VARCHAR | SUCCESS / FAILED |
| performed_at | DATETIME | Set on insert |

### `file_records`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK | → users.id |
| original_file_name | VARCHAR | |
| encrypted_file_name | VARCHAR | |
| file_size_bytes | BIGINT | |
| status | VARCHAR | ENCRYPTED / DECRYPTED |
| created_at | DATETIME | Set on insert |

> **Note:** `operation_type` and `algorithm_type` are VARCHAR, not ENUM. MySQL ENUM columns cause truncation errors when new values are added; VARCHAR avoids this.

---

## API Reference

All endpoints except `/auth/**` require:
```
Authorization: Bearer <jwt_token>
```

### Authentication

| Method | Endpoint | Body | Response |
|---|---|---|---|
| POST | `/auth/signup` | `{name, email, password}` | `"User registered successfully"` |
| POST | `/auth/login` | `{email, password}` | `{token}` |

### Text Encryption

| Method | Endpoint | Body | Response |
|---|---|---|---|
| POST | `/api/encrypt/text` | `{text, algorithm?}` | `{result, algorithm}` |
| POST | `/api/decrypt/text` | `{encryptedText, algorithm?}` | `{result, algorithm}` |

`algorithm` is optional — defaults to `AES_256`. Accepted values: `AES_128`, `AES_256`, `TRIPLE_DES`, `RSA`.

### Email Encryption

| Method | Endpoint | Body | Response |
|---|---|---|---|
| POST | `/api/email/send-encrypted` | `{toEmail, subject?, body, algorithm?}` | success message |
| POST | `/api/email/decrypt` | `{encryptedText, algorithm?}` | `{originalMessage, algorithm}` |

### Excel Export

| Method | Endpoint | Body | Response |
|---|---|---|---|
| GET | `/api/export/encrypt` | — | Downloads `export_<email>.enc` |
| POST | `/api/export/decrypt` | multipart `file` (.enc) | Downloads `export_<email>_decrypted.xlsx` |

The exported `.xlsx` contains 3 sheets: **Profile**, **Encryption History**, **File Records** — containing only the logged-in user's own data.

### Reports

| Method | Endpoint | Response |
|---|---|---|
| GET | `/api/reports/summary` | `{totalOperations, encryptions, decryptions, emailsSent, exports}` |
| GET | `/api/reports/history` | Array of history records |
| GET | `/api/reports/files` | Array of file records |

### Response Envelope

All JSON responses follow the `ApiResponse` envelope:
```json
{
  "success": true,
  "message": "Text encrypted successfully using AES-256-CBC",
  "data": {
    "result": "...",
    "algorithm": "AES-256-CBC"
  }
}
```

Errors:
```json
{
  "success": false,
  "message": "Text cannot be empty",
  "data": null
}
```

---

## Security Design

- Passwords are hashed with **BCrypt** before storage.
- JWT tokens are signed with **HS384** and expire after **24 hours**.
- The JWT filter runs before every request, validates the token, and loads the user into `SecurityContextHolder`.
- Sessions are **stateless** — no server-side session storage.
- `/auth/**` endpoints are public. Everything else requires a valid JWT.
- `GlobalExceptionHandler` catches all unhandled exceptions and returns a consistent `ApiResponse` — no Spring whitepage errors leak to the client.

---

## Setup

### Prerequisites
- Java 21
- Maven
- MySQL running locally

### 1. Create the database
```sql
CREATE DATABASE encryption_tool;
```

### 2. Configure `application.properties`
Create `src/main/resources/application.properties` (this file is gitignored):

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/encryption_tool
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your_256bit_secret_key_here

# AES
aes.secret-key=YourAES256KeyHere_32BytesExactly!

# Gmail SMTP (port 465 SSL)
spring.mail.host=smtp.gmail.com
spring.mail.port=465
spring.mail.username=your_gmail@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.connectiontimeout=10000
spring.mail.properties.mail.smtp.timeout=10000
spring.mail.properties.mail.smtp.writetimeout=10000
```

> For Gmail, use an **App Password** (not your account password). Generate one at Google Account → Security → App Passwords.

### 3. Run
```bash
mvn spring-boot:run
```

Server starts on `http://localhost:8333`.

---

## Testing with Bruno

Bruno collection is in `../encryptiontoolTestingApi/` with folders:

| Folder | Requests |
|---|---|
| Authentication | Register, Login, error cases |
| Test encryption-descrption | Encrypt/Decrypt for all 4 algorithms + error cases |
| Email Encryption | Send encrypted email, decrypt body |
| Excel Export | Export (download .enc), Decrypt export (upload .enc, download .xlsx) |
| Reports | Summary, History, File Records |

Replace `PASTE_YOUR_JWT_TOKEN_HERE` in each request with the token returned by the Login endpoint.

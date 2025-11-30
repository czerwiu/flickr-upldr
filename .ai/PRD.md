# Product Requirements Document (PRD)
# Flickr-Upldr - Flickr Photo Upload Microservice

**Document Version:** 1.0
**Last Updated:** 2025-11-30
**Author:** czerwiu
**Status:** Draft

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Problem Statement & Business Goals](#2-problem-statement--business-goals)
3. [User Stories & Use Cases](#3-user-stories--use-cases)
4. [Functional Requirements - MVP Scope](#4-functional-requirements---mvp-scope)
5. [Non-Functional Requirements](#5-non-functional-requirements)
6. [Technical Architecture](#6-technical-architecture)
7. [API Specification](#7-api-specification)
8. [Security Requirements](#8-security-requirements)
9. [Configuration Management](#9-configuration-management)
10. [Error Handling Strategy](#10-error-handling-strategy)
11. [Logging Strategy](#11-logging-strategy)
12. [Monitoring & Observability](#12-monitoring--observability)
13. [Testing Strategy](#13-testing-strategy)
14. [Deployment Architecture](#14-deployment-architecture)
15. [Success Metrics](#15-success-metrics)
16. [Future Roadmap](#16-future-roadmap)
17. [Implementation Plan](#17-implementation-plan)
18. [Dependencies & Requirements](#18-dependencies--requirements)

---

## 1. Executive Summary

### 1.1 Product Overview

**Product Name:** Flickr-Upldr
**Product Type:** REST API Microservice
**Version:** 1.0 (MVP)

Flickr-Upldr is a secure, enterprise-grade REST API microservice that provides a simplified interface for programmatic photo uploads to Flickr. It abstracts away the complexity of direct Flickr API integration, OAuth authentication, and error handling, providing developers with a simple HTTP endpoint for reliable photo uploads.

### 1.2 Vision

To provide the most reliable and developer-friendly way to automate photo uploads to Flickr, with enterprise-grade reliability, security, and observability.

### 1.3 Target Audience

Application developers building automation workflows that require photo uploads to Flickr

### 1.4 Value Proposition

- **Simplified Integration:** Single REST endpoint vs complex Flickr API with OAuth flows
- **Built-in Reliability:** Automatic retry logic with exponential backoff for transient failures
- **Enterprise-Ready:** Comprehensive monitoring, logging, and security out of the box
- **Album Management:** Automatic album creation and photo organization
- **Full Documentation:** Interactive Swagger UI for easy integration

### 1.5 Key Differentiators

- OAuth 1.0a authentication handled transparently
- Intelligent retry mechanism (3 attempts with 1s, 2s, 4s delays)
- Automatic album creation if doesn't exist
- Spring Boot Actuator integration for health checks and metrics
- Comprehensive logging with daily rotation and 30-day retention
- Production-ready security (Basic Auth with SHA-256 password hashing)
- Interactive API documentation (Swagger UI)
- Docker-ready with multi-stage builds

---

## 2. Problem Statement & Business Goals

### 2.1 Problem Statement

#### Current Challenges

1. **Manual Upload Inefficiency**
   - Manual photo uploads to Flickr are time-consuming and error-prone
   - Difficult to scale for bulk operations
   - No automation capabilities

2. **Flickr API Integration Complexity**
   - OAuth 1.0a authentication is complex to implement
   - Direct API calls require extensive error handling
   - Album management requires multiple API calls and state tracking
   - Transient failures need manual retry logic

3. **Lack of Observability**
   - No centralized logging for upload operations
   - Difficult to track success/failure rates
   - No health monitoring capabilities

4. **Security Concerns**
   - Credential management is challenging
   - Password storage often insecure
   - No standardized authentication mechanism


---

## 3. User Stories & Use Cases

### 3.1 Primary Actors

- **Application Developer:** Integrates the API into applications

### 3.2 User Stories

#### US-1: Upload Single Photo

**As a** developer
**I want to** upload a photo via REST API
**So that** I can automate photo publishing to Flickr

**Acceptance Criteria:**
- POST /upload endpoint accepts multipart file
- Returns 200 OK with photo ID and URL on success
- Returns appropriate error codes on failure
- Photo appears in Flickr account immediately

**Priority:** MUST HAVE

---

#### US-2: Organize Photos in Albums

**As a** developer
**I want to** automatically organize photos into albums
**So that** photos are properly categorized without manual work

**Acceptance Criteria:**
- Album is created if it doesn't exist
- Photo is added to specified album
- Album ID is returned in response
- Multiple photos can go to the same album

**Priority:** MUST HAVE

---

#### US-3: Add Photo Metadata

**As a** developer
**I want to** add title, description, and tags to photos
**So that** photos are searchable and well-documented

**Acceptance Criteria:**
- Optional title, description, tags parameters
- Metadata appears in Flickr immediately
- Tags are comma-separated
- All metadata visible in Flickr UI

**Priority:** MUST HAVE

---

#### US-4: Monitor Service Health

**As a** system administrator
**I want to** monitor service health status
**So that** I can ensure reliability and uptime

**Acceptance Criteria:**
- /actuator/health endpoint returns UP status
- Health check includes disk space status
- Response time <100ms
- Accessible without authentication

**Priority:** MUST HAVE

---

#### US-5: Secure Authentication

**As a** developer
**I want to** authenticate securely with Basic Auth
**So that** only authorized users can upload photos

**Acceptance Criteria:**
- HTTP Basic Auth required on /upload
- Password validated against SHA-256 hash
- Returns 401 for invalid credentials
- Credentials never logged in plain text

**Priority:** MUST HAVE

---

#### US-6: Browse API Documentation

**As a** developer
**I want to** browse interactive API documentation
**So that** I can integrate quickly and correctly

**Acceptance Criteria:**
- Swagger UI accessible at /swagger-ui.html
- All endpoints documented with examples
- Request/response schemas shown
- "Try it out" functionality works

**Priority:** SHOULD HAVE

---

#### US-7: Access Detailed Logs

**As a** system administrator
**I want to** view detailed logs with rotation
**So that** I can troubleshoot issues without disk overflow

**Acceptance Criteria:**
- Logs written to console and file
- Daily rotation at midnight
- 30-day retention policy
- All uploads logged with metadata
- Error stack traces included

**Priority:** MUST HAVE

---

#### US-8: Automatic Retry on Failures

**As a** developer
**I want to** automatic retries on transient failures
**So that** temporary issues don't cause upload failures

**Acceptance Criteria:**
- 3 retry attempts (initial + 2 retries)
- Exponential backoff: 1s, 2s, 4s
- Only retryable errors attempted (timeouts, 5xx)
- Non-retryable errors fail immediately (401, 400)
- All attempts logged

**Priority:** MUST HAVE

---

### 3.3 Use Case Diagram

```
┌─────────────────┐
│   Developer     │
└────────┬────────┘
         │
         ├─────► Upload Photo (file, album, metadata)
         │       └─► Returns: photoId, albumId, url
         │
         ├─────► View API Documentation (Swagger)
         │
         └─────► Authenticate (Basic Auth)

┌─────────────────┐
│  Sys Admin      │
└────────┬────────┘
         │
         ├─────► Monitor Health (/actuator/health)
         │
         ├─────► View Logs (rotating files)
         │
         └─────► Check Metrics (/actuator/metrics)

┌─────────────────┐
│  Flickr API     │
└────────┬────────┘
         │
         ├─────► Receive OAuth Request
         │
         ├─────► Store Photo
         │
         └─────► Manage Albums (create/add)
```

---

## 4. Functional Requirements - MVP Scope

### 4.1 FR-1: Photo Upload Endpoint

**Priority:** MUST HAVE

#### Specification

```http
POST /upload
Content-Type: multipart/form-data
Authorization: Basic <base64(username:password)>
```

#### Request Parameters

| Parameter | Type | Required | Max Length | Description | Example |
|-----------|------|----------|------------|-------------|---------|
| file | File (multipart) | Yes | 200MB | Image file to upload | sunset.jpg |
| album | String | Yes | 255 chars | Album name (created if not exists) | "Summer Vacation 2024" |
| title | String | No | 255 chars | Photo title | "Beach Sunset" |
| description | String | No | 2000 chars | Photo description | "Beautiful sunset at Malibu" |
| tags | String | No | 500 chars | Comma-separated tags | "beach,sunset,california" |

#### Response Codes

| Code | Status | Description |
|------|--------|-------------|
| 200 | OK | Photo uploaded successfully |
| 400 | Bad Request | Missing required parameters or invalid format |
| 401 | Unauthorized | Invalid or missing authentication credentials |
| 500 | Internal Server Error | Upload failed after all retries |

#### Success Response (200 OK)

```json
{
  "photoId": "53451234567",
  "album": "Summer Vacation 2024",
  "albumId": "72157720123456789",
  "uploadedAt": "2025-11-30T17:45:23Z",
  "status": "SUCCESS",
  "url": "https://www.flickr.com/photos/username/53451234567/"
}
```

#### Error Response (400 Bad Request)

```json
{
  "timestamp": "2025-11-30T17:45:23Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Required parameter 'album' is missing",
  "path": "/upload"
}
```

---

### 4.2 FR-2: Album Management

**Priority:** MUST HAVE

#### Behavior

1. **Check Album Existence**
   - Query Flickr API for album by name
   - Search user's photosets for matching title

2. **Create Album if Not Exists**
   - If album not found, create new photoset
   - Use first uploaded photo as primary photo
   - Set album title to provided name

3. **Add Photo to Album**
   - Add photo to existing or newly created album
   - Return album ID in response
   - Handle race conditions (multiple concurrent uploads to same new album)

#### Technical Details

- Uses Flickr API: `flickr.photosets.create`
- Uses Flickr API: `flickr.photosets.addPhoto`
- Album lookup case-insensitive
- Duplicate album names handled by using first match

---

### 4.3 FR-3: File Format Support

**Priority:** MUST HAVE

#### Supported Formats

All formats supported by Flickr API:
- **Image formats:** JPG, JPEG, PNG, GIF, WEBP, BMP, TIFF, HEIC, HEIF
- **Size limit:** 200MB per file (Flickr Free: 200MB, Pro: 1GB)

#### Validation Strategy

- **No application-side validation:** Delegate all format and size validation to Flickr API
- **Rationale:**
  - Flickr API provides authoritative validation
  - Reduces code complexity
  - Automatically supports new formats as Flickr adds them
  - Avoids duplication of validation logic

#### Error Handling

- If Flickr rejects file format: Return 400 Bad Request with Flickr's error message
- If file too large: Return 400 Bad Request with size limit info

---

### 4.4 FR-4: Authentication

**Priority:** MUST HAVE

#### Mechanism

**HTTP Basic Authentication** (RFC 7617)

```http
Authorization: Basic base64(username:password)
```

#### Configuration

- **Username:** Loaded from `flickr.user.name` property
- **Password:** SHA-256 hash stored in `flickr.user.pswd` property
- **Validation:** Hash incoming password, compare with stored hash

#### Password Hashing

```bash
# Example: Generate SHA-256 hash
echo -n "mySecretPassword123" | sha256sum
# Output: 5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8
```

#### Security Considerations

- Passwords never stored in plain text
- HTTPS required in production (TLS 1.2+)
- Failed login attempts logged
- Passwords masked in all logs ("****")

---

### 4.5 FR-5: Flickr OAuth Integration

**Priority:** MUST HAVE

#### Protocol

**OAuth 1.0a** (Flickr's authentication protocol)

#### Configuration Parameters

| Parameter | Environment Variable | Description |
|-----------|---------------------|-------------|
| API Key | FLICKR_API_KEY | Flickr application API key |
| API Secret | FLICKR_API_SECRET | Flickr application API secret |
| OAuth Token | FLICKR_OAUTH_TOKEN | User OAuth access token |
| OAuth Token Secret | FLICKR_OAUTH_TOKEN_SECRET | User OAuth access token secret |
| User NSID | FLICKR_USER_NSID | Flickr user numeric ID |

#### Token Management

- **Pre-configured tokens:** OAuth tokens obtained offline and configured at deployment
- **No user authorization flow:** MVP assumes single-user with pre-authorized tokens
- **Token refresh:** Not implemented in MVP (tokens don't expire)

#### Flickr4Java Library

- Use Flickr4Java 3.0.11 for API integration
- Handles OAuth signing automatically
- Provides high-level API wrappers

---

### 4.6 FR-6: API Documentation

**Priority:** SHOULD HAVE

#### Technology

**SpringDoc OpenAPI 3.0** with Swagger UI

#### Endpoints

| Endpoint | Description |
|----------|-------------|
| `/swagger-ui.html` | Interactive API documentation UI |
| `/v3/api-docs` | OpenAPI 3.0 specification (JSON) |
| `/v3/api-docs.yaml` | OpenAPI 3.0 specification (YAML) |

#### Features

- **Interactive Testing:** "Try it out" button for each endpoint
- **Request Examples:** Sample requests with all parameters
- **Response Schemas:** Detailed response structure with examples
- **Authentication:** Shows Basic Auth requirement
- **Error Codes:** Documents all possible error responses

#### Configuration

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
```

---

### 4.7 FR-7: Health Monitoring

**Priority:** MUST HAVE

#### Endpoints

**GET /actuator/health** (Public, no authentication required)

**Response Example:**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 250000000000,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

**GET /actuator/metrics** (Protected, requires authentication)

Lists all available metrics:
```json
{
  "names": [
    "upload.total",
    "upload.success",
    "upload.failure",
    "upload.duration",
    "http.server.requests",
    "jvm.memory.used",
    "jvm.threads.live"
  ]
}
```

**GET /actuator/metrics/{metricName}** (Protected)

Example: `/actuator/metrics/upload.total`
```json
{
  "name": "upload.total",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1523.0
    }
  ]
}
```

**GET /actuator/info** (Protected)

Application information:
```json
{
  "app": {
    "name": "flickr-upldr",
    "version": "1.0-SNAPSHOT",
    "java": {
      "version": "25"
    },
    "spring-boot": {
      "version": "4.0.0"
    }
  }
}
```

---

## 5. Non-Functional Requirements

### 5.1 NFR-1: Performance

**Priority:** MUST HAVE

| Metric | Target | Measurement Method |
|--------|--------|-------------------|
| Response Time (P95) | <5 seconds | Micrometer timer metrics |
| Response Time (P99) | <10 seconds | Micrometer timer metrics |
| Throughput | 10 concurrent uploads | Load testing |
| Flickr API Timeout | 30 seconds | HTTP client configuration |
| Application Startup | <30 seconds | Container logs |

#### Performance Considerations

- Async operations where possible (future enhancement)
- Connection pooling for Flickr API calls
- No blocking database operations (stateless)

---

### 5.2 NFR-2: Reliability

**Priority:** MUST HAVE

| Metric | Target | Implementation |
|--------|--------|----------------|
| Uptime | 99% | Robust error handling, health checks |
| Error Rate (Non-Flickr) | <1% | Comprehensive exception handling |
| Retry Success Rate | >80% | Exponential backoff retry logic |
| Data Loss | 0% | Synchronous uploads, no queuing in MVP |

#### Retry Strategy

**Retryable Errors:**
- Network timeouts (`SocketTimeoutException`)
- Flickr API 5xx errors (`FlickrException` with 500-599 status)
- Connection failures (`ConnectException`)
- Temporary unavailability

**Non-Retryable Errors:**
- Authentication failures (401)
- Invalid parameters (400)
- Flickr API 4xx client errors (403, 404, 429)
- File format/size violations

**Retry Configuration:**
```java
@Retryable(
  value = {FlickrException.class, SocketTimeoutException.class},
  maxAttempts = 3,
  backoff = @Backoff(delay = 1000, multiplier = 2)
)
// Delays: 1000ms (1s), 2000ms (2s), 4000ms (4s)
```

---

### 5.3 NFR-3: Security

**Priority:** MUST HAVE

#### Authentication & Authorization

- **Mechanism:** HTTP Basic Authentication
- **Transport Security:** HTTPS required in production (TLS 1.2+)
- **Password Storage:** SHA-256 hashed passwords only
- **Session Management:** Stateless (no sessions)

#### Credential Management

- **Storage:** Environment variables ONLY
- **Never in code:** No hardcoded credentials
- **Never in version control:** Credentials in .gitignored files only for local dev

#### Secure Headers

```http
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

#### Data Protection

- **Password Masking:** All passwords replaced with "****" in logs
- **Token Masking:** OAuth tokens show only first 4 characters in logs
- **NSID Masking:** User NSID partially masked in logs
- **Error Messages:** No credential leakage in error responses

---

### 5.4 NFR-4: Logging

**Priority:** MUST HAVE

#### Destinations

- **Console:** STDOUT for container environments
- **File:** Rotating log files for persistence

#### Rotation Policy

- **Frequency:** Daily at midnight
- **Retention:** 30 days
- **Max Total Size:** 10GB
- **Location:** `logs/flickr-upldr.log`

#### Log Levels

| Level | Usage |
|-------|-------|
| DEBUG | Request/response payloads (sanitized), Flickr API calls, retry attempts, config loading |
| INFO | Upload start/completion, album creation, authentication success, startup/shutdown |
| WARN | Retry attempts, slow responses (>3s), resource warnings (disk <10%) |
| ERROR | Upload failures, auth failures, Flickr API errors, unexpected exceptions with stack traces |

#### Structured Logging

**Format:**
```
2025-11-30 17:45:23 [http-nio-8080-exec-1] INFO  UploadService - Photo uploaded: photoId=53451234567, album=Summer Vacation, duration=1234ms, size=2457600, user=john.doe
```

**Example Error Log:**
```
2025-11-30 17:45:26 [http-nio-8080-exec-1] ERROR FlickrService - Upload failed after 3 attempts
pl.czerwiu.flickr.upldr.exception.RetryExhaustedException: Failed to upload photo after 3 retries
    at pl.czerwiu.flickr.upldr.service.FlickrService.recoverFromUploadFailure(FlickrService.java:78)
    ...
```

#### Log Content Requirements

**Must Log:**
- All upload operations (success and failure)
- Upload metadata: file size, album, user, duration
- Authentication attempts (success and failure)
- Retry attempts with reason and delay
- All errors with stack traces
- Application lifecycle events (startup, shutdown)

**Must NOT Log:**
- Plain text passwords
- Full OAuth tokens
- Raw user NSID

---

### 5.5 NFR-5: Monitoring & Observability

**Priority:** MUST HAVE

#### Health Indicators

- **Ping:** Basic liveness check
- **Disk Space:** Monitor available space for logs
- **Custom (Future):** Flickr API connectivity check

#### Custom Metrics (Micrometer)

| Metric | Type | Description |
|--------|------|-------------|
| upload.total | Counter | Total upload attempts |
| upload.success | Counter | Successful uploads |
| upload.failure | Counter | Failed uploads |
| upload.duration | Timer | Upload duration distribution |
| upload.file.size | Summary | File size distribution |

#### Built-in Metrics

| Metric | Type | Description |
|--------|------|-------------|
| http.server.requests | Timer | HTTP request metrics (status, method, URI) |
| jvm.memory.used | Gauge | JVM memory usage |
| jvm.memory.max | Gauge | JVM max memory |
| jvm.threads.live | Gauge | Live JVM threads |
| jvm.gc.pause | Timer | Garbage collection pause time |

#### Implementation Example

```java
@Service
public class UploadService {
    private final MeterRegistry meterRegistry;

    public UploadResponse upload(MultipartFile file, UploadRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            String photoId = flickrService.upload(file, request);
            meterRegistry.counter("upload.success").increment();
            meterRegistry.summary("upload.file.size").record(file.getSize());
            return new UploadResponse(photoId, ...);
        } catch (Exception e) {
            meterRegistry.counter("upload.failure").increment();
            throw e;
        } finally {
            sample.stop(meterRegistry.timer("upload.duration"));
            meterRegistry.counter("upload.total").increment();
        }
    }
}
```

---

### 5.6 NFR-6: Maintainability

**Priority:** SHOULD HAVE

#### Code Quality

- **Test Coverage:** 80% minimum (enforced by JaCoCo)
- **Code Style:** Spring Boot best practices, consistent formatting
- **Documentation:** JavaDoc for all public APIs
- **Complexity:** Maximum cyclomatic complexity: 10 per method

#### Architecture

- **Layered Design:** Clear separation of concerns (Controller → Service → Client)
- **Dependency Injection:** Spring dependency injection throughout
- **SOLID Principles:** Single responsibility, dependency inversion
- **No Circular Dependencies:** Enforced by Spring

#### Configuration

- **Externalized:** All configuration via environment variables
- **Profiles:** Support for dev, test, prod profiles
- **Defaults:** Sensible defaults for non-critical settings

---

### 5.7 NFR-7: Scalability

**Priority:** NICE TO HAVE (Future-ready design)

#### Horizontal Scaling

- **Stateless Design:** No session state, no local caching
- **No Shared Resources:** No local database or file dependencies
- **Environment Variables:** Configuration via env vars, not local files
- **Load Balancer Ready:** Multiple instances can run behind load balancer

#### Vertical Scaling

- **Memory Efficient:** Minimal memory footprint (~500MB JVM)
- **CPU Efficient:** Async operations where possible (future)
- **Configurable Limits:** Multipart file size limits configurable

---

## 6. Technical Architecture

### 6.1 Layered Architecture

```
┌─────────────────────────────────────────────────┐
│          PRESENTATION LAYER                     │
│  ┌──────────────────────────────────────────┐   │
│  │ UploadController                         │   │
│  │ - POST /upload                           │   │
│  │ - Parameter validation                   │   │
│  │ - Response mapping                       │   │
│  └──────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────┐   │
│  │ GlobalExceptionHandler                   │   │
│  │ - Centralized error handling             │   │
│  │ - ErrorResponse generation               │   │
│  └──────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────┐   │
│  │ OpenAPI Configuration                    │   │
│  │ - Swagger UI setup                       │   │
│  │ - API documentation                      │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         BUSINESS LOGIC LAYER                    │
│  ┌──────────────────────────────────────────┐   │
│  │ UploadService                            │   │
│  │ - Orchestrates upload workflow           │   │
│  │ - Retry logic (@Retryable)               │   │
│  │ - Metrics tracking                       │   │
│  └──────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────┐   │
│  │ AlbumService                             │   │
│  │ - Check album existence                  │   │
│  │ - Create album if needed                 │   │
│  │ - Add photo to album                     │   │
│  └──────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────┐   │
│  │ FlickrService                            │   │
│  │ - Flickr4Java wrapper                    │   │
│  │ - Upload photo with metadata             │   │
│  │ - Error translation                      │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│          INTEGRATION LAYER                      │
│  ┌──────────────────────────────────────────┐   │
│  │ FlickrClient (Flickr4Java)               │   │
│  │ - OAuth 1.0a signing                     │   │
│  │ - API method calls                       │   │
│  │ - Response parsing                       │   │
│  └──────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────┐   │
│  │ FlickrConfiguration                      │   │
│  │ - Flickr client initialization           │   │
│  │ - OAuth credentials setup                │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│          EXTERNAL SERVICES                      │
│  ┌──────────────────────────────────────────┐   │
│  │ Flickr API (OAuth 1.0a)                  │   │
│  │ - flickr.photos.upload                   │   │
│  │ - flickr.photosets.create                │   │
│  │ - flickr.photosets.addPhoto              │   │
│  │ - flickr.photosets.getList               │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│          CROSS-CUTTING CONCERNS                 │
│  - Security (SecurityConfig, Basic Auth)        │
│  - Logging (Logback, daily rotation)            │
│  - Monitoring (Actuator, Micrometer)            │
│  - Error Handling (GlobalExceptionHandler)      │
│  - Retry Logic (Spring Retry, exp. backoff)     │
└─────────────────────────────────────────────────┘
```

### 6.2 Component Breakdown

#### Controllers

**UploadController**
- Handles POST /upload requests
- Validates multipart file and parameters
- Delegates to UploadService
- Maps service responses to HTTP responses
- Annotated with @RestController, @RequestMapping

#### Services

**UploadService**
- Orchestrates complete upload workflow
- Calls AlbumService for album operations
- Calls FlickrService for photo upload
- Implements retry logic via @Retryable
- Tracks metrics (success, failure, duration, file size)
- Comprehensive logging

**AlbumService**
- Checks if album exists by name
- Creates new album if needed
- Adds photo to album (existing or new)
- Handles album operation errors

**FlickrService**
- Thin wrapper around Flickr4Java client
- Uploads photo with metadata (title, description, tags)
- Translates Flickr exceptions to domain exceptions
- Retry-eligible (annotated with @Retryable)

#### Configuration

**SecurityConfig**
- Configures Spring Security
- Sets up HTTP Basic Authentication
- Custom UserDetailsService for SHA-256 password validation
- Defines security rules (public /actuator/health, protected others)
- Disables CSRF for API usage

**FlickrConfig**
- Initializes Flickr4Java client
- Sets up OAuth credentials from properties
- Creates FlickrClient bean
- Configures HTTP connection settings (timeout, pool size)

**FlickrProperties**
- @ConfigurationProperties for flickr.* properties
- Type-safe configuration binding
- Validation annotations

**OpenApiConfig**
- SpringDoc OpenAPI configuration
- Swagger UI customization
- Security scheme definition (Basic Auth)
- API metadata (title, version, description)

**RetryConfig**
- Enables Spring Retry (@EnableRetry)
- Configures retry template (optional)

#### DTOs

**UploadRequest**
- Binds multipart form parameters
- Validation annotations (@NotNull, @Size)
- Fields: file, album, title, description, tags

**UploadResponse**
- Success response structure
- Fields: photoId, album, albumId, uploadedAt, status, url

**ErrorResponse**
- Standardized error format
- Fields: timestamp, status, error, message, details, path

#### Exceptions

**FlickrUploaderException** (base exception)
- Runtime exception for all application errors
- Includes error details field

**FlickrUploadException** extends FlickrUploaderException
- Upload operation failures

**AlbumOperationException** extends FlickrUploaderException
- Album creation/management failures

**AuthenticationException** extends FlickrUploaderException
- Authentication failures (401)

**ConfigurationException** extends FlickrUploaderException
- Missing or invalid configuration

**RetryExhaustedException** extends FlickrUploaderException
- All retry attempts exhausted

**GlobalExceptionHandler**
- @ControllerAdvice for centralized exception handling
- Maps exceptions to HTTP responses
- Logs all exceptions
- Returns ErrorResponse DTOs

### 6.3 Technology Stack

#### Core Framework

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 4.0.0 | Application framework |
| Spring Web MVC | (included) | REST API |
| Spring Security | (included) | Authentication |
| Java | 25 | Programming language |

#### Third-Party Libraries

| Library | Version | Purpose |
|---------|--------|---------|
| Flickr4Java | 3.0.11 | Flickr API integration |
| SpringDoc OpenAPI | 2.3.0  | API documentation (Swagger) |
| Spring Retry | (latest) | Retry logic |
| Logback | (bundled) | Logging framework |
| Micrometer | (bundled) | Metrics |

#### Testing

| Library | Version | Purpose |
|---------|---------|---------|
| JUnit 5 | (bundled) | Unit testing framework |
| Mockito | (bundled) | Mocking framework |
| Spring Boot Test | (bundled) | Integration testing |
| Spring Security Test | (bundled) | Security testing |
| JaCoCo | 0.8.11 | Code coverage |

#### Build & Runtime

| Tool | Version | Purpose |
|------|---------|---------|
| Maven | 3.9+ | Build tool |
| Docker | 20.10+ | Containerization |
| Docker Compose | 2.0+ | Local development |

### 6.4 Data Flow - Successful Upload

```
1. Client → POST /upload (multipart/form-data)
           Headers: Authorization: Basic base64(user:pass)

2. Spring Security → AuthenticationManager
                  → UserDetailsService
                  → SHA-256 hash validation
                  → Authentication object created

3. UploadController → Receives authenticated request
                   → Validates @RequestParam, @Valid
                   → Delegates to UploadService

4. UploadService → Starts Micrometer timer
              → Logs upload start (INFO level)
              → Calls AlbumService.ensureAlbum(albumName)

5. AlbumService → Calls FlickrService.getAlbumByName(albumName)
             → If not found: FlickrService.createAlbum(albumName)
             → Returns albumId

6. UploadService → Calls FlickrService.uploadPhoto(file, metadata)
              → [Retry logic active via @Retryable]

7. FlickrService → Converts MultipartFile to InputStream
              → Calls flickrClient.getUploader().upload(...)
              → Flickr API: OAuth signing, HTTP POST
              → Flickr API returns photoId

8. UploadService → Calls AlbumService.addPhotoToAlbum(photoId, albumId)

9. AlbumService → Calls FlickrService.addPhotoToAlbum(photoId, albumId)
             → Flickr API: flickr.photosets.addPhoto

10. UploadService → Stops timer, records metrics
               → Increments upload.success counter
               → Records upload.file.size
               → Logs success (INFO level)
               → Returns UploadResponse

11. UploadController → Maps UploadResponse to HTTP 200 OK
                    → Returns JSON response to client
```

### 6.5 Data Flow - Failed Upload with Retry

```
1-6. [Same as successful flow]

7. FlickrService → uploadPhoto() called
                → Flickr API call times out (SocketTimeoutException)

8. Spring Retry → Catches exception (retryable)
              → Logs WARN: "Upload attempt 1 failed: Connection timeout"
              → Waits 1000ms (1 second)
              → Retries uploadPhoto()

9. FlickrService → uploadPhoto() retry #1
                → Flickr API call times out again

10. Spring Retry → Catches exception
               → Logs WARN: "Upload attempt 2 failed: Connection timeout"
               → Waits 2000ms (2 seconds)
               → Retries uploadPhoto()

11. FlickrService → uploadPhoto() retry #2
                 → Flickr API call times out again

12. Spring Retry → Catches exception
               → No more retries left (maxAttempts = 3)
               → Logs WARN: "Upload attempt 3 failed: Connection timeout"
               → Calls @Recover method

13. FlickrService → recoverFromUploadFailure() called
                 → Logs ERROR with stack trace
                 → Throws RetryExhaustedException

14. UploadService → Catches RetryExhaustedException
                 → Increments upload.failure counter
                 → Rethrows exception

15. GlobalExceptionHandler → Catches RetryExhaustedException
                          → Logs ERROR
                          → Creates ErrorResponse
                          → Returns HTTP 500 Internal Server Error

16. Client → Receives 500 error with details
```

---

## 7. API Specification

### 7.1 POST /upload - Upload Photo to Flickr

#### Endpoint

```
POST /upload
```

#### Authentication

**HTTP Basic Auth** (Required)

```http
Authorization: Basic base64(username:password)
```

Example:
```http
Authorization: Basic am9obi5kb2U6bXlTZWNyZXRQYXNzd29yZDEyMw==
```

#### Request

**Content-Type:** `multipart/form-data`

**Parameters:**

| Parameter | Type | Required | Constraints | Description |
|-----------|------|----------|-------------|-------------|
| file | File | ✅ Yes | Max: 200MB<br>Format: Any Flickr-supported image format | The image file to upload |
| album | String | ✅ Yes | Max: 255 chars<br>Non-empty | Album name (created if doesn't exist) |
| title | String | ❌ No | Max: 255 chars | Photo title (optional) |
| description | String | ❌ No | Max: 2000 chars | Photo description (optional) |
| tags | String | ❌ No | Max: 500 chars | Comma-separated tags (optional) |

**Example Request (cURL):**

```bash
curl -X POST http://localhost:8080/upload \
  -u john.doe:mySecretPassword123 \
  -F "file=@/path/to/sunset.jpg" \
  -F "album=Summer Vacation 2024" \
  -F "title=Beach Sunset" \
  -F "description=Beautiful sunset at Malibu Beach, California" \
  -F "tags=beach,sunset,california,malibu,2024"
```

**Example Request (Raw HTTP):**

```http
POST /upload HTTP/1.1
Host: localhost:8080
Authorization: Basic am9obi5kb2U6bXlTZWNyZXRQYXNzd29yZDEyMw==
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary

------WebKitFormBoundary
Content-Disposition: form-data; name="file"; filename="sunset.jpg"
Content-Type: image/jpeg

[Binary image data]
------WebKitFormBoundary
Content-Disposition: form-data; name="album"

Summer Vacation 2024
------WebKitFormBoundary
Content-Disposition: form-data; name="title"

Beach Sunset
------WebKitFormBoundary
Content-Disposition: form-data; name="description"

Beautiful sunset at Malibu Beach, California
------WebKitFormBoundary
Content-Disposition: form-data; name="tags"

beach,sunset,california,malibu,2024
------WebKitFormBoundary--
```

#### Responses

##### 200 OK - Success

Photo uploaded successfully.

**Content-Type:** `application/json`

**Schema:**

```json
{
  "photoId": "string",
  "album": "string",
  "albumId": "string",
  "uploadedAt": "string (ISO 8601 datetime)",
  "status": "string",
  "url": "string (Flickr photo URL)"
}
```

**Example:**

```json
{
  "photoId": "53451234567",
  "album": "Summer Vacation 2024",
  "albumId": "72157720123456789",
  "uploadedAt": "2025-11-30T17:45:23Z",
  "status": "SUCCESS",
  "url": "https://www.flickr.com/photos/johndoe/53451234567/"
}
```

##### 400 Bad Request - Validation Error

Missing required parameters or invalid format.

**Content-Type:** `application/json`

**Schema:**

```json
{
  "timestamp": "string (ISO 8601 datetime)",
  "status": integer,
  "error": "string",
  "message": "string",
  "path": "string"
}
```

**Example (Missing album):**

```json
{
  "timestamp": "2025-11-30T17:45:23Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Required parameter 'album' is missing",
  "path": "/upload"
}
```

**Example (Invalid file format - from Flickr):**

```json
{
  "timestamp": "2025-11-30T17:45:23Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Unsupported file format",
  "details": "Flickr API rejected file: invalid image format",
  "path": "/upload"
}
```

##### 401 Unauthorized - Authentication Failed

Invalid or missing credentials.

**Content-Type:** `application/json`

**Example:**

```json
{
  "timestamp": "2025-11-30T17:45:23Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/upload"
}
```

##### 500 Internal Server Error - Upload Failed

Upload failed after all retry attempts.

**Content-Type:** `application/json`

**Example:**

```json
{
  "timestamp": "2025-11-30T17:45:23Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Failed to upload photo after 3 attempts",
  "details": "Flickr API connection timeout",
  "path": "/upload"
}
```

---

### 7.2 GET /actuator/health - Service Health Check

#### Endpoint

```
GET /actuator/health
```

#### Authentication

**Public** (No authentication required)

#### Response

##### 200 OK - Service Healthy

**Content-Type:** `application/json`

**Example:**

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 250000000000,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

##### 503 Service Unavailable - Service Unhealthy

**Content-Type:** `application/json`

**Example:**

```json
{
  "status": "DOWN",
  "components": {
    "diskSpace": {
      "status": "DOWN",
      "details": {
        "total": 500000000000,
        "free": 5000000,
        "threshold": 10485760,
        "exists": true,
        "error": "Insufficient disk space"
      }
    }
  }
}
```

---

### 7.3 GET /actuator/metrics - Available Metrics

#### Endpoint

```
GET /actuator/metrics
```

#### Authentication

**HTTP Basic Auth** (Required)

#### Response

##### 200 OK

**Content-Type:** `application/json`

**Example:**

```json
{
  "names": [
    "upload.total",
    "upload.success",
    "upload.failure",
    "upload.duration",
    "upload.file.size",
    "http.server.requests",
    "jvm.memory.used",
    "jvm.memory.max",
    "jvm.threads.live",
    "jvm.gc.pause"
  ]
}
```

---

### 7.4 GET /actuator/metrics/{metricName} - Specific Metric

#### Endpoint

```
GET /actuator/metrics/{metricName}
```

#### Authentication

**HTTP Basic Auth** (Required)

#### Path Parameters

| Parameter | Description | Example |
|-----------|-------------|---------|
| metricName | Metric name from /actuator/metrics | upload.total |

#### Response

##### 200 OK

**Content-Type:** `application/json`

**Example (Counter):**

```json
{
  "name": "upload.total",
  "description": "Total number of upload attempts",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1523.0
    }
  ]
}
```

**Example (Timer):**

```json
{
  "name": "upload.duration",
  "description": "Upload duration",
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1523.0
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 4821.5
    },
    {
      "statistic": "MAX",
      "value": 9.2
    }
  ]
}
```

---

### 7.5 GET /swagger-ui.html - API Documentation

#### Endpoint

```
GET /swagger-ui.html
```

#### Authentication

**Public** (No authentication required)

#### Response

Interactive Swagger UI with:
- All endpoints documented
- Request/response schemas
- "Try it out" functionality
- Authentication scheme (Basic Auth)
- Example requests and responses

---

## 8. Security Requirements

### 8.1 Authentication Mechanism

#### HTTP Basic Authentication (RFC 7617)

**Format:**
```http
Authorization: Basic base64(username:password)
```

**Example:**
```bash
# Username: john.doe
# Password: mySecretPassword123
# Combined: john.doe:mySecretPassword123
# Base64 encoded: am9obi5kb2U6bXlTZWNyZXRQYXNzd29yZDEyMw==

curl -u john.doe:mySecretPassword123 http://localhost:8080/upload
# Or explicitly:
curl -H "Authorization: Basic am9obi5kb2U6bXlTZWNyZXRQYXNzd29yZDEyMw==" http://localhost:8080/upload
```

### 8.2 Password Storage & Validation

#### SHA-256 Hash Algorithm

**Password Storage:**
- Plain text password NEVER stored anywhere
- Only SHA-256 hash stored in configuration
- Hash format: 64-character hexadecimal string

**Hash Generation (Offline):**

```bash
# Linux/Mac
echo -n "mySecretPassword123" | sha256sum
# Output: 5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8

# Or using OpenSSL
echo -n "mySecretPassword123" | openssl dgst -sha256
```

**Validation Process (Runtime):**

```java
public boolean validatePassword(String providedPassword, String storedHash) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(providedPassword.getBytes(StandardCharsets.UTF_8));
    String computedHash = bytesToHex(hash);
    return computedHash.equals(storedHash);
}
```

### 8.3 Credential Management

#### Configuration Hierarchy

**Priority Order (highest to lowest):**

1. **Environment Variables** (PRODUCTION)
   ```bash
   export FLICKR_API_KEY="a1b2c3d4e5f6g7h8i9j0..."
   export FLICKR_API_SECRET="9876543210abcdef..."
   export FLICKR_OAUTH_TOKEN="12345-67890abcdef..."
   export FLICKR_OAUTH_TOKEN_SECRET="abcdef123456..."
   export FLICKR_USER_NSID="12345678@N01"
   export FLICKR_USER_NAME="john.doe"
   export FLICKR_USER_PSWD_HASH="5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"
   ```

2. **application-local.yml** (LOCAL DEVELOPMENT ONLY, .gitignored)
   ```yaml
   flickr:
     api:
       key: "local-dev-api-key"
       secret: "local-dev-api-secret"
     oauth:
       token: "local-dev-oauth-token"
       token-secret: "local-dev-oauth-token-secret"
     user:
       nsid: "12345678@N01"
       name: "local.dev"
       pswd: "localdevhash123"
   ```

3. **NEVER in application.yml** (version controlled)
   - Only placeholder syntax: `${FLICKR_API_KEY}`
   - Never actual values

#### .gitignore Configuration

```gitignore
# Local development credentials
application-local.yml
application-local.yaml

# Environment files
.env
.env.local

# Secrets
secrets/
*.key
*.secret
```

### 8.4 Transport Security

#### HTTPS/TLS Requirements

**Development:**
- HTTP acceptable for localhost testing

**Production:**
- HTTPS REQUIRED (TLS 1.2 minimum, TLS 1.3 recommended)
- Valid SSL certificate
- HSTS header enabled

**Configuration (application-prod.yml):**

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: flickr-upldr
```

### 8.5 Security Headers

**Implemented Headers:**

```http
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
```

**Spring Security Configuration:**

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .headers(headers -> headers
            .contentTypeOptions()
            .xssProtection()
            .frameOptions().deny()
            .httpStrictTransportSecurity()
                .maxAgeInSeconds(31536000)
                .includeSubDomains(true)
        );
    return http.build();
}
```

### 8.6 Data Protection

#### Sensitive Data Masking

**Passwords:**
```java
String password = "mySecretPassword123";
logger.info("Authentication attempt for user: {} with password: ****", username);
// Never: logger.info("Password: {}", password);
```

**OAuth Tokens:**
```java
String token = "12345-67890abcdef...";
String masked = token.substring(0, 4) + "****";
logger.info("Using OAuth token: {}", masked);  // Logs: "1234****"
```

**User NSID:**
```java
String nsid = "12345678@N01";
String masked = nsid.substring(0, 4) + "****" + nsid.substring(nsid.length() - 4);
logger.info("User NSID: {}", masked);  // Logs: "1234****@N01"
```

#### Error Response Sanitization

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    // NEVER include full stack trace in response
    // NEVER include credentials or tokens
    ErrorResponse error = new ErrorResponse(
        LocalDateTime.now(),
        500,
        "Internal Server Error",
        "An unexpected error occurred",  // Generic message
        null,  // No details leaked
        request.getDescription(false)
    );
    return ResponseEntity.status(500).body(error);
}
```

### 8.7 Security Best Practices

#### Input Validation

- Use `@Valid` and `@Validated` annotations
- Spring Boot auto-escaping for XSS prevention
- Multipart file size limits enforced
- Parameter length limits enforced

#### CSRF Protection

```java
// CSRF disabled for stateless API
http.csrf(csrf -> csrf.disable());
```

**Rationale:** Stateless API with token-based auth (Basic Auth), no session cookies, no browser-based forms.

---

## 9. Configuration Management

### 9.1 application.yml (Complete Configuration)

**Location:** `src/main/resources/application.yml`

```yaml
# Application Configuration
spring:
  application:
    name: flickr-upldr

  # Multipart File Upload Configuration
  servlet:
    multipart:
      enabled: true
      max-file-size: 200MB
      max-request-size: 210MB
      file-size-threshold: 2MB
      location: /tmp

# Server Configuration
server:
  port: 8080
  error:
    include-message: always
    include-binding-errors: on_param
    include-stacktrace: never  # Security: don't leak stack traces
    include-exception: false

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized  # Show details only for authenticated users
      probes:
        enabled: true
  metrics:
    export:
      simple:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true

# Logging Configuration
logging:
  file:
    name: logs/flickr-upldr.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  level:
    root: INFO
    pl.czerwiu.flickr.upldr: DEBUG
    org.springframework.web: INFO
    org.springframework.security: INFO

# Flickr API Configuration (use environment variables)
flickr:
  api:
    key: ${FLICKR_API_KEY}
    secret: ${FLICKR_API_SECRET}
  oauth:
    token: ${FLICKR_OAUTH_TOKEN}
    token-secret: ${FLICKR_OAUTH_TOKEN_SECRET}
  user:
    nsid: ${FLICKR_USER_NSID}
    name: ${FLICKR_USER_NAME}
    pswd: ${FLICKR_USER_PSWD_HASH}  # SHA-256 hash

# SpringDoc OpenAPI Configuration
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
    tagsSorter: alpha
```

### 9.2 logback-spring.xml (Complete Configuration)

**Location:** `src/main/resources/logback-spring.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- File Appender with Daily Rotation -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/flickr-upldr.log</file>

        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- Daily rollover at midnight -->
            <fileNamePattern>logs/flickr-upldr.%d{yyyy-MM-dd}.log</fileNamePattern>

            <!-- Keep 30 days of history -->
            <maxHistory>30</maxHistory>

            <!-- Total size cap (10GB) -->
            <totalSizeCap>10GB</totalSizeCap>
        </rollingPolicy>

        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- Root Logger (all packages) -->
    <root level="INFO">
        <appender-ref ref="FILE" />
        <appender-ref ref="CONSOLE" />
    </root>

    <!-- Application Logger (pl.czerwiu.flickr.upldr) -->
    <logger name="pl.czerwiu.flickr.upldr" level="DEBUG" additivity="false">
        <appender-ref ref="FILE" />
        <appender-ref ref="CONSOLE" />
    </logger>

    <!-- Spring Framework Loggers -->
    <logger name="org.springframework.web" level="INFO" additivity="false">
        <appender-ref ref="FILE" />
        <appender-ref ref="CONSOLE" />
    </logger>

    <logger name="org.springframework.security" level="INFO" additivity="false">
        <appender-ref ref="FILE" />
        <appender-ref ref="CONSOLE" />
    </logger>

    <!-- Flickr4Java Logger -->
    <logger name="com.flickr4java" level="INFO" additivity="false">
        <appender-ref ref="FILE" />
        <appender-ref ref="CONSOLE" />
    </logger>

</configuration>
```

### 9.3 Environment Variables (Production)

**Required Environment Variables:**

```bash
# Flickr API Credentials
export FLICKR_API_KEY="your-flickr-api-key-here"
export FLICKR_API_SECRET="your-flickr-api-secret-here"

# Flickr OAuth Tokens (obtained offline)
export FLICKR_OAUTH_TOKEN="your-oauth-token-here"
export FLICKR_OAUTH_TOKEN_SECRET="your-oauth-token-secret-here"

# Flickr User Information
export FLICKR_USER_NSID="12345678@N01"

# Application Authentication
export FLICKR_USER_NAME="your-username"
export FLICKR_USER_PSWD_HASH="sha256-hash-of-your-password"
```

**Setting on Different Platforms:**

**Linux/Mac (.bashrc or .bash_profile):**
```bash
export FLICKR_API_KEY="..."
export FLICKR_API_SECRET="..."
# ... etc
```

**Docker (.env file):**
```env
FLICKR_API_KEY=...
FLICKR_API_SECRET=...
FLICKR_OAUTH_TOKEN=...
FLICKR_OAUTH_TOKEN_SECRET=...
FLICKR_USER_NSID=...
FLICKR_USER_NAME=...
FLICKR_USER_PSWD_HASH=...
```

**Kubernetes (Secret):**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: flickr-secrets
type: Opaque
data:
  flickr-api-key: <base64-encoded>
  flickr-api-secret: <base64-encoded>
  # ... etc
```

---

## 10. Error Handling Strategy

### 10.1 Exception Hierarchy

```
java.lang.Throwable
└── java.lang.Exception
    └── java.lang.RuntimeException
        └── pl.czerwiu.flickr.upldr.exception.FlickrUploaderException (BASE)
            ├── FlickrUploadException
            │   (Photo upload failures)
            ├── AlbumOperationException
            │   (Album creation/management failures)
            ├── AuthenticationException
            │   (Authentication failures - 401)
            ├── ConfigurationException
            │   (Missing or invalid configuration)
            └── RetryExhaustedException
                (All retry attempts failed)
```

### 10.2 Exception Definitions

**FlickrUploaderException (Base)**

```java
public class FlickrUploaderException extends RuntimeException {
    private final String details;

    public FlickrUploaderException(String message) {
        super(message);
        this.details = null;
    }

    public FlickrUploaderException(String message, String details) {
        super(message);
        this.details = details;
    }

    public FlickrUploaderException(String message, Throwable cause) {
        super(message, cause);
        this.details = null;
    }

    public String getDetails() {
        return details;
    }
}
```

**Specific Exceptions:**

```java
public class FlickrUploadException extends FlickrUploaderException {
    public FlickrUploadException(String message, String details) {
        super(message, details);
    }
}

public class AlbumOperationException extends FlickrUploaderException {
    public AlbumOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class RetryExhaustedException extends FlickrUploaderException {
    public RetryExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 10.3 Spring Retry Configuration

**Enable Retry:**

```java
@Configuration
@EnableRetry
public class RetryConfig {
    // Enables @Retryable and @Recover annotations
}
```

**Retryable Service Method:**

```java
@Service
public class FlickrService {

    private static final Logger logger = LoggerFactory.getLogger(FlickrService.class);

    @Retryable(
        value = {FlickrException.class, SocketTimeoutException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
        // Delays: 1000ms, 2000ms, 4000ms
    )
    public String uploadPhoto(MultipartFile file, UploadMetadata metadata)
            throws FlickrUploadException {

        logger.debug("Uploading photo: {}", file.getOriginalFilename());

        try {
            InputStream inputStream = file.getInputStream();
            UploadMetaData flickrMetadata = new UploadMetaData();
            flickrMetadata.setTitle(metadata.getTitle());
            flickrMetadata.setDescription(metadata.getDescription());
            flickrMetadata.setTags(Arrays.asList(metadata.getTags().split(",")));

            String photoId = flickrClient.getUploader()
                .upload(inputStream, flickrMetadata);

            logger.info("Photo uploaded successfully: photoId={}", photoId);
            return photoId;

        } catch (FlickrException e) {
            logger.warn("Flickr API error during upload: {}", e.getMessage());
            throw e;  // Will trigger retry
        } catch (IOException e) {
            logger.error("IO error during upload: {}", e.getMessage());
            throw new FlickrUploadException("Failed to read file", e.getMessage());
        }
    }

    @Recover
    public String recoverFromUploadFailure(
            FlickrException e,
            MultipartFile file,
            UploadMetadata metadata) {

        logger.error("Upload failed after 3 attempts: {}", e.getMessage(), e);
        throw new RetryExhaustedException(
            "Failed to upload photo after 3 retries", e);
    }
}
```

### 10.4 Global Exception Handler

**@ControllerAdvice Implementation:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle photo upload failures
     */
    @ExceptionHandler(FlickrUploadException.class)
    public ResponseEntity<ErrorResponse> handleUploadException(
            FlickrUploadException ex, WebRequest request) {

        logger.error("Upload failed: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(500)
            .error("Internal Server Error")
            .message("Failed to upload photo: " + ex.getMessage())
            .details(ex.getDetails())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(500).body(error);
    }

    /**
     * Handle retry exhausted exceptions
     */
    @ExceptionHandler(RetryExhaustedException.class)
    public ResponseEntity<ErrorResponse> handleRetryExhausted(
            RetryExhaustedException ex, WebRequest request) {

        logger.error("All retries exhausted: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(500)
            .error("Internal Server Error")
            .message(ex.getMessage())
            .details("Please try again later or contact support")
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(500).body(error);
    }

    /**
     * Handle validation errors (missing parameters, invalid format)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        logger.warn("Validation error: {}", message);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(400)
            .error("Bad Request")
            .message(message)
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(400).body(error);
    }

    /**
     * Handle missing multipart parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, WebRequest request) {

        String message = String.format("Required parameter '%s' is missing",
            ex.getParameterName());

        logger.warn("Missing parameter: {}", ex.getParameterName());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(400)
            .error("Bad Request")
            .message(message)
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(400).body(error);
    }

    /**
     * Handle authentication errors
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationError(
            AuthenticationException ex, WebRequest request) {

        logger.warn("Authentication failed: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(401)
            .error("Unauthorized")
            .message("Invalid credentials")
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(401).body(error);
    }

    /**
     * Handle all other unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        logger.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(500)
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(500).body(error);
    }
}
```

### 10.5 Retryable vs Non-Retryable Errors

**Retryable Errors (will trigger retry):**
- `SocketTimeoutException` - Network timeout
- `ConnectException` - Connection refused
- `FlickrException` with 5xx status - Flickr server error
- `IOException` for network issues

**Non-Retryable Errors (fail immediately):**
- `FlickrException` with 401 status - Authentication failure
- `FlickrException` with 400 status - Invalid parameters
- `FlickrException` with 403 status - Forbidden
- `FlickrException` with 404 status - Not found
- `FlickrException` with 429 status - Rate limit exceeded
- Validation exceptions - Invalid input

---

## 11. Logging Strategy

### 11.1 Log Levels & Usage

#### DEBUG Level

**When to use:** Development, troubleshooting

**What to log:**
- Request/response payloads (sanitized, no passwords/tokens)
- Flickr API call details (method, parameters)
- Retry attempts with delays
- Configuration loading events
- Internal state transitions

**Examples:**
```java
logger.debug("Uploading photo: file={}, album={}, title={}",
    file.getOriginalFilename(), album, title);
logger.debug("Flickr API call: flickr.photosets.create, title={}", albumName);
logger.debug("Retrying in {}ms...", delay);
logger.debug("Loaded Flickr configuration: apiKey={}", maskToken(apiKey));
```

#### INFO Level

**When to use:** Normal operation

**What to log:**
- Upload start/completion with metadata
- Album creation events
- Authentication success
- Application startup/shutdown
- Significant state changes

**Examples:**
```java
logger.info("Photo uploaded: photoId={}, album={}, duration={}ms, size={}, user={}",
    photoId, album, duration, fileSize, username);
logger.info("Album created: albumId={}, name={}", albumId, albumName);
logger.info("Authentication successful: user={}", username);
logger.info("Application started on port {}", port);
```

#### WARN Level

**When to use:** Recoverable issues, degraded performance

**What to log:**
- Retry attempts due to transient failures
- Slow API responses (>3 seconds)
- Resource warnings (disk space <10%, memory >90%)
- Deprecated API usage

**Examples:**
```java
logger.warn("Upload attempt {} failed: {}, retrying in {}ms",
    attemptNumber, errorMessage, delay);
logger.warn("Slow Flickr API response: duration={}ms (threshold: 3000ms)", duration);
logger.warn("Low disk space: free={}MB, threshold={}MB", freeSpace, threshold);
```

#### ERROR Level

**When to use:** Failures requiring attention

**What to log:**
- Upload failures after all retries
- Authentication failures
- Flickr API errors
- Unexpected exceptions with full stack traces
- Configuration errors

**Examples:**
```java
logger.error("Upload failed after 3 attempts: {}", ex.getMessage(), ex);
logger.error("Authentication failed for user: {}", username);
logger.error("Flickr API error: code={}, message={}", errorCode, errorMessage);
logger.error("Unexpected exception during upload", ex);
logger.error("Missing required configuration: flickr.api.key");
```

### 11.2 Structured Logging Examples

#### Successful Upload Log Sequence

```
2025-11-30 17:45:20 [http-nio-8080-exec-1] INFO  UploadController - Upload request received: file=sunset.jpg, size=2457600, album=Summer Vacation 2024, user=john.doe
2025-11-30 17:45:20 [http-nio-8080-exec-1] DEBUG UploadService - Starting upload workflow
2025-11-30 17:45:20 [http-nio-8080-exec-1] DEBUG AlbumService - Checking if album exists: Summer Vacation 2024
2025-11-30 17:45:21 [http-nio-8080-exec-1] INFO  AlbumService - Album found: albumId=72157720123456789
2025-11-30 17:45:21 [http-nio-8080-exec-1] DEBUG FlickrService - Uploading photo to Flickr
2025-11-30 17:45:23 [http-nio-8080-exec-1] INFO  FlickrService - Photo uploaded to Flickr: photoId=53451234567
2025-11-30 17:45:23 [http-nio-8080-exec-1] DEBUG AlbumService - Adding photo to album: photoId=53451234567, albumId=72157720123456789
2025-11-30 17:45:23 [http-nio-8080-exec-1] INFO  AlbumService - Photo added to album successfully
2025-11-30 17:45:23 [http-nio-8080-exec-1] INFO  UploadService - Photo uploaded successfully: photoId=53451234567, album=Summer Vacation 2024, duration=3124ms, fileSize=2457600, user=john.doe
```

#### Failed Upload with Retry Log Sequence

```
2025-11-30 17:45:20 [http-nio-8080-exec-1] INFO  UploadController - Upload request received: file=sunset.jpg, size=2457600, album=Summer Vacation 2024, user=john.doe
2025-11-30 17:45:20 [http-nio-8080-exec-1] DEBUG UploadService - Starting upload workflow
2025-11-30 17:45:20 [http-nio-8080-exec-1] DEBUG AlbumService - Checking if album exists: Summer Vacation 2024
2025-11-30 17:45:21 [http-nio-8080-exec-1] INFO  AlbumService - Album found: albumId=72157720123456789
2025-11-30 17:45:21 [http-nio-8080-exec-1] DEBUG FlickrService - Uploading photo to Flickr
2025-11-30 17:45:51 [http-nio-8080-exec-1] WARN  FlickrService - Upload attempt 1 failed: Connection timeout (30000ms)
2025-11-30 17:45:51 [http-nio-8080-exec-1] DEBUG FlickrService - Retrying in 1000ms...
2025-11-30 17:45:52 [http-nio-8080-exec-1] DEBUG FlickrService - Retry attempt 1: Uploading photo to Flickr
2025-11-30 17:46:22 [http-nio-8080-exec-1] WARN  FlickrService - Upload attempt 2 failed: Connection timeout (30000ms)
2025-11-30 17:46:22 [http-nio-8080-exec-1] DEBUG FlickrService - Retrying in 2000ms...
2025-11-30 17:46:24 [http-nio-8080-exec-1] DEBUG FlickrService - Retry attempt 2: Uploading photo to Flickr
2025-11-30 17:46:54 [http-nio-8080-exec-1] WARN  FlickrService - Upload attempt 3 failed: Connection timeout (30000ms)
2025-11-30 17:46:54 [http-nio-8080-exec-1] ERROR FlickrService - Upload failed after 3 attempts
2025-11-30 17:46:54 [http-nio-8080-exec-1] ERROR GlobalExceptionHandler - Upload failed: Failed to upload photo after 3 retries
pl.czerwiu.flickr.upldr.exception.RetryExhaustedException: Failed to upload photo after 3 retries
    at pl.czerwiu.flickr.upldr.service.FlickrService.recoverFromUploadFailure(FlickrService.java:78)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    ... [full stack trace]
```

### 11.3 Sensitive Data Masking

**Implementation:**

```java
public class LogMaskingUtil {

    /**
     * Mask password completely
     */
    public static String maskPassword(String password) {
        return "****";
    }

    /**
     * Mask OAuth token (show first 4 chars)
     */
    public static String maskToken(String token) {
        if (token == null || token.length() <= 4) {
            return "****";
        }
        return token.substring(0, 4) + "****";
    }

    /**
     * Mask user NSID (show first 4 and last 4 chars)
     */
    public static String maskNsid(String nsid) {
        if (nsid == null || nsid.length() <= 8) {
            return "****";
        }
        int length = nsid.length();
        return nsid.substring(0, 4) + "****" + nsid.substring(length - 4);
    }

    /**
     * Mask API secret (show first 4 chars)
     */
    public static String maskSecret(String secret) {
        return maskToken(secret);
    }
}
```

**Usage:**

```java
logger.debug("Flickr client initialized: apiKey={}, apiSecret={}, nsid={}",
    LogMaskingUtil.maskToken(apiKey),
    LogMaskingUtil.maskSecret(apiSecret),
    LogMaskingUtil.maskNsid(userNsid)
);
// Output: "Flickr client initialized: apiKey=a1b2****, apiSecret=9876****, nsid=1234****@N01"
```

---

## 12. Monitoring & Observability

### 12.1 Spring Boot Actuator Endpoints

#### Exposed Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| /actuator/health | GET | Public | Service health status |
| /actuator/metrics | GET | Protected | List of available metrics |
| /actuator/metrics/{name} | GET | Protected | Specific metric details |
| /actuator/info | GET | Protected | Application information |

#### Configuration

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true
```

### 12.2 Health Indicators

#### Built-in Health Indicators

**DiskSpaceHealthIndicator:**
- Checks available disk space
- Threshold: 10MB minimum
- Status: UP if above threshold, DOWN if below

**PingHealthIndicator:**
- Basic liveness check
- Always returns UP if application is running

#### Custom Health Indicator (Future Enhancement)

```java
@Component
public class FlickrApiHealthIndicator implements HealthIndicator {

    private final FlickrService flickrService;

    @Override
    public Health health() {
        try {
            // Ping Flickr API to check connectivity
            boolean isReachable = flickrService.pingFlickrApi();

            if (isReachable) {
                return Health.up()
                    .withDetail("flickr-api", "reachable")
                    .build();
            } else {
                return Health.down()
                    .withDetail("flickr-api", "unreachable")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("flickr-api", "error")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### 12.3 Custom Metrics (Micrometer)

#### Metric Definitions

| Metric Name | Type | Description | Tags |
|-------------|------|-------------|------|
| upload.total | Counter | Total upload attempts | - |
| upload.success | Counter | Successful uploads | - |
| upload.failure | Counter | Failed uploads | - |
| upload.duration | Timer | Upload duration distribution | - |
| upload.file.size | DistributionSummary | File size distribution | - |

#### Implementation

```java
@Service
public class UploadService {

    private final MeterRegistry meterRegistry;
    private final AlbumService albumService;
    private final FlickrService flickrService;

    public UploadResponse upload(MultipartFile file, UploadRequest request) {
        // Start timer
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            // Upload workflow
            String albumId = albumService.ensureAlbum(request.getAlbum());
            String photoId = flickrService.uploadPhoto(file, request);
            albumService.addPhotoToAlbum(photoId, albumId);

            // Record success metrics
            meterRegistry.counter("upload.success").increment();
            meterRegistry.summary("upload.file.size").record(file.getSize());

            // Build response
            UploadResponse response = UploadResponse.builder()
                .photoId(photoId)
                .album(request.getAlbum())
                .albumId(albumId)
                .uploadedAt(Instant.now())
                .status("SUCCESS")
                .url(buildFlickrUrl(photoId))
                .build();

            return response;

        } catch (Exception e) {
            // Record failure metric
            meterRegistry.counter("upload.failure").increment();
            throw e;

        } finally {
            // Record duration and total
            sample.stop(meterRegistry.timer("upload.duration"));
            meterRegistry.counter("upload.total").increment();
        }
    }
}
```

### 12.4 Metric Queries & Analysis

#### Query Examples

**Total uploads:**
```bash
curl -u user:pass http://localhost:8080/actuator/metrics/upload.total
```

**Success rate calculation:**
```
success_rate = (upload.success / upload.total) * 100
```

**Failure rate calculation:**
```
failure_rate = (upload.failure / upload.total) * 100
```

**Average upload duration:**
```bash
curl -u user:pass http://localhost:8080/actuator/metrics/upload.duration
# Look for TOTAL_TIME / COUNT
```

**P95 response time:**
- Use Micrometer percentiles (configured in application.yml)
- Or export to Prometheus/Grafana for advanced percentile calculations

### 12.5 Alerting Criteria (Future)

**Recommended Alerts:**

| Alert | Condition | Severity | Action |
|-------|-----------|----------|--------|
| High Error Rate | upload.failure/upload.total > 5% | Critical | Investigate immediately |
| Service Down | /actuator/health returns DOWN | Critical | Restart service |
| Slow Uploads | upload.duration P95 > 10s | Warning | Check Flickr API status |
| Low Disk Space | diskSpace.free < 1GB | Warning | Clean up logs |
| High Memory Usage | jvm.memory.used > 90% | Warning | Check for memory leaks |

---

## 13. Testing Strategy

### 13.1 Test Pyramid

```
        /\
       /E2E\         5% (40 tests)
      /------\       - Full upload flow
     /  INT   \      - Mocked Flickr API
    /----------\
   /   UNIT     \    25% (200 tests)
  /--------------\   - Controller → Service integration
 /                \  - Spring context tests
/                  \
--------------------
      UNIT          70% (560 tests)
                    - Service logic
                    - Utilities
                    - Exception handling
                    - Validation

Total: ~800 tests to achieve 80% coverage
```

### 13.2 Test Coverage Requirements (JaCoCo)

**Minimum Thresholds:**

| Coverage Type | Target |
|---------------|--------|
| Line Coverage | 80% |
| Branch Coverage | 75% |
| Method Coverage | 85% |
| Class Coverage | 90% |

**Maven Plugin Configuration:**

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.75</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 13.3 Unit Tests (70% of tests)

#### UploadServiceTest

```java
@ExtendWith(MockitoExtension.class)
class UploadServiceTest {

    @Mock
    private AlbumService albumService;

    @Mock
    private FlickrService flickrService;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private UploadService uploadService;

    @Test
    void upload_Success_ReturnsUploadResponse() {
        // Given
        MultipartFile file = createMockFile();
        UploadRequest request = createUploadRequest();
        when(albumService.ensureAlbum(anyString())).thenReturn("albumId123");
        when(flickrService.uploadPhoto(any(), any())).thenReturn("photoId456");

        // When
        UploadResponse response = uploadService.upload(file, request);

        // Then
        assertThat(response.getPhotoId()).isEqualTo("photoId456");
        assertThat(response.getAlbumId()).isEqualTo("albumId123");
        verify(albumService).addPhotoToAlbum("photoId456", "albumId123");
    }

    @Test
    void upload_FlickrFailure_ThrowsException() {
        // Given
        MultipartFile file = createMockFile();
        UploadRequest request = createUploadRequest();
        when(flickrService.uploadPhoto(any(), any()))
            .thenThrow(new FlickrUploadException("Upload failed", "Network error"));

        // When & Then
        assertThrows(FlickrUploadException.class, () -> {
            uploadService.upload(file, request);
        });
    }
}
```

#### FlickrServiceTest (with Retry)

```java
@ExtendWith(MockitoExtension.class)
@EnableRetry
class FlickrServiceTest {

    @Mock
    private Flickr flickrClient;

    @Spy
    @InjectMocks
    private FlickrService flickrService;

    @Test
    void uploadPhoto_Success_ReturnsPhotoId() throws Exception {
        // Given
        MultipartFile file = createMockFile();
        UploadMetadata metadata = createMetadata();
        when(flickrClient.getUploader().upload(any(), any()))
            .thenReturn("photoId123");

        // When
        String photoId = flickrService.uploadPhoto(file, metadata);

        // Then
        assertThat(photoId).isEqualTo("photoId123");
        verify(flickrClient.getUploader(), times(1)).upload(any(), any());
    }

    @Test
    void uploadPhoto_TransientError_RetriesAndSucceeds() throws Exception {
        // Given
        MultipartFile file = createMockFile();
        UploadMetadata metadata = createMetadata();
        when(flickrClient.getUploader().upload(any(), any()))
            .thenThrow(new SocketTimeoutException("Timeout"))
            .thenReturn("photoId123");  // Succeeds on 2nd attempt

        // When
        String photoId = flickrService.uploadPhoto(file, metadata);

        // Then
        assertThat(photoId).isEqualTo("photoId123");
        verify(flickrClient.getUploader(), times(2)).upload(any(), any());
    }

    @Test
    void uploadPhoto_AllRetriesFail_ThrowsRetryExhaustedException() throws Exception {
        // Given
        MultipartFile file = createMockFile();
        UploadMetadata metadata = createMetadata();
        when(flickrClient.getUploader().upload(any(), any()))
            .thenThrow(new SocketTimeoutException("Timeout"));

        // When & Then
        assertThrows(RetryExhaustedException.class, () -> {
            flickrService.uploadPhoto(file, metadata);
        });

        // Verify 3 attempts
        verify(flickrClient.getUploader(), times(3)).upload(any(), any());
    }
}
```

### 13.4 Integration Tests (25% of tests)

#### UploadControllerIntegrationTest

```java
@SpringBootTest
@AutoConfigureMockMvc
class UploadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlickrService flickrService;

    @Test
    @WithMockUser(username = "testuser")
    void upload_ValidRequest_Returns200() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(flickrService.uploadPhoto(any(), any())).thenReturn("photoId123");

        // When & Then
        mockMvc.perform(multipart("/upload")
                .file(file)
                .param("album", "Test Album")
                .param("title", "Test Title"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.photoId").value("photoId123"))
            .andExpect(jsonPath("$.album").value("Test Album"));
    }

    @Test
    void upload_NoAuth_Returns401() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "test image content".getBytes());

        // When & Then
        mockMvc.perform(multipart("/upload")
                .file(file)
                .param("album", "Test Album"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void upload_MissingAlbum_Returns400() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "test image content".getBytes());

        // When & Then
        mockMvc.perform(multipart("/upload")
                .file(file))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("album")));
    }
}
```

#### SecurityIntegrationTest

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void actuatorHealth_NoAuth_Returns200() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void actuatorMetrics_NoAuth_Returns401() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void actuatorMetrics_WithAuth_Returns200() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.names").isArray());
    }
}
```

### 13.5 E2E Tests (5% of tests)

#### UploadE2ETest (with WireMock)

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UploadE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        configureFor("localhost", 8089);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void completeUploadFlow_Success() {
        // Mock Flickr API responses
        stubFor(post(urlEqualTo("/services/upload"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("<photoid>12345</photoid>")));

        stubFor(post(urlPathMatching("/services/rest.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"stat\":\"ok\"}")));

        // Prepare request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource("test.jpg"));
        body.add("album", "Test Album");
        body.add("title", "Test Photo");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBasicAuth("testuser", "testpass");

        HttpEntity<MultiValueMap<String, Object>> request =
            new HttpEntity<>(body, headers);

        // Execute request
        ResponseEntity<UploadResponse> response = restTemplate.exchange(
            "http://localhost:" + port + "/upload",
            HttpMethod.POST,
            request,
            UploadResponse.class
        );

        // Assertions
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getPhotoId()).isEqualTo("12345");
        assertThat(response.getBody().getAlbum()).isEqualTo("Test Album");

        // Verify Flickr API was called
        verify(postRequestedFor(urlEqualTo("/services/upload")));
    }
}
```

### 13.6 Running Tests

**Run all tests:**
```bash
./mvnw test
```

**Run with coverage:**
```bash
./mvnw clean test jacoco:report
```

**View coverage report:**
```bash
open target/site/jacoco/index.html
```

**Run specific test class:**
```bash
./mvnw test -Dtest=UploadServiceTest
```

**Run specific test method:**
```bash
./mvnw test -Dtest=UploadServiceTest#upload_Success_ReturnsUploadResponse
```

**Skip tests (not recommended):**
```bash
./mvnw clean package -DskipTests
```

---

## 14. Deployment Architecture

### 14.1 Dockerfile (Multi-Stage Build)

**Location:** `Dockerfile`

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy JAR from build stage
COPY --from=build /app/target/flickr-upldr-1.0-SNAPSHOT.jar app.jar

# Create logs directory and set permissions
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build Docker image:**
```bash
docker build -t flickr-upldr:1.0 .
```

**Run container:**
```bash
docker run -p 8080:8080 \
  -e FLICKR_API_KEY="your-api-key" \
  -e FLICKR_API_SECRET="your-api-secret" \
  -e FLICKR_OAUTH_TOKEN="your-oauth-token" \
  -e FLICKR_OAUTH_TOKEN_SECRET="your-oauth-token-secret" \
  -e FLICKR_USER_NSID="your-nsid" \
  -e FLICKR_USER_NAME="your-username" \
  -e FLICKR_USER_PSWD_HASH="your-password-hash" \
  -v $(pwd)/logs:/app/logs \
  flickr-upldr:1.0
```

### 14.2 docker-compose.yml (Local Development)

**Location:** `docker-compose.yml`

```yaml
version: '3.8'

services:
  flickr-upldr:
    build: .
    container_name: flickr-upldr
    ports:
      - "8080:8080"
    environment:
      - FLICKR_API_KEY=${FLICKR_API_KEY}
      - FLICKR_API_SECRET=${FLICKR_API_SECRET}
      - FLICKR_OAUTH_TOKEN=${FLICKR_OAUTH_TOKEN}
      - FLICKR_OAUTH_TOKEN_SECRET=${FLICKR_OAUTH_TOKEN_SECRET}
      - FLICKR_USER_NSID=${FLICKR_USER_NSID}
      - FLICKR_USER_NAME=${FLICKR_USER_NAME}
      - FLICKR_USER_PSWD_HASH=${FLICKR_USER_PSWD_HASH}
    volumes:
      - ./logs:/app/logs
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 40s
```

**.env file:**

```env
FLICKR_API_KEY=your-api-key-here
FLICKR_API_SECRET=your-api-secret-here
FLICKR_OAUTH_TOKEN=your-oauth-token-here
FLICKR_OAUTH_TOKEN_SECRET=your-oauth-token-secret-here
FLICKR_USER_NSID=your-nsid-here
FLICKR_USER_NAME=your-username-here
FLICKR_USER_PSWD_HASH=your-password-hash-here
```

**Run with Docker Compose:**
```bash
docker-compose up -d
```

**View logs:**
```bash
docker-compose logs -f
```

**Stop:**
```bash
docker-compose down
```

### 14.3 CI/CD Pipeline (GitHub Actions)

**Location:** `.github/workflows/ci-cd.yml`

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    name: Run Tests
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK 25
      uses: actions/setup-java@v3
      with:
        java-version: '25'
        distribution: 'temurin'
        cache: maven

    - name: Run tests with coverage
      run: ./mvnw clean test jacoco:report

    - name: Check coverage threshold
      run: ./mvnw jacoco:check

    - name: Upload coverage report
      uses: codecov/codecov-action@v3
      with:
        files: ./target/site/jacoco/jacoco.xml
        flags: unittests
        name: codecov-umbrella

  build:
    name: Build Application
    needs: test
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK 25
      uses: actions/setup-java@v3
      with:
        java-version: '25'
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      run: ./mvnw clean package -DskipTests

    - name: Upload JAR artifact
      uses: actions/upload-artifact@v3
      with:
        name: flickr-upldr-jar
        path: target/flickr-upldr-1.0-SNAPSHOT.jar

  docker:
    name: Build and Push Docker Image
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2

    - name: Login to Docker Hub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}

    - name: Build and push Docker image
      uses: docker/build-push-action@v4
      with:
        context: .
        push: true
        tags: |
          your-dockerhub-username/flickr-upldr:latest
          your-dockerhub-username/flickr-upldr:${{ github.sha }}
        cache-from: type=gha
        cache-to: type=gha,mode=max

    - name: Image digest
      run: echo ${{ steps.docker_build.outputs.digest }}
```

### 14.4 Kubernetes Deployment (Optional)

**deployment.yaml:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: flickr-upldr
  labels:
    app: flickr-upldr
spec:
  replicas: 2
  selector:
    matchLabels:
      app: flickr-upldr
  template:
    metadata:
      labels:
        app: flickr-upldr
    spec:
      containers:
      - name: flickr-upldr
        image: your-dockerhub-username/flickr-upldr:latest
        ports:
        - containerPort: 8080
        env:
        - name: FLICKR_API_KEY
          valueFrom:
            secretKeyRef:
              name: flickr-secrets
              key: api-key
        - name: FLICKR_API_SECRET
          valueFrom:
            secretKeyRef:
              name: flickr-secrets
              key: api-secret
        # ... other env vars from secrets
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 40
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: flickr-upldr-service
spec:
  selector:
    app: flickr-upldr
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

## 15. Implementation Plan

### 15.1 Phase 1: Foundation 

**Tasks:**

1. **Update pom.xml** (4 hours)
   - Add Flickr4Java dependency
   - Add Spring Boot Actuator
   - Add SpringDoc OpenAPI
   - Add Spring Retry
   - Add JaCoCo plugin

2. **Create Configuration Classes** (8 hours)
   - FlickrConfig: Initialize Flickr client
   - SecurityConfig: Basic Auth setup
   - FlickrProperties: @ConfigurationProperties
   - RetryConfig: Enable retry

3. **Set up Logging** (4 hours)
   - Create logback-spring.xml
   - Configure rotation and retention
   - Test log output

4. **Create Package Structure** (4 hours)
   - controller, service, config, exception, dto packages
   - Create DTOs: UploadRequest, UploadResponse, ErrorResponse

**Deliverables:**
- Dependencies added
- Configuration framework ready
- Logging working
- Package structure in place

---

### 15.2 Phase 2: Core Features 

**Tasks:**

5. **Implement FlickrService** (12 hours)
   - Flickr4Java wrapper
   - Upload photo method
   - @Retryable annotation
   - Error translation

6. **Implement UploadService** (12 hours)
   - Orchestrate upload workflow
   - Metrics tracking
   - Logging
   - Error handling

7. **Implement AlbumService** (8 hours)
   - Check album existence
   - Create album
   - Add photo to album

8. **Create UploadController** (8 hours)
   - POST /upload endpoint
   - Parameter validation
   - Response mapping

**Deliverables:**
- Core upload functionality working
- Retry logic implemented
- Metrics tracked

---

### 15.3 Phase 3: Security & Error Handling 

**Tasks:**

9. **Configure Spring Security** (8 hours)
   - Basic Auth setup
   - SHA-256 password validation
   - Security rules (public /actuator/health, protected others)

10. **Implement GlobalExceptionHandler** (8 hours)
    - @ControllerAdvice
    - Exception to HTTP mapping
    - ErrorResponse generation

11. **Create Exception Hierarchy** (4 hours)
    - FlickrUploaderException (base)
    - Specific exceptions
    - Exception tests

12. **Add Validation** (4 hours)
    - @Valid annotations
    - Parameter constraints
    - Validation error handling

**Deliverables:**
- Security working
- Error handling comprehensive
- Validation in place

---

### 15.4 Phase 4: Monitoring & Documentation 

**Tasks:**

13. **Configure Actuator Endpoints** (4 hours)
    - Enable health, metrics, info
    - Configure security
    - Test endpoints

14. **Add Custom Metrics** (8 hours)
    - Micrometer counters, timers
    - Metrics in UploadService
    - Test metric collection

15. **Configure SpringDoc OpenAPI** (8 hours)
    - OpenApiConfig setup
    - Endpoint annotations
    - Test Swagger UI

16. **Add JavaDoc** (4 hours)
    - Document public APIs
    - Class-level descriptions
    - Method documentation

**Deliverables:**
- Monitoring working
- Swagger UI accessible
- Code documented

---

### 15.5 Phase 5: Testing

**Tasks:**

17. **Unit Tests** (20 hours)
    - UploadServiceTest
    - FlickrServiceTest (with retry tests)
    - AlbumServiceTest
    - SecurityConfigTest
    - ExceptionHandlerTest
    - Utility tests

18. **Integration Tests** (16 hours)
    - UploadControllerIntegrationTest
    - SecurityIntegrationTest
    - ActuatorEndpointsTest
    - Full Spring context tests

19. **E2E Tests** (12 hours)
    - UploadE2ETest with WireMock
    - Full flow testing
    - Error scenario testing

20. **Achieve 80% Coverage** (8 hours)
    - Identify gaps
    - Add missing tests
    - Verify with JaCoCo

**Deliverables:**
- 800+ tests written
- 80%+ code coverage
- All tests passing

---

### 15.6 Phase 6: Deployment 

**Tasks:**

21. **Create Dockerfile** (4 hours)
    - Multi-stage build
    - Non-root user
    - Health check

22. **Create docker-compose.yml** (4 hours)
    - Service definition
    - Environment variables
    - Volume mounts

23. **Set up GitHub Actions** (8 hours)
    - CI/CD pipeline
    - Test, build, push stages
    - Coverage enforcement

24. **Test Deployment** (8 hours)
    - Build Docker image
    - Run with Docker Compose
    - Test all endpoints
    - Verify logs and metrics

**Deliverables:**
- Docker image built
- CI/CD pipeline working
- Deployment tested

---

### 15.7 Phase 7: Documentation & Release 

**Tasks:**

25. **Finalize PRD** (8 hours)
    - Review all sections
    - Update based on implementation
    - Add diagrams

26. **Create User Guide** (8 hours)
    - Getting started
    - API usage examples
    - Configuration guide
    - Troubleshooting

27. **Create Runbook** (4 hours)
    - Deployment steps
    - Monitoring guide
    - Common issues
    - Emergency procedures

28. **Tag v1.0.0** (4 hours)
    - Final code review
    - Git tag
    - GitHub release
    - Announcement

**Deliverables:**
- Complete documentation
- v1.0.0 released
- Production-ready

---

## 16. Dependencies & Requirements

### 16.1 Maven Dependencies (pom.xml)

**Add to pom.xml:**

```xml
<!-- Flickr API Integration -->
<dependency>
    <groupId>com.flickr4java</groupId>
    <artifactId>flickr4java</artifactId>
    <version>3.0.11</version>
</dependency>

<!-- Spring Boot Actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- OpenAPI Documentation -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- Spring Retry -->
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
</dependency>

<!-- Lombok (optional, for cleaner code) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- JaCoCo Maven Plugin -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</plugin>
```

### 16.2 External Services

| Service | Purpose | Required | URL |
|---------|---------|----------|-----|
| Flickr API | Photo upload destination | Yes | https://www.flickr.com/services/api/ |

### 16.3 Development Tools

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 25 | Runtime & compilation |
| Maven | 3.9+ | Build tool |
| Docker | 20.10+ | Containerization |
| Git | 2.0+ | Version control |

### 16.4 Production Requirements

| Requirement | Specification |
|-------------|---------------|
| CPU | 2 cores minimum |
| Memory | 1GB RAM minimum (2GB recommended) |
| Disk | 20GB minimum (for logs and temp files) |
| Network | Outbound HTTPS to Flickr API |
| OS | Linux (Ubuntu 22.04 recommended) |

---

## Appendix A: Glossary

| Term | Definition |
|------|------------|
| Basic Auth | HTTP Basic Authentication, username/password sent in Authorization header |
| Flickr4Java | Java library for Flickr API integration |
| JaCoCo | Java Code Coverage tool |
| Micrometer | Application metrics facade (similar to SLF4J for logging) |
| NSID | Flickr user numeric ID (e.g., "12345678@N01") |
| OAuth 1.0a | Authentication protocol used by Flickr |
| PRD | Product Requirements Document |
| SHA-256 | Secure Hash Algorithm, 256-bit cryptographic hash |
| SpringDoc | Library for OpenAPI 3.0 specification and Swagger UI |

---

## Appendix B: References

| Reference | URL |
|-----------|-----|
| Flickr API Documentation | https://www.flickr.com/services/api/ |
| Flickr4Java GitHub | https://github.com/boncey/Flickr4Java |
| Spring Boot Documentation | https://docs.spring.io/spring-boot/4.0.0/reference/ |
| SpringDoc OpenAPI | https://springdoc.org/ |
| Micrometer Documentation | https://micrometer.io/docs |




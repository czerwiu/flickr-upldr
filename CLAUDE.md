# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot-based web service that provides an HTTP endpoint for uploading photos to Flickr. The application uses basic authentication secured with SHA-256 hashed passwords and integrates with Flickr's API using OAuth tokens.

## Build & Run Commands

**Build the project:**
```bash
./mvnw clean package
```

**Run the application:**
```bash
./mvnw spring-boot:run
```

**Run with required runtime variables:**
```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="\
  --flickr.api.key=YOUR_API_KEY \
  --flickr.api.secret=YOUR_API_SECRET \
  --flickr.oauth.token=YOUR_OAUTH_TOKEN \
  --flickr.oauth.token.secret=YOUR_OAUTH_TOKEN_SECRET \
  --flickr.user.nsid=YOUR_USER_NSID \
  --flickr.user.name=YOUR_USERNAME \
  --flickr.user.pswd=SHA256_HASH_OF_PASSWORD"
```

**Run tests:**
```bash
./mvnw test
```

**Run a single test class:**
```bash
./mvnw test -Dtest=ClassName
```

**Run a single test method:**
```bash
./mvnw test -Dtest=ClassName#methodName
```

## Required Configuration

The application requires 7 runtime variables to function:
- `flickr.api.key` - Flickr API key
- `flickr.api.secret` - Flickr API secret
- `flickr.oauth.token` - OAuth token from Flickr
- `flickr.oauth.token.secret` - OAuth token secret from Flickr
- `flickr.user.nsid` - Flickr user NSID (numeric identifier)
- `flickr.user.name` - Username for basic auth
- `flickr.user.pswd` - SHA-256 hash of password for basic auth

## Architecture

**Technology Stack:**
- Java 25
- Spring Boot 4.0.0
- Maven
- Spring Security (Basic Authentication)
- Spring Web MVC
- Flickr4Java library (for Flickr API integration)

**Package Structure:**
- Base package: `pl.czerwiu.flickr.upldr`
- Main application class: `App.java` (standard Spring Boot entry point)

**API Endpoint:**
- `POST /upload` - Multipart file upload endpoint
  - Required: `file` (multipart), `album` (string)
  - Optional: `title`, `description`, `tags` (comma-separated)
  - Creates album if it doesn't exist
  - Protected by basic authentication

**Security:**
- Uses Spring Security for basic authentication
- Password stored as SHA-256 hash in configuration
- All endpoints require authentication

# Multi-stage build for smaller final image

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-25-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime image
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Create logs directory with proper permissions
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app/logs

# Copy the built jar from builder stage
COPY --from=builder /app/target/flickr-upldr-1.0-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

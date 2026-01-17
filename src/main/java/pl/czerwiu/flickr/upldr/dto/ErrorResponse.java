package pl.czerwiu.flickr.upldr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Error response DTO for all error cases.
 * Provides standardized error information to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Timestamp when error occurred (ISO 8601 format).
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code (e.g., 400, 401, 500).
     */
    private int status;

    /**
     * Error type (e.g., "Bad Request", "Unauthorized", "Internal Server Error").
     */
    private String error;

    /**
     * Human-readable error message.
     */
    private String message;

    /**
     * Additional error details (optional).
     */
    private String details;

    /**
     * Request path that caused the error.
     */
    private String path;
}

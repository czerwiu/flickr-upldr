package pl.czerwiu.flickr.upldr.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import pl.czerwiu.flickr.upldr.dto.ErrorResponse;
import pl.czerwiu.flickr.upldr.exception.FlickrUploadException;
import pl.czerwiu.flickr.upldr.exception.RetryExhaustedException;

import java.time.LocalDateTime;

/**
 * Global exception handler for all REST controllers.
 * Provides centralized error handling and standardized error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles FlickrUploadException (photo upload failures).
     */
    @ExceptionHandler(FlickrUploadException.class)
    public ResponseEntity<ErrorResponse> handleFlickrUploadException(
            FlickrUploadException ex, WebRequest request) {

        log.error("Flickr upload error: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Failed to upload photo: " + ex.getMessage())
            .details(ex.getDetails())
            .path(extractPath(request))
            .build();

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }

    /**
     * Handles RetryExhaustedException (all retry attempts failed).
     */
    @ExceptionHandler(RetryExhaustedException.class)
    public ResponseEntity<ErrorResponse> handleRetryExhaustedException(
            RetryExhaustedException ex, WebRequest request) {

        log.error("All retry attempts exhausted: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message(ex.getMessage())
            .details("Upload failed after 3 retry attempts. Please try again later.")
            .path(extractPath(request))
            .build();

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }

    /**
     * Handles MissingServletRequestParameterException (missing required parameters).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, WebRequest request) {

        log.warn("Missing required parameter: {}", ex.getParameterName());

        String message = String.format("Required parameter '%s' is missing",
            ex.getParameterName());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(message)
            .path(extractPath(request))
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    /**
     * Handles IllegalArgumentException (validation errors).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Validation error: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ex.getMessage())
            .path(extractPath(request))
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    /**
     * Handles MaxUploadSizeExceededException (file too large).
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex, WebRequest request) {

        log.warn("File size exceeded: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("File size exceeds maximum allowed size (200MB)")
            .details(ex.getMessage())
            .path(extractPath(request))
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("An unexpected error occurred")
            .path(extractPath(request))
            .build();

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }

    /**
     * Extracts request path from WebRequest.
     */
    private String extractPath(WebRequest request) {
        String description = request.getDescription(false);
        return description.replace("uri=", "");
    }
}

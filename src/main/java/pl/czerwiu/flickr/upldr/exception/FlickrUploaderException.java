package pl.czerwiu.flickr.upldr.exception;

import lombok.Getter;

/**
 * Base exception for all application-specific exceptions.
 * All custom exceptions should extend this class.
 */
@Getter
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

    public FlickrUploaderException(String message, String details, Throwable cause) {
        super(message, cause);
        this.details = details;
    }
}

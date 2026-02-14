package pl.czerwiu.flickr.upldr.exception;

/**
 * Exception thrown when Flickr detects a duplicate photo upload (error code 9).
 */
public class DuplicatePhotoException extends FlickrUploaderException {

    public DuplicatePhotoException(String message) {
        super(message);
    }

    public DuplicatePhotoException(String message, String details) {
        super(message, details);
    }

    public DuplicatePhotoException(String message, String details, Throwable cause) {
        super(message, details, cause);
    }
}

package pl.czerwiu.flickr.upldr.exception;

/**
 * Exception thrown when photo upload to Flickr fails.
 */
public class FlickrUploadException extends FlickrUploaderException {

    public FlickrUploadException(String message, String details) {
        super(message, details);
    }

    public FlickrUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlickrUploadException(String message, String details, Throwable cause) {
        super(message, details, cause);
    }
}

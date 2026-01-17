package pl.czerwiu.flickr.upldr.exception;

/**
 * Exception thrown when all retry attempts have been exhausted.
 */
public class RetryExhaustedException extends FlickrUploaderException {

    public RetryExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }
}

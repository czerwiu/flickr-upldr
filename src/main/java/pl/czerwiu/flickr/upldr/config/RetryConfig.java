package pl.czerwiu.flickr.upldr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Configuration for Spring Retry.
 * Enables @Retryable and @Recover annotations.
 */
@Configuration
@EnableRetry
public class RetryConfig {
    // This class enables Spring Retry support
    // No additional configuration needed for basic retry functionality
}

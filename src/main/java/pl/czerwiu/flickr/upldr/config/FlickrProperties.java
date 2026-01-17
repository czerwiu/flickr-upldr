package pl.czerwiu.flickr.upldr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Flickr API integration.
 * Maps properties from application.yml with prefix "flickr".
 */
@Data
@Component
@ConfigurationProperties(prefix = "flickr")
public class FlickrProperties {

    private Api api = new Api();
    private OAuth oauth = new OAuth();
    private User user = new User();

    @Data
    public static class Api {
        /**
         * Flickr API key
         */
        private String key;

        /**
         * Flickr API secret
         */
        private String secret;
    }

    @Data
    public static class OAuth {
        /**
         * OAuth access token
         */
        private String token;

        /**
         * OAuth access token secret
         */
        private String tokenSecret;
    }

    @Data
    public static class User {
        /**
         * Flickr user NSID (numeric identifier)
         */
        private String nsid;

        /**
         * Username for basic authentication
         */
        private String name;

        /**
         * SHA-256 hash of password for basic authentication
         */
        private String pswd;
    }
}

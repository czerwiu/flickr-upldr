package pl.czerwiu.flickr.upldr.config;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.util.AuthStore;
import com.flickr4java.flickr.util.FileAuthStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Configuration class for Flickr API integration.
 * Initializes Flickr4Java client with OAuth credentials.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlickrConfig {

    private final FlickrProperties flickrProperties;

    /**
     * Creates and configures Flickr client bean.
     * Sets up OAuth 1.0a authentication with pre-configured tokens.
     *
     * @return configured Flickr client instance
     */
    @Bean
    public Flickr flickrClient() {
        log.info("Initializing Flickr client");

        // Create Flickr instance with REST transport
        Flickr flickr = new Flickr(
            flickrProperties.getApi().getKey(),
            flickrProperties.getApi().getSecret(),
            new REST()
        );

        // Set up OAuth authentication
        Auth auth = new Auth();
        auth.setPermission(Permission.WRITE);
        auth.setToken(flickrProperties.getOauth().getToken());
        auth.setTokenSecret(flickrProperties.getOauth().getTokenSecret());

        // Configure Flickr client with auth
        flickr.setAuth(auth);

        log.info("Flickr client initialized successfully for user NSID: {}",
            maskNsid(flickrProperties.getUser().getNsid()));

        return flickr;
    }

    @Bean
    @SneakyThrows
    public AuthStore authStore() {
        return new FileAuthStore(new File(System.getProperty("java.io.tmpdir"), "flickr-store"));
    }

    /**
     * Masks user NSID for logging (shows first 4 and last 4 chars).
     */
    private String maskNsid(String nsid) {
        if (nsid == null || nsid.length() <= 8) {
            return "****";
        }
        int length = nsid.length();
        return nsid.substring(0, 4) + "****" + nsid.substring(length - 4);
    }
}

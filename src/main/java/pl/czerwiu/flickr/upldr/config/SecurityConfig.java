package pl.czerwiu.flickr.upldr.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Security configuration for HTTP Basic Authentication.
 * Uses SHA-256 password hashing for credential validation.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final FlickrProperties flickrProperties;

    /**
     * Configures HTTP security with Basic Auth.
     * - /actuator/health is public (no auth required)
     * - /swagger-ui.html and /v3/api-docs are public
     * - All other endpoints require authentication
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .csrf(AbstractHttpConfigurer::disable);  // Disable CSRF for stateless API

        return http.build();
    }

    /**
     * UserDetailsService for Basic Auth.
     * Loads user from FlickrProperties configuration.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username(flickrProperties.getUser().getName())
            .password(flickrProperties.getUser().getPswd())  // SHA-256 hash from config
            .roles("USER")
            .build();

        log.info("Basic Auth configured for user: {}", flickrProperties.getUser().getName());

        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Custom PasswordEncoder for SHA-256 hash comparison.
     * Hashes provided password and compares with stored hash.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return sha256Hash(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                String hashedPassword = sha256Hash(rawPassword.toString());
                return hashedPassword.equals(encodedPassword);
            }

            private String sha256Hash(String password) {
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                    return bytesToHex(hash);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("SHA-256 algorithm not available", e);
                }
            }

            private String bytesToHex(byte[] bytes) {
                StringBuilder hexString = new StringBuilder();
                for (byte b : bytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                return hexString.toString();
            }
        };
    }
}

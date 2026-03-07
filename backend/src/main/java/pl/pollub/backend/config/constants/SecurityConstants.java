package pl.pollub.backend.config.constants;

/**
 * Security-related constants for JWT and authentication.
 * Defines standard headers, algorithm names, and token format specifications.
 */
public final class SecurityConstants {

    // JWT/Authorization headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_SCHEME = "Bearer";
    public static final int BEARER_PREFIX_LENGTH = 7; // "Bearer ".length()

    // Cryptographic algorithms
    public static final String SHA256_ALGORITHM = "SHA-256";

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }
}


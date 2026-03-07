package pl.pollub.backend.config.constants;

/**
 * Validation constants for user input validation.
 * Defines minimum and maximum lengths for usernames, passwords, and other validated fields.
 */
public final class ValidationConstants {

    // Username validation
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 20;

    // Password validation
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 30;

    private ValidationConstants() {
        // Private constructor to prevent instantiation
    }
}


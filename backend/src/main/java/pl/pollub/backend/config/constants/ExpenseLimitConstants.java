package pl.pollub.backend.config.constants;

/**
 * Constants related to expense limits and thresholds.
 * Defines values for expense limit indicators and warning thresholds.
 */
public final class ExpenseLimitConstants {

    // Indicator value for "no limit" / "unlimited"
    public static final double NO_EXPENSE_LIMIT = -1.0;

    // Threshold for warning emails (as a fraction of remaining budget)
    // When remaining budget is 10% or less, send a warning email
    public static final double EXPENSE_WARNING_THRESHOLD = 0.1;

    private ExpenseLimitConstants() {
        // Private constructor to prevent instantiation
    }
}


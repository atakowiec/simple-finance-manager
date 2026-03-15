package pl.pollub.backend.activity;

/**
 * Enum representing all possible activity event types that services can emit.
 * The ActivityLoggingMediator maps each type to a formatted log message.
 */
public enum ActivityEventType {
    // Expense events
    EXPENSE_CREATED,
    EXPENSE_DELETED,
    EXPENSE_UPDATED,

    // Income events
    INCOME_CREATED,
    INCOME_DELETED,
    INCOME_UPDATED,

    // Generic transaction event (e.g. clone operations)
    TRANSACTION_CREATED,

    // Group membership events
    MEMBER_JOINED_GROUP,
    MEMBER_LEFT_GROUP,
    MEMBER_REMOVED_FROM_GROUP,

    // Group lifecycle events
    GROUP_CREATED,
    GROUP_DELETED,

    // Budget events
    BUDGET_EXCEEDED
}


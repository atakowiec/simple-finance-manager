package pl.pollub.backend.transaction.observer;

/**
 * Source that triggered an expense-limit lifecycle evaluation.
 */
public enum ExpenseLimitTriggerSource {
    GROUP_LIMIT_CHANGED,
    EXPENSE_CREATED,
    EXPENSE_UPDATED,
    EXPENSE_DELETED,
    GROUP_LIMIT_UNDONE
}


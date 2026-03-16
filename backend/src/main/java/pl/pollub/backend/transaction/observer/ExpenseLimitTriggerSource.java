package pl.pollub.backend.transaction.observer;

/**
 * Source that triggered an expense-limit lifecycle evaluation.
 */
public enum ExpenseLimitTriggerSource {
    GROUP_LIMIT_CHANGED,
    GROUP_RULE_CHANGED,
    EXPENSE_CREATED,
    EXPENSE_UPDATED,
    EXPENSE_DELETED,
    INCOME_CREATED,
    INCOME_UPDATED,
    INCOME_DELETED,
    GROUP_LIMIT_UNDONE
}


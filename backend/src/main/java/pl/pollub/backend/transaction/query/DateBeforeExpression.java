package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

import java.time.LocalDate;

/**
 * Terminal Expression: Checks if transaction date is before a specified date.
 * Example: "date before 2026-12-31"
 */
public class DateBeforeExpression implements Expression {
    private final LocalDate date;

    public DateBeforeExpression(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean interpret(Transaction transaction) {
        return transaction.getDate() != null && transaction.getDate().isBefore(date);
    }
}


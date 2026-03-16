package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

import java.time.LocalDate;

/**
 * Terminal Expression: Checks if transaction date is after a specified date.
 * Example: "date after 2026-01-01"
 */
public class DateAfterExpression implements Expression {
    private final LocalDate date;

    public DateAfterExpression(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean interpret(Transaction transaction) {
        return transaction.getDate() != null && transaction.getDate().isAfter(date);
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


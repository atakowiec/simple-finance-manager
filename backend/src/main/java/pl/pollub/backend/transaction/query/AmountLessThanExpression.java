package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

/**
 * Terminal Expression: Checks if transaction amount is less than a specified value.
 * Example: "amount < 50"
 */
public class AmountLessThanExpression implements Expression {
    private final double threshold;

    public AmountLessThanExpression(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean interpret(Transaction transaction) {
        return transaction.getAmount() < threshold;
    }
}


package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

/**
 * Terminal Expression: Checks if transaction amount is greater than a specified value.
 * Example: "amount > 100"
 */
public class AmountGreaterThanExpression implements Expression {
    private final double threshold;

    public AmountGreaterThanExpression(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean interpret(Transaction transaction) {
        return transaction.getAmount() > threshold;
    }

    public double getThreshold() {
        return threshold;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


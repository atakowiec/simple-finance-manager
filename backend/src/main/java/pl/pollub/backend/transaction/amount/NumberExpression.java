package pl.pollub.backend.transaction.amount;

/**
 * Terminal expression that stores a numeric value.
 */
public class NumberExpression implements AmountExpression {
    private final double value;

    public NumberExpression(double value) {
        this.value = value;
    }

    @Override
    public double interpret() {
        return value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public <T> T accept(AmountExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


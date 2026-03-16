package pl.pollub.backend.transaction.amount;

/**
 * Non-terminal expression for subtraction.
 */
public class SubtractExpression implements AmountExpression {
    private final AmountExpression left;
    private final AmountExpression right;

    public SubtractExpression(AmountExpression left, AmountExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public double interpret() {
        return left.interpret() - right.interpret();
    }

    public AmountExpression getLeft() {
        return left;
    }

    public AmountExpression getRight() {
        return right;
    }

    @Override
    public <T> T accept(AmountExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


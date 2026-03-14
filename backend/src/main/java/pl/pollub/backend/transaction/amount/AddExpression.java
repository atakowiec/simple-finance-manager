package pl.pollub.backend.transaction.amount;

/**
 * Non-terminal expression for addition.
 */
public class AddExpression implements AmountExpression {
    private final AmountExpression left;
    private final AmountExpression right;

    public AddExpression(AmountExpression left, AmountExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public double interpret() {
        return left.interpret() + right.interpret();
    }
}


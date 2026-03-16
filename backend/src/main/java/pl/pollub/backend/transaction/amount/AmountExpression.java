package pl.pollub.backend.transaction.amount;

/**
 * Abstract expression for transaction amount arithmetic.
 */
public interface AmountExpression {
    double interpret();

    <T> T accept(AmountExpressionVisitor<T> visitor);
}


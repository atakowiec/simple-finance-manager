package pl.pollub.backend.transaction.amount;

/**
 * Abstract expression for transaction amount arithmetic.
 */
@FunctionalInterface
public interface AmountExpression {
    double interpret();
}


package pl.pollub.backend.transaction.amount;

// start visitor
/**
 * Visitor contract for amount expression tree.
 */
public interface AmountExpressionVisitor<T> {
    T visit(AddExpression expression);

    T visit(SubtractExpression expression);

    T visit(NumberExpression expression);
}


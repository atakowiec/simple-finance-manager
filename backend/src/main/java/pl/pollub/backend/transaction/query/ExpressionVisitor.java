package pl.pollub.backend.transaction.query;

// start visitor
/**
 * Visitor contract for transaction query expression tree.
 */
public interface ExpressionVisitor<T> {
    T visit(AndExpression expression);

    T visit(OrExpression expression);

    T visit(NotExpression expression);

    T visit(NameContainsExpression expression);

    T visit(CategoryEqualsExpression expression);

    T visit(AmountGreaterThanExpression expression);

    T visit(AmountLessThanExpression expression);

    T visit(DateAfterExpression expression);

    T visit(DateBeforeExpression expression);
}

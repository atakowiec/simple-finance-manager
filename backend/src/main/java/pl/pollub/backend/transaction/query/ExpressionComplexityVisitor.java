package pl.pollub.backend.transaction.query;

/**
 * Counts nodes in query expression tree as a simple complexity metric.
 */
public class ExpressionComplexityVisitor implements ExpressionVisitor<Integer> {

    @Override
    public Integer visit(AndExpression expression) {
        return 1 + expression.getLeft().accept(this) + expression.getRight().accept(this);
    }

    @Override
    public Integer visit(OrExpression expression) {
        return 1 + expression.getLeft().accept(this) + expression.getRight().accept(this);
    }

    @Override
    public Integer visit(NotExpression expression) {
        return 1 + expression.getExpression().accept(this);
    }

    @Override
    public Integer visit(NameContainsExpression expression) {
        return 1;
    }

    @Override
    public Integer visit(CategoryEqualsExpression expression) {
        return 1;
    }

    @Override
    public Integer visit(AmountGreaterThanExpression expression) {
        return 1;
    }

    @Override
    public Integer visit(AmountLessThanExpression expression) {
        return 1;
    }

    @Override
    public Integer visit(DateAfterExpression expression) {
        return 1;
    }

    @Override
    public Integer visit(DateBeforeExpression expression) {
        return 1;
    }
}


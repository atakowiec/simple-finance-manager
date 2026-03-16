package pl.pollub.backend.transaction.amount;

/**
 * Counts nodes in amount AST as a simple complexity metric.
 */
public class AmountExpressionComplexityVisitor implements AmountExpressionVisitor<Integer> {

    @Override
    public Integer visit(AddExpression expression) {
        return 1 + expression.getLeft().accept(this) + expression.getRight().accept(this);
    }

    @Override
    public Integer visit(SubtractExpression expression) {
        return 1 + expression.getLeft().accept(this) + expression.getRight().accept(this);
    }

    @Override
    public Integer visit(NumberExpression expression) {
        return 1;
    }
}


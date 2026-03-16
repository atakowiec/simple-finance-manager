package pl.pollub.backend.transaction.amount;

/**
 * Produces infix representation of amount expression tree.
 */
public class AmountExpressionPrettyPrintVisitor implements AmountExpressionVisitor<String> {

    @Override
    public String visit(AddExpression expression) {
        return "(" + expression.getLeft().accept(this) + " + " + expression.getRight().accept(this) + ")";
    }

    @Override
    public String visit(SubtractExpression expression) {
        return "(" + expression.getLeft().accept(this) + " - " + expression.getRight().accept(this) + ")";
    }

    @Override
    public String visit(NumberExpression expression) {
        return Double.toString(expression.getValue());
    }
}


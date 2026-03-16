package pl.pollub.backend.transaction.query;

import java.time.format.DateTimeFormatter;

/**
 * Produces a readable normalized form of query expression tree.
 */
public class ExpressionPrettyPrintVisitor implements ExpressionVisitor<String> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public String visit(AndExpression expression) {
        return "(" + expression.getLeft().accept(this) + " AND " + expression.getRight().accept(this) + ")";
    }

    @Override
    public String visit(OrExpression expression) {
        return "(" + expression.getLeft().accept(this) + " OR " + expression.getRight().accept(this) + ")";
    }

    @Override
    public String visit(NotExpression expression) {
        return "(NOT " + expression.getExpression().accept(this) + ")";
    }

    @Override
    public String visit(NameContainsExpression expression) {
        return "name contains '" + expression.getText() + "'";
    }

    @Override
    public String visit(CategoryEqualsExpression expression) {
        return "category = '" + expression.getCategoryName() + "'";
    }

    @Override
    public String visit(AmountGreaterThanExpression expression) {
        return "amount > " + expression.getThreshold();
    }

    @Override
    public String visit(AmountLessThanExpression expression) {
        return "amount < " + expression.getThreshold();
    }

    @Override
    public String visit(DateAfterExpression expression) {
        return "date after " + expression.getDate().format(DATE_FORMAT);
    }

    @Override
    public String visit(DateBeforeExpression expression) {
        return "date before " + expression.getDate().format(DATE_FORMAT);
    }
}


package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

/**
 * Terminal Expression: Checks if transaction category matches a specified name.
 * Example: "category = 'Food'"
 */
public class CategoryEqualsExpression implements Expression {
    private final String categoryName;

    public CategoryEqualsExpression(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean interpret(Transaction transaction) {
        if (transaction.getCategory() == null) {
            return false;
        }
        return transaction.getCategory().getName().equalsIgnoreCase(categoryName);
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


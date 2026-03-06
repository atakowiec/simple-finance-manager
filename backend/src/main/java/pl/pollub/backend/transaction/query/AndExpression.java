package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

/**
 * Non-Terminal Expression: Logical AND operation.
 * Example: "amount > 100 AND category = 'Food'"
 */
public class AndExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public AndExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(Transaction transaction) {
        return left.interpret(transaction) && right.interpret(transaction);
    }
}


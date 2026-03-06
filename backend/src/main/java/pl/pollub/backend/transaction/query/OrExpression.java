package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

/**
 * Non-Terminal Expression: Logical OR operation.
 * Example: "category = 'Food' OR category = 'Transport'"
 */
public class OrExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public OrExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(Transaction transaction) {
        return left.interpret(transaction) || right.interpret(transaction);
    }
}


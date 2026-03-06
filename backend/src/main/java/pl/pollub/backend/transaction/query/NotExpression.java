package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

/**
 * Non-Terminal Expression: Logical NOT operation.
 * Example: "NOT category = 'Food'"
 */
public class NotExpression implements Expression {
    private final Expression expression;

    public NotExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean interpret(Transaction transaction) {
        return !expression.interpret(transaction);
    }
}


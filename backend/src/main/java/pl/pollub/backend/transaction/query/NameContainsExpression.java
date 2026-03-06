package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

/**
 * Terminal Expression: Checks if transaction name contains a specified text.
 * Example: "name contains 'coffee'"
 */
public class NameContainsExpression implements Expression {
    private final String text;

    public NameContainsExpression(String text) {
        this.text = text;
    }

    @Override
    public boolean interpret(Transaction transaction) {
        if (transaction.getName() == null) {
            return false;
        }
        return transaction.getName().toLowerCase().contains(text.toLowerCase());
    }
}


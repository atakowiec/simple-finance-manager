package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

// start open close principle, Abstrakcja i sterowanie danymi
/**
 * Abstract Expression interface for the Interpreter pattern.
 * Represents a query expression that can be evaluated against a transaction.
 */
public interface Expression {
    /**
     * Interprets and evaluates the expression against a transaction.
     *
     * @param transaction the transaction to evaluate
     * @return true if the transaction matches the expression criteria
     */
    boolean interpret(Transaction transaction);
}


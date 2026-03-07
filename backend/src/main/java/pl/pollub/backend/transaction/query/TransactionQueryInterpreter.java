package pl.pollub.backend.transaction.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.transaction.model.Transaction;

import java.util.List;
import java.util.stream.Collectors;

// start interpreter
/**
 * Service that executes transaction queries using the Interpreter pattern.
 * This is the Context in the Interpreter pattern.
 */
@Service
@RequiredArgsConstructor
public class TransactionQueryInterpreter {

    private final TransactionQueryParser parser;

    /**
     * Filters transactions based on a query string.
     *
     * @param transactions list of transactions to filter
     * @param query query string in Transaction Query Language
     * @return filtered list of transactions matching the query
     */
    public <T extends Transaction> List<T> filter(List<T> transactions, String query) {
        Expression expression = parser.parse(query);

        // start functional interface
        return transactions.stream()
                .filter(expression::interpret)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a single transaction matches the query.
     *
     * @param transaction transaction to check
     * @param query query string in Transaction Query Language
     * @return true if transaction matches the query
     */
    public boolean matches(Transaction transaction, String query) {
        Expression expression = parser.parse(query);
        return expression.interpret(transaction);
    }
}


package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

import java.util.List;

/**
 * In-memory data source for Transaction Query Language.
 */
public class InMemoryTransactionQuerySource<T extends Transaction> implements TransactionQueryDataSource<T> {
    private final List<T> transactions;

    public InMemoryTransactionQuerySource(List<T> transactions) {
        this.transactions = transactions;
    }

    @Override
    public List<T> load() {
        return transactions;
    }
}

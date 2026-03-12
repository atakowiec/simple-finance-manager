package pl.pollub.backend.transaction.query;

import pl.pollub.backend.transaction.model.Transaction;

import java.util.List;

/**
 * Implementor interface for Bridge pattern - query data source.
 */
public interface TransactionQueryDataSource<T extends Transaction> {
    List<T> load();
}

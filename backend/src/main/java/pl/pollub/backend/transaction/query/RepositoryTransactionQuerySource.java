package pl.pollub.backend.transaction.query;

import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.model.Transaction;
import pl.pollub.backend.transaction.repository.TransactionRepository;

import java.util.List;

/**
 * Repository-backed data source for Transaction Query Language.
 */
public class RepositoryTransactionQuerySource<T extends Transaction> implements TransactionQueryDataSource<T> {
    private final TransactionRepository<T> repository;
    private final Group group;

    public RepositoryTransactionQuerySource(TransactionRepository<T> repository, Group group) {
        this.repository = repository;
        this.group = group;
    }

    @Override
    public List<T> load() {
        return repository.findAllByGroup(group);
    }
}

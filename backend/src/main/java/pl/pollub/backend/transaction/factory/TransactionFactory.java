package pl.pollub.backend.transaction.factory;

import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.model.Transaction;

public interface TransactionFactory<T extends Transaction> {
    T create(TransactionCreateDto dto, TransactionFactoryContext context);
}

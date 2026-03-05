package pl.pollub.backend.transaction.factory;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.model.Transaction;

public interface TransactionFactory<T extends Transaction> {
    T create(TransactionCreateDto dto, User user, TransactionCategory category, Group group);
}

package pl.pollub.backend.transaction.factory;

import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.model.Expense;

@Component
public class ExpenseFactory implements TransactionFactory<Expense> {
    @Override
    public Expense create(TransactionCreateDto dto, User user, TransactionCategory category, Group group) {
        Expense expense = new Expense();
        expense.setName(dto.getName());
        expense.setAmount(dto.getAmount());
        expense.setCategory(category);
        expense.setUser(user);
        expense.setDate(dto.getDate());
        expense.setGroup(group);
        return expense;
    }
}

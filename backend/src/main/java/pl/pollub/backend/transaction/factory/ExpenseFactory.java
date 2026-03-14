package pl.pollub.backend.transaction.factory;

import org.springframework.stereotype.Component;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.model.Expense;

@Component
public class ExpenseFactory implements TransactionFactory<Expense> {
    @Override
    public Expense create(TransactionCreateDto dto, TransactionFactoryContext context) {
        Expense expense = new Expense();
        expense.setName(dto.getName());
        expense.setCategory(context.category());
        expense.setUser(context.user());
        expense.setDate(dto.getDate());
        expense.setGroup(context.group());
        return expense;
    }
}

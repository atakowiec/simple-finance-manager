package pl.pollub.backend.transaction.factory;

import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.model.Income;

@Component
public class IncomeFactory implements TransactionFactory<Income> {
    @Override
    public Income create(TransactionCreateDto dto, User user, TransactionCategory category, Group group) {
        Income income = new Income();
        income.setName(dto.getName());
        income.setAmount(dto.getAmount());
        income.setCategory(category);
        income.setUser(user);
        income.setDate(dto.getDate());
        income.setGroup(group);
        return income;
    }
}

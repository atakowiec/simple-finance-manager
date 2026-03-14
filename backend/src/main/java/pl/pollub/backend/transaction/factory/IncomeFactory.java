package pl.pollub.backend.transaction.factory;

import org.springframework.stereotype.Component;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.model.Income;

@Component
public class IncomeFactory implements TransactionFactory<Income> {
    @Override
    public Income create(TransactionCreateDto dto, TransactionFactoryContext context) {
        Income income = new Income();
        income.setName(dto.getName());
        income.setCategory(context.category());
        income.setUser(context.user());
        income.setDate(dto.getDate());
        income.setGroup(context.group());
        return income;
    }
}

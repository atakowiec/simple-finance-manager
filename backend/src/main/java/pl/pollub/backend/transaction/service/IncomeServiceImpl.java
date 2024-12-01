package pl.pollub.backend.transaction.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.repository.IncomeRepository;
import pl.pollub.backend.transaction.repository.TransactionRepository;
import pl.pollub.backend.transaction.service.interfaces.IncomeService;

/**
 * Service for managing incomes. It provides methods for adding, updating and deleting incomes.
 */
@Service
@RequiredArgsConstructor
@Getter
public class IncomeServiceImpl implements IncomeService {
    private final IncomeRepository incomeRepository;
    private final GroupService groupService;
    private final CategoryService categoryService;

    @Override
    public TransactionRepository<Income> getTransactionRepository() {
        return incomeRepository;
    }

    @Override
    public Income createTransaction(TransactionCreateDto createDto, User user) {
        TransactionCategory category = getCategoryService().getCategoryByIdOrThrow(createDto.getCategoryId());

        Group group = getGroupService().getGroupByIdOrThrow(createDto.getGroupId());
        getGroupService().checkMembershipOrThrow(user, group);

        Income income = new Income();
        income.setName(createDto.getName());
        income.setAmount(createDto.getAmount());
        income.setCategory(category);
        income.setUser(user);
        income.setDate(createDto.getDate());
        income.setGroup(group);

        return save(income);
    }
}


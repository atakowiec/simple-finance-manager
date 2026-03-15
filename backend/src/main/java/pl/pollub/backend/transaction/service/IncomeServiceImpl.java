package pl.pollub.backend.transaction.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityMediator;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.transaction.amount.AmountExpressionInterpreter;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.dto.TransactionUpdateDto;
import pl.pollub.backend.transaction.factory.IncomeFactory;
import pl.pollub.backend.transaction.factory.TransactionFactory;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.repository.IncomeRepository;
import pl.pollub.backend.transaction.repository.TransactionRepository;
import pl.pollub.backend.transaction.service.interfaces.IncomeService;

/**
 * Service for managing incomes. It provides methods for adding, updating and deleting incomes.
 * Uses the {@link ActivityMediator} to emit activity events instead of writing logs directly.
 */
@Service
@RequiredArgsConstructor
@Getter
public class IncomeServiceImpl implements IncomeService {
    private final IncomeRepository incomeRepository;
    private final GroupService groupService;
    private final CategoryService categoryService;
    private final AmountExpressionInterpreter amountExpressionInterpreter;
    private final IncomeFactory incomeFactory;
    private final ActivityMediator activityMediator;

    @Override
    public TransactionRepository<Income> getTransactionRepository() {
        return incomeRepository;
    }

    @Override
    public TransactionFactory<Income> getTransactionFactory() {
        return incomeFactory;
    }

    @Override
    public Income createTransaction(TransactionCreateDto createDto, User user) {
        Income income = IncomeService.super.createTransaction(createDto, user);

        activityMediator.notify(
                ActivityEventType.INCOME_CREATED,
                ActivityEventData.builder()
                        .user(user)
                        .group(income.getGroup())
                        .transaction(income)
                        .resourceName(income.getName())
                        .amount(income.getAmount())
                        .build()
        );

        return income;
    }

    @Override
    public Income updateTransaction(Long id, TransactionUpdateDto updateDto, User user) {
        Income income = IncomeService.super.updateTransaction(id, updateDto, user);

        activityMediator.notify(
                ActivityEventType.INCOME_UPDATED,
                ActivityEventData.builder()
                        .user(user)
                        .group(income.getGroup())
                        .transaction(income)
                        .resourceName(income.getName())
                        .amount(income.getAmount())
                        .build()
        );

        return income;
    }

    @Override
    public void deleteTransaction(Long id, User user) {
        Income income = getTransactionByIdAndUserOrThrow(id, user);
        getGroupService().checkMembershipOrThrow(user, income.getGroup());

        activityMediator.notify(
                ActivityEventType.INCOME_DELETED,
                ActivityEventData.builder()
                        .user(user)
                        .group(income.getGroup())
                        .resourceName(income.getName())
                        .amount(income.getAmount())
                        .build()
        );

        getTransactionRepository().deleteById(id);
    }
}
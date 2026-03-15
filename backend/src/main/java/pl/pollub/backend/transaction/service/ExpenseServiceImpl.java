package pl.pollub.backend.transaction.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityMediator;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.amount.AmountExpressionInterpreter;
import pl.pollub.backend.transaction.observer.ExpenseLimitEvent;
import pl.pollub.backend.transaction.observer.ExpenseLimitSubject;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.dto.TransactionUpdateDto;
import pl.pollub.backend.transaction.factory.ExpenseFactory;
import pl.pollub.backend.transaction.factory.TransactionFactory;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.TransactionRepository;
import pl.pollub.backend.transaction.service.interfaces.ExpenseService;

import java.time.LocalDate;

/**
 * Service for managing expenses. It provides methods for adding, updating and deleting expenses.
 * Uses the {@link ActivityMediator} to emit activity events instead of writing logs directly.
 */
@Service
@RequiredArgsConstructor
@Getter
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final CategoryService categoryService;
    private final AmountExpressionInterpreter amountExpressionInterpreter;
    private final ExpenseLimitSubject expenseLimitSubject;
    private final ExpenseFactory expenseFactory;
    private final ActivityMediator activityMediator;

    @Override
    public TransactionRepository<Expense> getTransactionRepository() {
        return expenseRepository;
    }

    @Override
    public TransactionFactory<Expense> getTransactionFactory() {
        return expenseFactory;
    }

    @Override
    @Transactional
    public Expense createTransaction(TransactionCreateDto createDto, User user) {
        Expense expense = ExpenseService.super.createTransaction(createDto, user);

        activityMediator.notify(
                ActivityEventType.EXPENSE_CREATED,
                ActivityEventData.builder()
                        .user(user)
                        .group(expense.getGroup())
                        .transaction(expense)
                        .resourceName(expense.getName())
                        .amount(expense.getAmount())
                        .build()
        );

        Group group = getGroupService().getGroupByIdOrThrow(createDto.getGroupId());
        if (createDto.getDate().isAfter(LocalDate.now().withDayOfMonth(1))) {
            this.trySendLimitWarningMail(user, group);
        }

        return expense;
    }

    @Override
    public Expense updateTransaction(Long id, TransactionUpdateDto updateDto, User user) {
        Expense expense = ExpenseService.super.updateTransaction(id, updateDto, user);

        activityMediator.notify(
                ActivityEventType.EXPENSE_UPDATED,
                ActivityEventData.builder()
                        .user(user)
                        .group(expense.getGroup())
                        .transaction(expense)
                        .resourceName(expense.getName())
                        .amount(expense.getAmount())
                        .build()
        );

        return expense;
    }

    @Override
    public void deleteTransaction(Long id, User user) {
        Expense expense = getTransactionByIdAndUserOrThrow(id, user);
        getGroupService().checkMembershipOrThrow(user, expense.getGroup());

        activityMediator.notify(
                ActivityEventType.EXPENSE_DELETED,
                ActivityEventData.builder()
                        .user(user)
                        .group(expense.getGroup())
                        .resourceName(expense.getName())
                        .amount(expense.getAmount())
                        .build()
        );

        getTransactionRepository().deleteById(id);
    }

    @Override
    public void trySendLimitWarningMail(User user, Group group) {
        expenseLimitSubject.notifyObservers(new ExpenseLimitEvent(user, group));
    }
}

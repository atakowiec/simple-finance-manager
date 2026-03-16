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
import pl.pollub.backend.transaction.observer.ExpenseLimitLifecycleService;
import pl.pollub.backend.transaction.observer.ExpenseLimitTriggerSource;
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
    private final ExpenseLimitLifecycleService expenseLimitLifecycleService;
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
                ActivityEventData.newBuilder()
                        .user(user)
                        .group(expense.getGroup())
                        .transaction(expense)
                        .resourceName(expense.getName())
                        .amount(expense.getAmount())
                        .build()
        );

        if (isInCurrentMonth(createDto.getDate())) {
            expenseLimitLifecycleService.evaluateAndNotify(user, expense.getGroup(), ExpenseLimitTriggerSource.EXPENSE_CREATED);
        }

        return expense;
    }

    @Override
    public Expense updateTransaction(Long id, TransactionUpdateDto updateDto, User user) {
        Expense existingExpense = getTransactionByIdAndUserOrThrow(id, user);
        LocalDate originalDate = existingExpense.getDate();
        Expense expense = ExpenseService.super.updateTransaction(id, updateDto, user);

        activityMediator.notify(
                ActivityEventType.EXPENSE_UPDATED,
                ActivityEventData.newBuilder()
                        .user(user)
                        .group(expense.getGroup())
                        .transaction(expense)
                        .resourceName(expense.getName())
                        .amount(expense.getAmount())
                        .build()
        );

        if (isInCurrentMonth(originalDate) || isInCurrentMonth(expense.getDate())) {
            expenseLimitLifecycleService.evaluateAndNotify(user, expense.getGroup(), ExpenseLimitTriggerSource.EXPENSE_UPDATED);
        }

        return expense;
    }

    @Override
    public void deleteTransaction(Long id, User user) {
        Expense expense = getTransactionByIdAndUserOrThrow(id, user);
        getGroupService().checkMembershipOrThrow(user, expense.getGroup());
        LocalDate expenseDate = expense.getDate();
        Group group = expense.getGroup();

        activityMediator.notify(
                ActivityEventType.EXPENSE_DELETED,
                ActivityEventData.newBuilder()
                        .user(user)
                        .group(group)
                        .resourceName(expense.getName())
                        .amount(expense.getAmount())
                        .build()
        );

        getTransactionRepository().deleteById(id);

        if (isInCurrentMonth(expenseDate)) {
            expenseLimitLifecycleService.evaluateAndNotify(user, group, ExpenseLimitTriggerSource.EXPENSE_DELETED);
        }
    }

    @Override
    public void trySendLimitWarningMail(User user, Group group) {
        expenseLimitLifecycleService.evaluateAndNotify(user, group, ExpenseLimitTriggerSource.EXPENSE_CREATED);
    }

    private boolean isInCurrentMonth(LocalDate date) {
        if (date == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        return date.getYear() == now.getYear() && date.getMonth() == now.getMonth();
    }
}

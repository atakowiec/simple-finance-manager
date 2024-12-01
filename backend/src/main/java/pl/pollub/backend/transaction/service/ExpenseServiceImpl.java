package pl.pollub.backend.transaction.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.notifications.MailService;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.TransactionRepository;
import pl.pollub.backend.transaction.service.interfaces.ExpenseService;

import java.time.LocalDate;

/**
 * Service for managing expenses. It provides methods for adding, updating and deleting expenses.
 */
@Service
@RequiredArgsConstructor
@Getter
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final CategoryService categoryService;
    private final MailService mailService;

    @Override
    public TransactionRepository<Expense> getTransactionRepository() {
        return expenseRepository;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    @Override
    public Expense createTransaction(TransactionCreateDto createDto, User user) {
        TransactionCategory category = getCategoryService().getCategoryByIdOrThrow(createDto.getCategoryId());

        Group group = getGroupService().getGroupByIdOrThrow(createDto.getGroupId());
        getGroupService().checkMembershipOrThrow(user, group);

        Expense expense = new Expense();
        expense.setName(createDto.getName());
        expense.setAmount(createDto.getAmount());
        expense.setCategory(category);
        expense.setUser(user);
        expense.setDate(createDto.getDate());
        expense.setGroup(group);

        if (createDto.getDate().isAfter(LocalDate.now().withDayOfMonth(1)))
            mailService.trySendLimitWarningMail(user, group);

        return save(expense);
    }
}

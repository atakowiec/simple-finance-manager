package pl.pollub.backend.transaction.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.mail.MailService;
import pl.pollub.backend.mail.interfaces.Mail;
import pl.pollub.backend.mail.mails.CloseToLimitMail;
import pl.pollub.backend.mail.mails.LimitExceededMail;
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
    @Transactional
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

        expense = save(expense);

        if (createDto.getDate().isAfter(LocalDate.now().withDayOfMonth(1)))
            this.trySendLimitWarningMail(user, group);

        return expense;
    }

    @Override
    public void trySendLimitWarningMail(User user, Group group) {
        if (group.getExpenseLimit() <= 0)
            return;

        LocalDate now = LocalDate.now();
        LocalDate startOfTheMonth = now.withDayOfMonth(1);
        Double totalExpenses = expenseRepository.getTotalByGroupAndMinDate(group, startOfTheMonth);

        if (totalExpenses == null) {
            totalExpenses = 0.0;
        }

        double remainingPart = 1 - (totalExpenses / group.getExpenseLimit());

        if (remainingPart > 0.1) {
            return;
        }

        Mail<?> mail;

        if (remainingPart < 0) {
            mail = LimitExceededMail.createFor(user, group, totalExpenses);
        } else {
            mail = CloseToLimitMail.createFor(user, group, totalExpenses);
        }

        try {
            this.mailService.sendMail(mail);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd podczas wysyłania e-maila");
        }
    }
}

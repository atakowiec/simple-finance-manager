package pl.pollub.backend.terrible;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.backend.auth.AuthService;
import pl.pollub.backend.auth.dto.RegisterDto;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.terrible.dto.UserOnboardingDto;
import pl.pollub.backend.terrible.dto.UserOnboardingResultDto;
import pl.pollub.backend.transaction.dto.TransactionCreateDto;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.service.interfaces.ExpenseService;

/**
 * Facade that orchestrates the onboarding flow:
 * create user, create group, create first expense.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserOnboardingFacade {

    private static final double MAX_EXPENSE_LIMIT = 1_000_000.0;

    private final AuthService authService;
    private final GroupService groupService;
    private final ExpenseService expenseService;

    @Transactional
    public UserOnboardingResultDto createUserGroupAndExpense(UserOnboardingDto createDto) {
        log.info("Starting onboarding flow for username: {}", createDto.getUsername());

        validateExpenseLimit(createDto.getExpenseLimit());
        validateRegistration(createDto);

        User user = createUser(createDto);

        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setName(createDto.getGroupName());
        Group group = groupService.createGroup(user, groupCreateDto);

        if (createDto.getExpenseLimit() > 0) {
            groupService.changeExpenseLimit(user, createDto.getExpenseLimit(), group.getId());
        }

        Expense expense = createExpense(user, group, createDto);

        log.info("Onboarding flow completed for username: {}", createDto.getUsername());
        return UserOnboardingResultDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .groupId(group.getId())
                .groupName(group.getName())
                .expenseLimit(group.getExpenseLimit())
                .expenseId(expense.getId())
                .expenseName(expense.getName())
                .amount(expense.getAmount())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .build();
    }

    private void validateRegistration(UserOnboardingDto createDto) {
        if (authService.isUsernameTaken(createDto.getUsername())) {
            throw new HttpException(HttpStatus.CONFLICT, "username");
        }
        if (authService.isEmailTaken(createDto.getEmail())) {
            throw new HttpException(HttpStatus.CONFLICT, "email");
        }
    }

    private User createUser(UserOnboardingDto createDto) {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername(createDto.getUsername());
        registerDto.setPassword(createDto.getPassword());
        registerDto.setEmail(createDto.getEmail());

        authService.createUser(registerDto);

        return authService.loadUserByUsername(createDto.getUsername());
    }

    private Expense createExpense(User user, Group group, UserOnboardingDto createDto) {
        TransactionCreateDto transactionCreateDto = new TransactionCreateDto();
        transactionCreateDto.setName(createDto.getExpenseName());
        transactionCreateDto.setAmount(String.valueOf(createDto.getAmount()));
        transactionCreateDto.setCategoryId(createDto.getCategoryId());
        transactionCreateDto.setGroupId(group.getId());

        return expenseService.createTransaction(transactionCreateDto, user);
    }

    private void validateExpenseLimit(double expenseLimit) {
        if (expenseLimit < 0) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Limit wydatkow nie moze byc ujemny");
        }

        if (expenseLimit > MAX_EXPENSE_LIMIT) {
            throw new HttpException(HttpStatus.BAD_REQUEST,
                    "Limit wydatkow nie moze przekraczac " + MAX_EXPENSE_LIMIT);
        }
    }
}

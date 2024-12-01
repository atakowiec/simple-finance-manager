package pl.pollub.backend.expenses;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryRepository;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.GroupServiceImpl;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.dto.TransactionUpdateDto;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.service.ExpenseServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private GroupServiceImpl groupService;

    @Mock
    private CategoryService categoryService; // mockito needs to know what to inject

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private User user;
    private final long groupId = 1L;
    private List<Expense> expectedExpenses;

    private final long notExistingGroupId = 10L;

    private Expense notExistingExpense;
    private Expense existingExpense;

    private Group group;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        group = new Group();
        group.setId(groupId);
        group.setUsers(List.of(user));

        existingExpense = new Expense();
        existingExpense.setId(1L);
        existingExpense.setGroup(group);
        existingExpense.setUser(user);

        notExistingExpense = new Expense();
        notExistingExpense.setId(2L);
        notExistingExpense.setGroup(group);
        notExistingExpense.setUser(user);

        expectedExpenses = List.of(existingExpense);

        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(new TransactionCategory()));

        Mockito.when(expenseRepository.findAllByGroup(Mockito.any())).thenReturn(expectedExpenses);

        Mockito.when(expenseRepository.findByIdAndUser(2L, user)).thenReturn(Optional.empty());
        Mockito.when(expenseRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existingExpense));

        Mockito.when(groupService.getGroupByIdOrThrow(groupId)).thenReturn(group);
        Mockito.when(groupService.getGroupByIdOrThrow(notExistingGroupId)).thenThrow(new HttpException(404, "Not found"));

        Mockito.doThrow(new HttpException(403, "Forbidden")).when(groupService).checkMembershipOrThrow(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(groupService).checkMembershipOrThrow(user, group);
    }

    @Test
    void getAllExpensesForGroup_EverythingIsOkay_ReturnExpectedExpenses() {
        List<Expense> expenses = expenseService.getAllTransactionsForGroup(user, groupId);

        Assertions.assertNotNull(expenses);
        Assertions.assertEquals(expectedExpenses, expenses);
    }

    @Test
    void getAllExpensesForGroup_UserNotInGroup_ThrowHttp403Exception() {
        User userNotInGroup = new User();
        userNotInGroup.setId(2L);

        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> expenseService.getAllTransactionsForGroup(userNotInGroup, groupId));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());
    }

    @Test
    void getAllExpensesForGroup_GroupNotFound_ThrowHttp404Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> expenseService.getAllTransactionsForGroup(user, notExistingGroupId));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());
    }

    @Test
    void saveExpenses_EverythingCorrect_SavesExpenses() {
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setGroup(new Group());

        Mockito.when(expenseRepository.save(Mockito.any())).thenReturn(expense);

        Expense savedExpense = expenseService.save(expense);

        Assertions.assertNotNull(savedExpense);
        Assertions.assertEquals(expense, savedExpense);
        Mockito.verify(expenseRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void updateExpense_EverythingCorrect_updatesExpese() {
        TransactionUpdateDto updatedExpense = new TransactionUpdateDto();
        updatedExpense.setDate(LocalDate.parse("2021-01-01"));
        updatedExpense.setName("New name");
        updatedExpense.setAmount(100.0);
        updatedExpense.setCategoryId(1L);

        Mockito.when(expenseRepository.save(Mockito.any())).thenReturn(existingExpense);

        Expense updated = expenseService.updateTransaction(1L, updatedExpense, user);

        Assertions.assertNotNull(updated);
        Assertions.assertEquals(updatedExpense.getName(), updated.getName());
        Mockito.verify(expenseRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void updateExpense_ExpenseDoesNotExist_ThrowHttp404Exception() {
        TransactionUpdateDto updatedExpense = new TransactionUpdateDto();
        updatedExpense.setName("New name");

        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> expenseService.updateTransaction(notExistingExpense.getId(), updatedExpense, user));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());
    }

    @Test
    void deleteExpense_EverythingCorrect_DeletesExpense() {
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setGroup(group);

        Mockito.when(expenseRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expense));

        expenseService.deleteTransaction(1L, user);

        Mockito.verify(expenseRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteExpense_ExpanseDoesNotExist_ThrowHttp404Exception() {
        Mockito.when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> expenseService.deleteTransaction(notExistingExpense.getId(), user));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());
    }
}
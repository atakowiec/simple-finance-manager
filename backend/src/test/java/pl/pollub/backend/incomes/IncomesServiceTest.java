package pl.pollub.backend.incomes;

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
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.incomes.dto.IncomeUpdateDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IncomesServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private GroupService groupService;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private IncomeService incomeService;

    private User user;
    private final long groupId = 1L;
    private List<Income> expectedIncomes;

    private final long notExistingGroupId = 10L;

    private Income notExistingIncome;
    private Income existingIncome;
    private Group group;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        group = new Group();
        group.setId(groupId);
        group.setUsers(List.of(user));

        existingIncome = new Income();
        existingIncome.setId(1L);
        existingIncome.setGroup(group);
        existingIncome.setUser(user);

        notExistingIncome = new Income();
        notExistingIncome.setId(2L);
        notExistingIncome.setGroup(group);
        notExistingIncome.setUser(user);

        expectedIncomes = List.of(existingIncome);

        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(new TransactionCategory()));

        Mockito.when(incomeRepository.findAllByGroup(Mockito.any())).thenReturn(expectedIncomes);

        Mockito.when(incomeRepository.findByIdAndUser(2L, user)).thenReturn(Optional.empty());
        Mockito.when(incomeRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existingIncome));

        Mockito.when(groupService.getGroupByIdOrThrow(groupId)).thenReturn(group);
        Mockito.when(groupService.getGroupByIdOrThrow(notExistingGroupId)).thenThrow(new HttpException(404, "Not found"));

        Mockito.doThrow(new HttpException(403, "Forbidden")).when(groupService).checkMembershipOrThrow(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(groupService).checkMembershipOrThrow(user, group);
    }

    @Test
    void getAllIncomesForGroup_EverythingIsOkay_ReturnExpectedIncomes() {
        List<Income> incomes = incomeService.getAllIncomesForGroup(user, groupId);

        Assertions.assertNotNull(incomes);
        Assertions.assertEquals(expectedIncomes, incomes);
    }

    @Test
    void getAllIncomesForGroup_UserNotInGroup_ThrowHttp403Exception() {
        User userNotInGroup = new User();
        userNotInGroup.setId(2L);

        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> incomeService.getAllIncomesForGroup(userNotInGroup, groupId));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());
    }

    @Test
    void getAllIncomesForGroup_GroupNotFound_ThrowHttp404Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> incomeService.getAllIncomesForGroup(user, notExistingGroupId));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());
    }

    @Test
    void saveIncomes_EverythingCorrect_SavesIncomes() {
        Income income = new Income();
        income.setUser(user);
        income.setGroup(new Group());

        Mockito.when(incomeRepository.save(Mockito.any())).thenReturn(income);

        Income savedIncome = incomeService.addIncome(income);

        Assertions.assertNotNull(savedIncome);
        Assertions.assertEquals(income, savedIncome);
        Mockito.verify(incomeRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void updateIncome_EverythingCorrect_updatesExpese() {
        IncomeUpdateDto updatedIncome = new IncomeUpdateDto();
        updatedIncome.setDate(LocalDate.parse("2021-01-01"));
        updatedIncome.setName("New name");
        updatedIncome.setAmount(100.0);
        updatedIncome.setCategoryId(1L);

        Mockito.when(incomeRepository.save(Mockito.any())).thenReturn(existingIncome);

        Income updated = incomeService.updateIncome(1L, updatedIncome, user);

        Assertions.assertNotNull(updated);
        Assertions.assertEquals(updatedIncome.getName(), updated.getName());
        Mockito.verify(incomeRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void updateIncome_IncomeDoesNotExist_ThrowHttp404Exception() {
        IncomeUpdateDto updatedIncome = new IncomeUpdateDto();
        updatedIncome.setName("New name");

        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> incomeService.updateIncome(notExistingIncome.getId(), updatedIncome, user));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());
    }

    @Test
    void deleteIncome_EverythingCorrect_DeletesIncome() {
        Income income = new Income();
        income.setUser(user);
        income.setGroup(group);

        Mockito.when(incomeRepository.findById(Mockito.any())).thenReturn(Optional.of(income));

        incomeService.deleteIncome(1L, user);

        Mockito.verify(incomeRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteIncome_ExpanseDoesNotExist_ThrowHttp404Exception() {
        Mockito.when(incomeRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> incomeService.deleteIncome(notExistingIncome.getId(), user));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());
    }
}
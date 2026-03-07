package pl.pollub.backend.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.memento.GroupCaretaker;
import pl.pollub.backend.group.memento.GroupMemento;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.repository.GroupInviteRepository;
import pl.pollub.backend.group.repository.GroupRepository;
import pl.pollub.backend.transaction.dto.TransactionDto;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.IncomeRepository;
import pl.pollub.backend.transaction.observer.ExpenseLimitEvent;
import pl.pollub.backend.transaction.observer.ExpenseLimitSubject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for managing groups. It provides methods for creating, updating and deleting groups.
 */
@Service
@RequiredArgsConstructor
@Getter
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final GroupInviteRepository groupInviteRepository;
    private final CategoryService categoryService;
    private final GroupCaretaker groupCaretaker;
    private final ExpenseLimitSubject expenseLimitSubject;

    @Override
    public Group getGroupByIdOrThrow(long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono grupy o podanym identyfikatorze: " + groupId));
    }

    @Override
    public void checkMembershipOrThrow(User user, Group group) {
        if (group.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie masz dostępu do tej grupy");
        }
    }

    @Override
    public List<Group> getAllGroupsForUser(User user) {
        return groupRepository.findByUsers_Id(user.getId());
    }

    @Override
    public Group createGroup(User user, GroupCreateDto groupCreateDto) {
        Group group = new Group();
        group.setName(groupCreateDto.getName());
        group.setOwner(user);
        group.setColor(groupCreateDto.getColor());
        group.setCreatedAt(LocalDate.now());
        group.setUsers(List.of(user));

        groupRepository.save(group);
        return group;
    }

    @Override
    public Group changeColor(User user, String color, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        // Save current state before making changes (Memento pattern)
        saveGroupState(group);
        group.setColor(color);
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group changeName(User user, String newName, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        // Save current state before making changes (Memento pattern)
        saveGroupState(group);
        group.setName(newName);
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group changeExpenseLimit(User user, Double expenseLimit, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        // Save current state before making changes (Memento pattern)
        saveGroupState(group);
        group.setExpenseLimit(expenseLimit);
        groupRepository.save(group);
        expenseLimitSubject.notifyObservers(new ExpenseLimitEvent(user, group));
        return group;
    }

    @Override
    public Group deleteMember(User user, Long groupId, Long memberId) {
        Group group = getGroupByIdOrThrow(groupId);

        if (!Objects.equals(group.getOwner().getId(), user.getId()))
            throw new HttpException(HttpStatus.FORBIDDEN, "Musisz być właścicielem grupy aby to zrobić!");

        if (Objects.equals(group.getOwner().getId(), memberId))
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie możesz usunąć właściciela grupy!");

        boolean anyRemoved = group.getUsers().removeIf(member -> Objects.equals(member.getId(), memberId));
        if (!anyRemoved)
            throw new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika o podanym identyfikatorze: " + memberId);

        groupRepository.save(group);

        return group;
    }

    @Override
    public void importTransactions(User user, Long groupId, ImportExportDto importExportDto) {
        Group group = getGroupByIdOrThrow(groupId);
        LocalDate startOfTheMonth = LocalDate.now().withDayOfMonth(1);
        boolean hasCurrentMonthImportedExpense = false;

        Map<Long, TransactionCategory> categories = categoryService.getAllCategories().stream()
                .collect(Collectors.toMap(TransactionCategory::getId, v -> v));

        for (TransactionDto expense : importExportDto.getExpenses()) {
            TransactionCategory category = categories.get(expense.getCategory().getId());

            if (category == null)
                throw new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + expense.getCategory().getId());

            Expense newExpense = new Expense();
            newExpense.setName(expense.getName());
            newExpense.setAmount(expense.getAmount());
            newExpense.setCategory(category);
            newExpense.setDate(expense.getDate());
            newExpense.setGroup(group);
            newExpense.setUser(user);

            if (expense.getDate() != null && !expense.getDate().isBefore(startOfTheMonth)) {
                hasCurrentMonthImportedExpense = true;
            }

            expenseRepository.save(newExpense);
        }

        for (TransactionDto income : importExportDto.getIncomes()) {
            TransactionCategory category = categories.get(income.getCategory().getId());

            if (category == null)
                throw new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + income.getCategory().getId());

            Income newIncome = new Income();
            newIncome.setName(income.getName());
            newIncome.setAmount(income.getAmount());
            newIncome.setCategory(category);
            newIncome.setDate(income.getDate());
            newIncome.setGroup(group);
            newIncome.setUser(user);

            incomeRepository.save(newIncome);
        }

        if (hasCurrentMonthImportedExpense) {
            expenseLimitSubject.notifyObservers(new ExpenseLimitEvent(user, group));
        }
    }

    @Override
    public ImportExportDto exportTransactions(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);

        List<Expense> expenses = expenseRepository.findAllByGroup(group);
        List<Income> incomes = incomeRepository.findAllByGroup(group);

        ImportExportDto exportDto = new ImportExportDto();
        exportDto.setCreatedAt(LocalDateTime.now());
        exportDto.setExportedBy(user.getUsername());
        exportDto.setExpenses(expenses.stream().map(TransactionDto::new).collect(Collectors.toList()));
        exportDto.setIncomes(incomes.stream().map(TransactionDto::new).collect(Collectors.toList()));

        return exportDto;
    }

    @Override
    @Transactional
    public void removeGroup(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);

        if (!Objects.equals(group.getOwner().getId(), user.getId()))
            throw new HttpException(HttpStatus.FORBIDDEN, "Musisz być właścicielem grupy aby to zrobić!");

        groupInviteRepository.deleteAllByGroup(group);
        expenseRepository.deleteAllByGroup(group);
        incomeRepository.deleteAllByGroup(group);
        groupRepository.delete(group);
    }

    @Override
    public void leaveGroup(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);

        if (Objects.equals(group.getOwner().getId(), user.getId()))
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie możesz opuścić grupy, której jesteś właścicielem!");

        group.getUsers().removeIf(user::equals);

        groupRepository.save(group);
    }

    @Override
    public void save(Group group) {
        groupRepository.save(group);
    }

    @Override
    public Group undoGroupChange(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        GroupMemento memento = groupCaretaker.getLastMemento(groupId);

        if (memento == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Brak historii zmian do cofnięcia");
        }

        // Restore the state from memento
        group.setName(memento.getName());
        group.setColor(memento.getColor());
        group.setExpenseLimit(memento.getExpenseLimit());

        groupRepository.save(group);
        return group;
    }

    @Override
    public boolean canUndo(Long groupId) {
        return groupCaretaker.hasHistory(groupId);
    }

    /**
     * Helper method to save the current state of a group before making changes.
     * Part of the Memento pattern implementation.
     *
     * @param group the group whose state should be saved
     */
    private void saveGroupState(Group group) {
        GroupMemento memento = GroupMemento.create(
                group.getName(),
                group.getColor(),
                group.getExpenseLimit()
        );
        groupCaretaker.saveMemento(group.getId(), memento);
    }
}

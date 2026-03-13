package pl.pollub.backend.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.config.constants.ExpenseLimitConstants;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.membership.UserMembership;
import pl.pollub.backend.group.memento.GroupCaretaker;
import pl.pollub.backend.group.memento.GroupMemento;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.repository.GroupInviteRepository;
import pl.pollub.backend.group.repository.GroupRepository;
import pl.pollub.backend.transaction.dto.TransactionDto;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.observer.ExpenseLimitEvent;
import pl.pollub.backend.transaction.observer.ExpenseLimitSubject;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.IncomeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    private final GroupCaretaker groupCaretaker;
    private final ExpenseLimitSubject expenseLimitSubject;
    private final GroupImportFacade groupImportFacade;
    // Prototype instance for creating new groups with default settings
    private final Group groupPrototype = createGroupPrototype();

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
        // Use Prototype pattern to create a new group based on a template
        Group group = groupPrototype.clone();
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
        groupImportFacade.importTransactions(user, group, importExportDto);
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

        // Use State Pattern: Let the state object validate the membership transition
        UserMembership membership = UserMembership.create(user, group, null);
        membership.leaveGroup();

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

    private Group createGroupPrototype() {
        Group prototype = new Group();
        prototype.setExpenseLimit(ExpenseLimitConstants.NO_EXPENSE_LIMIT);
        return prototype;
    }
}

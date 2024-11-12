package pl.pollub.backend.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryRepository;
import pl.pollub.backend.categories.model.TransactionCategory;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.expenses.Expense;
import pl.pollub.backend.expenses.ExpenseRepository;
import pl.pollub.backend.expenses.dto.ExpenseDto;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.incomes.Income;
import pl.pollub.backend.incomes.IncomeRepository;
import pl.pollub.backend.incomes.dto.IncomeDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Service for managing groups. It provides methods for creating, updating and deleting groups.
 */
@Service
@RequiredArgsConstructor
public class GroupService {
    @Getter
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final GroupInviteRepository groupInviteRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Returns the group with the specified id or throws an exception if the group does not exist.
     *
     * @param groupId id of the group
     * @return group with the specified id
     */
    public Group getGroupByIdOrThrow(long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono grupy o podanym identyfikatorze: " + groupId));
    }

    /**
     * Checks if the specified user is a member of the specified group. If not, throws an exception.
     *
     * @param user  user to check
     * @param group group to check
     * @throws HttpException if the user is not a member of the group
     */
    public void checkMembershipOrThrow(User user, Group group) {
        if (group.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie masz dostępu do tej grupy");
        }
    }

    /**
     * Returns all groups for the specified user.
     *
     * @param user user whose groups will be returned
     * @return list of groups
     */
    public List<Group> getAllGroupsForUser(User user) {
        return groupRepository.findByUsers_Id(user.getId());
    }

    /**
     * Saves the specified group to the database
     */
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

    /**
     * Changes the color of the specified group.
     *
     * @param user    user who wants to change the color
     * @param color   new color
     * @param groupId id of the group
     * @return updated group
     */
    public Group changeColor(User user, String color, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        group.setColor(color);

        groupRepository.save(group);

        return group;
    }

    /**
     * Changes the name of the specified group.
     *
     * @param user    user who wants to change the name
     * @param newName new name
     * @param groupId id of the group
     * @return updated group
     */
    public Group changeName(User user, String newName, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        group.setName(newName);

        groupRepository.save(group);

        return group;
    }

    /**
     * Changes the expense limit of the specified group.
     *
     * @param user         user who wants to change the expense limit
     * @param expenseLimit new expense limit
     * @param groupId      id of the group
     * @return updated group
     */
    public Group changeExpenseLimit(User user, Double expenseLimit, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        group.setExpenseLimit(expenseLimit);

        groupRepository.save(group);

        return group;
    }

    /**
     * Deletes the specified member from the specified group.
     *
     * @param user     user who wants to delete the member
     * @param groupId  id of the group
     * @param memberId id of the member to delete
     * @return updated group
     */
    public Group deleteMember(User user, Long groupId, Long memberId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

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

    /**
     * Invites the specified user to the specified group.
     *
     * @param user            user who wants to invite the member
     * @param groupId         id of the group
     * @param importExportDto data transfer object with the list of expenses and incomes to import
     */
    public void importTransactions(User user, Long groupId, ImportExportDto importExportDto) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        for (ExpenseDto expense : importExportDto.getExpenses()) {
            TransactionCategory category = categoryRepository.findById(expense.getCategory().getId())
                    .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + expense.getCategory().getId()));

            Expense newExpense = new Expense();
            newExpense.setName(expense.getName());
            newExpense.setAmount(expense.getAmount());
            newExpense.setCategory(category);
            newExpense.setDate(expense.getDate());
            newExpense.setGroup(group);
            newExpense.setUser(user);

            expenseRepository.save(newExpense);
        }

        for (IncomeDto income : importExportDto.getIncomes()) {
            TransactionCategory category = categoryRepository.findById(income.getCategory().getId())
                    .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + income.getCategory().getId()));

            Income newIncome = new Income();
            newIncome.setName(income.getName());
            newIncome.setAmount(income.getAmount());
            newIncome.setCategory(category);
            newIncome.setDate(income.getDate());
            newIncome.setGroup(group);
            newIncome.setUser(user);

            incomeRepository.save(newIncome);
        }
    }

    /**
     * Removes the specified group and all its data from the database.
     *
     * @param user    user who wants to remove the group
     * @param groupId id of the group
     */
    @Transactional
    public void removeGroup(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        if (!Objects.equals(group.getOwner().getId(), user.getId()))
            throw new HttpException(HttpStatus.FORBIDDEN, "Musisz być właścicielem grupy aby to zrobić!");

        groupInviteRepository.deleteAllByGroup(group);
        expenseRepository.deleteAllByGroup(group);
        incomeRepository.deleteAllByGroup(group);
        groupRepository.delete(group);
    }

    public void leaveGroup(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        if (Objects.equals(group.getOwner().getId(), user.getId()))
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie możesz opuścić grupy, której jesteś właścicielem!");

        group.getUsers().removeIf(user::equals);

        groupRepository.save(group);
    }
}

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
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.repository.GroupInviteRepository;
import pl.pollub.backend.group.repository.GroupRepository;
import pl.pollub.backend.transaction.dto.TransactionDto;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.IncomeRepository;

import java.time.LocalDate;
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
        checkMembershipOrThrow(user, group);

        group.setColor(color);

        groupRepository.save(group);

        return group;
    }

    @Override
    public Group changeName(User user, String newName, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        group.setName(newName);

        groupRepository.save(group);

        return group;
    }

    @Override
    public Group changeExpenseLimit(User user, Double expenseLimit, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        group.setExpenseLimit(expenseLimit);

        groupRepository.save(group);

        return group;
    }

    @Override
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

    @Override
    public void importTransactions(User user, Long groupId, ImportExportDto importExportDto) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

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
    }

    @Override
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

    @Override
    public void leaveGroup(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        checkMembershipOrThrow(user, group);

        if (Objects.equals(group.getOwner().getId(), user.getId()))
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie możesz opuścić grupy, której jesteś właścicielem!");

        group.getUsers().removeIf(user::equals);

        groupRepository.save(group);
    }

    @Override
    public void save(Group group) {
        groupRepository.save(group);
    }
}

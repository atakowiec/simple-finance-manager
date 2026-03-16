package pl.pollub.backend.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityMediator;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.config.constants.ExpenseLimitConstants;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.deletion.GroupDeletionMediator;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.dto.GroupMemberDto;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.membership.UserMembership;
import pl.pollub.backend.group.memento.GroupCaretaker;
import pl.pollub.backend.group.memento.GroupMemento;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.observer.GroupMemberChangeAction;
import pl.pollub.backend.group.observer.GroupMemberChangeEvent;
import pl.pollub.backend.group.observer.GroupMemberChangeSubject;
import pl.pollub.backend.group.repository.GroupRepository;
import pl.pollub.backend.transaction.dto.TransactionDto;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.observer.ExpenseLimitLifecycleService;
import pl.pollub.backend.transaction.observer.ExpenseLimitTriggerSource;
import pl.pollub.backend.transaction.observer.rule.ExpenseLimitRuleInterpreter;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.IncomeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing groups. It provides methods for creating, updating and deleting groups.
 */
@Service
@RequiredArgsConstructor
@Getter
public class GroupServiceImpl implements GroupService {
    private static final int MAX_ICON_SIZE_BYTES = 2 * 1024 * 1024;
    private static final Set<String> SUPPORTED_ICON_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/gif",
            "image/webp"
    );

    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final GroupCaretaker groupCaretaker;
    private final ExpenseLimitLifecycleService expenseLimitLifecycleService;
    private final ExpenseLimitRuleInterpreter expenseLimitRuleInterpreter;
    private final GroupMemberChangeSubject groupMemberChangeSubject;
    private final GroupImportFacade groupImportFacade;
    private final ActivityMediator activityMediator;
    private final GroupDeletionMediator groupDeletionMediator;
    private final Group groupPrototype = createGroupPrototype();

    @Override
    public Group getGroupByIdOrThrow(long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono grupy o podanym identyfikatorze: " + groupId));
    }

    @Override
    public void checkMembershipOrThrow(User user, Group group) {
        if (group.getUsers().stream().noneMatch(member -> member.getId().equals(user.getId()))) {
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie masz dostępu do tej grupy");
        }
    }

    @Override
    public List<Group> getAllGroupsForUser(User user) {
        return groupRepository.findByUsers_Id(user.getId());
    }

    @Override
    public Group createGroup(User user, GroupCreateDto groupCreateDto) {
        Group group = groupPrototype.clone();
        group.setName(groupCreateDto.getName());
        group.setOwner(user);
        group.setColor(groupCreateDto.getColor());
        group.setCreatedAt(LocalDate.now());
        group.setUsers(new ArrayList<>(List.of(user)));
        group.setIcon(copyIcon(groupCreateDto.getIcon()));
        group.setIconContentType(resolveIconContentType(groupCreateDto.getIcon(), groupCreateDto.getIconContentType()));

        groupRepository.save(group);

        activityMediator.notify(
                ActivityEventType.GROUP_CREATED,
                ActivityEventData.builder()
                        .user(user)
                        .group(group)
                        .resourceName(group.getName())
                        .build()
        );

        return group;
    }

    @Override
    public List<GroupMemberDto> getGroupOwners(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        GroupOwnersIterator iterator = new GroupOwnersIterator(group.getUsers());
        List<GroupMemberDto> result = new ArrayList<>();

        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
    }

    @Override
    public Group changeColor(User user, String color, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        saveGroupState(group);
        group.setColor(color);
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group changeName(User user, String newName, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        saveGroupState(group);
        group.setName(newName);
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group changeExpenseLimit(User user, Double expenseLimit, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        saveGroupState(group);
        group.setExpenseLimit(expenseLimit);
        groupRepository.save(group);
        expenseLimitLifecycleService.evaluateAndNotify(user, group, ExpenseLimitTriggerSource.GROUP_LIMIT_CHANGED);
        return group;
    }

    @Override
    public Group changeExpenseLimitRule(User user, String expenseLimitRule, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        String normalizedRule = normalizeRule(expenseLimitRule);

        if (normalizedRule != null) {
            expenseLimitRuleInterpreter.parse(normalizedRule);
        }

        saveGroupState(group);
        group.setExpenseLimitRule(normalizedRule);
        groupRepository.save(group);
        expenseLimitLifecycleService.evaluateAndNotify(user, group, ExpenseLimitTriggerSource.GROUP_RULE_CHANGED);
        return group;
    }

    @Override
    public Group changeIcon(User user, byte[] icon, String contentType, Long groupId) {
        if (icon == null || icon.length == 0) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Prześlij plik ikony grupy");
        }

        Group group = getGroupByIdOrThrow(groupId);
        saveGroupState(group);
        group.setIcon(copyIcon(icon));
        group.setIconContentType(resolveIconContentType(icon, contentType));
        groupRepository.save(group);
        return group;
    }

    @Override
    public void deleteIcon(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        if (!group.hasIcon()) {
            return;
        }

        saveGroupState(group);
        group.setIcon(null);
        group.setIconContentType(null);
        groupRepository.save(group);
    }

    @Override
    public Group deleteMember(User user, Long groupId, Long memberId) {
        Group group = getGroupByIdOrThrow(groupId);

        if (!Objects.equals(group.getOwner().getId(), user.getId())) {
            throw new HttpException(HttpStatus.FORBIDDEN, "Musisz być właścicielem grupy aby to zrobić!");
        }

        if (Objects.equals(group.getOwner().getId(), memberId)) {
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie możesz usunąć właściciela grupy!");
        }

        User removedMember = group.getUsers().stream()
                .filter(member -> Objects.equals(member.getId(), memberId))
                .findFirst()
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND,
                        "Nie znaleziono użytkownika o podanym identyfikatorze: " + memberId));

        boolean anyRemoved = group.getUsers().removeIf(member -> Objects.equals(member.getId(), memberId));
        if (!anyRemoved) {
            throw new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika o podanym identyfikatorze: " + memberId);
        }

        groupRepository.save(group);

        activityMediator.notify(
                ActivityEventType.MEMBER_REMOVED_FROM_GROUP,
                ActivityEventData.builder()
                        .user(removedMember)
                        .group(group)
                        .additionalInfo("removed by " + user.getUsername())
                        .build()
        );

        groupMemberChangeSubject.notifyObservers(new GroupMemberChangeEvent(
                user,
                removedMember,
                group,
                GroupMemberChangeAction.REMOVED,
                createMemberChangeRecipients(group, removedMember)
        ));

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
    public void removeGroup(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);

        if (!Objects.equals(group.getOwner().getId(), user.getId())) {
            throw new HttpException(HttpStatus.FORBIDDEN, "Musisz być właścicielem grupy aby to zrobić!");
        }

        groupDeletionMediator.deleteGroup(user, group);
    }

    @Override
    public void leaveGroup(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);

        if (Objects.equals(group.getOwner().getId(), user.getId())) {
            throw new HttpException(HttpStatus.FORBIDDEN, "Nie możesz opuścić grupy, której jesteś właścicielem!");
        }

        UserMembership membership = UserMembership.create(user, group, null);
        membership.leaveGroup();

        groupRepository.save(group);

        activityMediator.notify(
                ActivityEventType.MEMBER_LEFT_GROUP,
                ActivityEventData.builder()
                        .user(user)
                        .group(group)
                        .build()
        );

        groupMemberChangeSubject.notifyObservers(new GroupMemberChangeEvent(
                user,
                user,
                group,
                GroupMemberChangeAction.LEFT,
                createMemberChangeRecipients(group, user)
        ));
    }

    @Override
    public void save(Group group) {
        groupRepository.save(group);
    }

    @Override
    public Group undoGroupChange(User user, Long groupId) {
        Group group = getGroupByIdOrThrow(groupId);
        GroupMemento memento = groupCaretaker.getLastMemento(groupId);
        if (memento == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Brak historii zmian do cofnięcia");
        }

        try {
            group.setName(memento.getName());
            group.setColor(memento.getColor());
            group.setIcon(memento.getIcon());
            group.setIconContentType(memento.getIconContentType());
            group.setExpenseLimit(memento.getExpenseLimit());
            group.setExpenseLimitRule(memento.getExpenseLimitRule());
        } finally {
            memento.release();
        }

        groupRepository.save(group);
        expenseLimitLifecycleService.evaluateAndNotify(user, group, ExpenseLimitTriggerSource.GROUP_LIMIT_UNDONE);
        return group;
    }

    @Override
    public boolean canUndo(Long groupId) {
        return groupCaretaker.hasHistory(groupId);
    }

    private void saveGroupState(Group group) {
        GroupMemento memento = GroupMemento.create(
                group.getName(),
                group.getColor(),
                group.getIcon(),
                group.getIconContentType(),
                group.getExpenseLimit(),
                group.getExpenseLimitRule()
        );
        groupCaretaker.saveMemento(group.getId(), memento);
    }

    private String normalizeRule(String expenseLimitRule) {
        if (expenseLimitRule == null) {
            return null;
        }

        String trimmed = expenseLimitRule.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<User> createMemberChangeRecipients(Group group, User additionalRecipient) {
        List<User> recipients = new ArrayList<>(group.getUsers());
        if (additionalRecipient != null) {
            recipients.add(additionalRecipient);
        }
        return recipients;
    }

    private Group createGroupPrototype() {
        Group prototype = new Group();
        prototype.setExpenseLimit(ExpenseLimitConstants.NO_EXPENSE_LIMIT);
        prototype.setColor("#ffffff");
        return prototype;
    }

    private byte[] copyIcon(byte[] icon) {
        if (icon == null || icon.length == 0) {
            return null;
        }

        if (icon.length > MAX_ICON_SIZE_BYTES) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Ikona grupy nie może być większa niż 2 MB");
        }

        return Arrays.copyOf(icon, icon.length);
    }

    private String resolveIconContentType(byte[] icon, String contentType) {
        if (icon == null || icon.length == 0) {
            return null;
        }

        String normalizedContentType = normalizeContentType(contentType);
        if (normalizedContentType != null) {
            if (!SUPPORTED_ICON_CONTENT_TYPES.contains(normalizedContentType)) {
                throw new HttpException(HttpStatus.BAD_REQUEST, "Nieobsługiwany format ikony grupy");
            }
            return normalizedContentType;
        }

        String detectedContentType = detectImageContentType(icon);
        if (detectedContentType == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Nieobsługiwany format ikony grupy");
        }

        return detectedContentType;
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return null;
        }

        String normalized = contentType.trim().toLowerCase();
        if ("image/jpg".equals(normalized)) {
            return "image/jpeg";
        }
        return normalized;
    }

    private String detectImageContentType(byte[] icon) {
        if (startsWith(icon, (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)) {
            return "image/png";
        }
        if (startsWith(icon, (byte) 0xFF, (byte) 0xD8, (byte) 0xFF)) {
            return "image/jpeg";
        }
        if (startsWith(icon, 'G', 'I', 'F', '8')) {
            return "image/gif";
        }
        if (startsWith(icon, 'R', 'I', 'F', 'F') && icon.length > 11
                && icon[8] == 'W' && icon[9] == 'E' && icon[10] == 'B' && icon[11] == 'P') {
            return "image/webp";
        }
        return null;
    }

    private boolean startsWith(byte[] data, int... signature) {
        if (data == null || data.length < signature.length) {
            return false;
        }

        for (int i = 0; i < signature.length; i++) {
            if ((data[i] & 0xFF) != (signature[i] & 0xFF)) {
                return false;
            }
        }
        return true;
    }
}

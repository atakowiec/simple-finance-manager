package pl.pollub.backend.group;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pl.pollub.backend.activity.ActivityMediator;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.CategoryService;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.deletion.GroupDeletionMediator;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.memento.GroupCaretaker;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.observer.GroupMemberChangeAction;
import pl.pollub.backend.group.observer.GroupMemberChangeEvent;
import pl.pollub.backend.group.observer.GroupMemberChangeSubject;
import pl.pollub.backend.group.repository.GroupInviteRepository;
import pl.pollub.backend.group.repository.GroupRepository;
import pl.pollub.backend.transaction.model.Expense;
import pl.pollub.backend.transaction.model.Income;
import pl.pollub.backend.transaction.observer.ExpenseLimitLifecycleService;
import pl.pollub.backend.transaction.observer.ExpenseLimitSubject;
import pl.pollub.backend.transaction.observer.ExpenseLimitTriggerSource;
import pl.pollub.backend.transaction.repository.ExpenseRepository;
import pl.pollub.backend.transaction.repository.IncomeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupInviteRepository groupInviteRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private CategoryService categoryService; // mockito needs to know what to inject
    @Mock
    private GroupImportFacade importFacade;
    @Mock
    private GroupCaretaker groupCaretaker;
    @Mock
    private ExpenseLimitSubject expenseLimitSubject;
    @Mock
    private ExpenseLimitLifecycleService expenseLimitLifecycleService;
    @Mock
    private GroupMemberChangeSubject groupMemberChangeSubject;

    @Mock
    private ActivityMediator activityMediator;
    @Mock
    private GroupDeletionMediator groupDeletionMediator;

    @InjectMocks
    private GroupServiceImpl groupServiceImpl;

    private GroupServiceProxy groupService;

    private User loggedUser;
    private Group loggedUserGroup;

    private User otherUser;
    private Group otherUserGroup;

    private User ownerUser;

    @BeforeEach
    void setUp() {
        ownerUser = new User();
        ownerUser.setId(3L);

        loggedUser = new User();
        loggedUser.setId(1L);

        loggedUserGroup = new Group();
        loggedUserGroup.setId(1L);
        loggedUserGroup.setUsers(new ArrayList<>(List.of(ownerUser, loggedUser)));
        loggedUserGroup.setOwner(ownerUser);

        otherUser = new User();
        otherUser.setId(2L);

        otherUserGroup = new Group();
        otherUserGroup.setId(2L);
        otherUserGroup.setUsers(new ArrayList<>(List.of(ownerUser, otherUser)));
        otherUserGroup.setOwner(ownerUser);

        groupService = new GroupServiceProxy(groupServiceImpl);

        Mockito.when(groupRepository.findById(loggedUserGroup.getId())).thenReturn(Optional.of(loggedUserGroup));
        Mockito.when(groupRepository.findById(otherUserGroup.getId())).thenReturn(Optional.of(otherUserGroup));
    }

    private void mockTransactionRepositories() {
        Mockito.when(expenseRepository.save(Mockito.any())).thenReturn(new Expense());
        Mockito.when(incomeRepository.save(Mockito.any())).thenReturn(new Income());
    }

    @Test
    void checkMembershipOrThrow_UserNotInGroup_ThrowsHttp403Exception() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> groupService.checkMembershipOrThrow(loggedUser, otherUserGroup));
        Assertions.assertEquals(403, ((HttpException) exception).getHttpStatus().value());

        exception = Assertions.assertThrows(Exception.class, () -> groupService.checkMembershipOrThrow(otherUser, loggedUserGroup));
        Assertions.assertEquals(403, ((HttpException) exception).getHttpStatus().value());
    }

    @Test
    void checkMembershipOrThrow_UserInGroup_DoesNotThrowException() {
        Assertions.assertDoesNotThrow(() -> groupService.checkMembershipOrThrow(loggedUser, loggedUserGroup));
        Assertions.assertDoesNotThrow(() -> groupService.checkMembershipOrThrow(otherUser, otherUserGroup));
    }

    @Test
    void getGroupByIdOrThrow_GroupExists_ReturnsGroup() {
        Group group = groupService.getGroupByIdOrThrow(loggedUserGroup.getId());
        Assertions.assertEquals(loggedUserGroup, group);

        group = groupService.getGroupByIdOrThrow(otherUserGroup.getId());
        Assertions.assertEquals(otherUserGroup, group);

        Mockito.verify(groupRepository, Mockito.times(2)).findById(Mockito.any());
    }

    @Test
    void getGroupByIdOrThrow_GroupDoesNotExist_ThrowsHttp404Exception() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> groupService.getGroupByIdOrThrow(4L));

        Assertions.assertEquals(404, ((HttpException) exception).getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    void getAllGroupsForUser_UserInGroups_ReturnsGroups() {
        Mockito.when(groupRepository.findByUsers_Id(loggedUser.getId())).thenReturn(List.of(loggedUserGroup));
        Mockito.when(groupRepository.findByUsers_Id(otherUser.getId())).thenReturn(List.of(otherUserGroup));

        List<Group> groups = groupService.getAllGroupsForUser(loggedUser);
        Assertions.assertEquals(List.of(loggedUserGroup), groups);

        groups = groupService.getAllGroupsForUser(otherUser);
        Assertions.assertEquals(List.of(otherUserGroup), groups);

        Mockito.verify(groupRepository, Mockito.times(2)).findByUsers_Id(Mockito.anyLong());
    }

    @Test
    void getAllGroupsForUser_UserNotInGroups_ReturnsEmptyList() {
        Mockito.when(groupRepository.findByUsers_Id(loggedUser.getId())).thenReturn(List.of());
        Mockito.when(groupRepository.findByUsers_Id(otherUser.getId())).thenReturn(List.of());

        List<Group> groups = groupService.getAllGroupsForUser(loggedUser);
        Assertions.assertEquals(List.of(), groups);

        groups = groupService.getAllGroupsForUser(otherUser);
        Assertions.assertEquals(List.of(), groups);

        Mockito.verify(groupRepository, Mockito.times(2)).findByUsers_Id(Mockito.anyLong());
    }

    @Test
    void createGroup_UserCreatesGroup_ReturnsGroup() {
        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setName("Test group");
        groupCreateDto.setColor("#FFFFFF");

        Group group = groupService.createGroup(loggedUser, groupCreateDto);

        Assertions.assertEquals(groupCreateDto.getName(), group.getName());
        Assertions.assertEquals(groupCreateDto.getColor(), group.getColor());
        Assertions.assertEquals(loggedUser, group.getOwner());
        Assertions.assertEquals(List.of(loggedUser), group.getUsers());

        Mockito.verify(groupRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void getGroupOwner_UserNotInGroup_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class,
                () -> groupService.getGroupOwners(otherUser, loggedUserGroup.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());
    }

    @Test
    void changeColor_UserInGroup_ChangesColor() {
        String newColor = "#000000";

        Group group = groupService.changeColor(loggedUser, newColor, loggedUserGroup.getId());

        Assertions.assertEquals(newColor, group.getColor());
        Mockito.verify(groupRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void changeColor_UserNotInGroup_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.changeColor(otherUser, "#000000", loggedUserGroup.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void changeColor_GroupDoesNotExist_ThrowsHttp404Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.changeColor(loggedUser, "#000000", 4L));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void changeName_UserInGroup_ChangesName() {
        String newName = "New name";

        Group group = groupService.changeName(loggedUser, newName, loggedUserGroup.getId());

        Assertions.assertEquals(newName, group.getName());
        Mockito.verify(groupRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void changeName_UserNotInGroup_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.changeName(otherUser, "New name", loggedUserGroup.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void changeName_GroupDoesNotExist_ThrowsHttp404Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.changeName(loggedUser, "New name", 4L));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void changeExpenseLimit_UserInGroup_ChangesExpenseLimit() {
        Double newExpenseLimit = 100.0;

        Group group = groupService.changeExpenseLimit(loggedUser, newExpenseLimit, loggedUserGroup.getId());

        Assertions.assertEquals(newExpenseLimit, group.getExpenseLimit());
        Mockito.verify(groupRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(expenseLimitLifecycleService, Mockito.times(1))
                .evaluateAndNotify(loggedUser, loggedUserGroup, ExpenseLimitTriggerSource.GROUP_LIMIT_CHANGED);
    }

    @Test
    void changeExpenseLimit_UserNotInGroup_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.changeExpenseLimit(otherUser, 100.0, loggedUserGroup.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void changeExpenseLimit_GroupDoesNotExist_ThrowsHttp404Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.changeExpenseLimit(loggedUser, 100.0, 4L));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void deleteMember_UserInGroup_DeletesMember() {
        Group group = groupService.deleteMember(ownerUser, loggedUserGroup.getId(), loggedUser.getId());

        Assertions.assertFalse(group.getUsers().contains(loggedUser));
        Mockito.verify(groupRepository, Mockito.times(1)).save(Mockito.any());
        ArgumentCaptor<GroupMemberChangeEvent> captor = ArgumentCaptor.forClass(GroupMemberChangeEvent.class);
        Mockito.verify(groupMemberChangeSubject, Mockito.times(1)).notifyObservers(captor.capture());
        Assertions.assertEquals(GroupMemberChangeAction.REMOVED, captor.getValue().action());
        Assertions.assertEquals(loggedUser, captor.getValue().member());
        Assertions.assertTrue(captor.getValue().recipients().contains(ownerUser));
        Assertions.assertTrue(captor.getValue().recipients().contains(loggedUser));
    }

    @Test
    void deleteMember_UserNotInGroup_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.deleteMember(otherUser, loggedUserGroup.getId(), loggedUser.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void deleteMember_GroupDoesNotExist_ThrowsHttp404Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.deleteMember(loggedUser, 4L, otherUser.getId()));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void deleteMember_UserIsNotOwner_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.deleteMember(loggedUser, loggedUserGroup.getId(), loggedUser.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void deleteMember_TargetUserNotInGroup_ThrowsHttp404Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.deleteMember(ownerUser, loggedUserGroup.getId(), 4L));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void deleteMember_TargetUserIsOwner_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.deleteMember(loggedUser, loggedUserGroup.getId(), loggedUserGroup.getOwner().getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void importTransactions_EmptyPayload_DoesNothing() {
        ImportExportDto importExportDto = new ImportExportDto();
        importExportDto.setExpenses(new ArrayList<>());
        importExportDto.setIncomes(new ArrayList<>());

        groupService.importTransactions(loggedUser, loggedUserGroup.getId(), importExportDto);

        Mockito.verifyNoInteractions(expenseRepository);
        Mockito.verifyNoInteractions(incomeRepository);
    }

    @Test
    void importTransactions_GroupDoesNotExist_ThrowsHttp404Exception() {
        ImportExportDto importExportDto = new ImportExportDto();
        importExportDto.setExpenses(new ArrayList<>());
        importExportDto.setIncomes(new ArrayList<>());

        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.importTransactions(loggedUser, 4L, importExportDto));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());

        Mockito.verifyNoInteractions(expenseRepository);
    }

    @Test
    void importTransactions_UserNotInGroup_ThrowsHttp403Exception() {
        ImportExportDto importExportDto = new ImportExportDto();
        importExportDto.setExpenses(new ArrayList<>());
        importExportDto.setIncomes(new ArrayList<>());

        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.importTransactions(otherUser, loggedUserGroup.getId(), importExportDto));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verifyNoInteractions(expenseRepository);
    }

    @Test
    void deleteGroup_EverythingOk_DeletesGroup() {
        groupService.removeGroup(ownerUser, loggedUserGroup.getId());
    }

    @Test
    void deleteGroup_UserNotInGroup_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.removeGroup(otherUser, loggedUserGroup.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).deleteById(Mockito.any());
    }

    @Test
    void deleteGroup_GroupDoesNotExist_ThrowsHttp404Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.removeGroup(ownerUser, 4L));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).deleteById(Mockito.any());
    }

    @Test
    void deleteGroup_UserIsNotOwner_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.removeGroup(loggedUser, loggedUserGroup.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).deleteById(Mockito.any());
    }

    @Test
    void deleteGroup_GroupHasExpensesAndIncomes_DelegatesDeletionWorkflowToMediator() {
        groupService.removeGroup(ownerUser, loggedUserGroup.getId());

        Mockito.verify(groupDeletionMediator, Mockito.times(1)).deleteGroup(ownerUser, loggedUserGroup);
    }

    @Test
    void leaveGroup_EverythingOk_LeavesGroup() {
        groupService.leaveGroup(loggedUser, loggedUserGroup.getId());

        Mockito.verify(groupRepository, Mockito.times(1)).save(loggedUserGroup);
        ArgumentCaptor<GroupMemberChangeEvent> captor = ArgumentCaptor.forClass(GroupMemberChangeEvent.class);
        Mockito.verify(groupMemberChangeSubject, Mockito.times(1)).notifyObservers(captor.capture());
        Assertions.assertEquals(GroupMemberChangeAction.LEFT, captor.getValue().action());
        Assertions.assertEquals(loggedUser, captor.getValue().member());
        Assertions.assertTrue(captor.getValue().recipients().contains(ownerUser));
        Assertions.assertTrue(captor.getValue().recipients().contains(loggedUser));
    }

    @Test
    void leaveGroup_UserNotInGroup_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.leaveGroup(otherUser, loggedUserGroup.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(loggedUserGroup);
    }

    @Test
    void leaveGroup_GroupDoesNotExist_ThrowsHttp404Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.leaveGroup(loggedUser, 4L));

        Assertions.assertEquals(404, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(loggedUserGroup);
    }

    @Test
    void leaveGroup_UserIsOwner_ThrowsHttp403Exception() {
        HttpException httpException = Assertions.assertThrows(HttpException.class, () -> groupService.leaveGroup(ownerUser, loggedUserGroup.getId()));

        Assertions.assertEquals(403, httpException.getHttpStatus().value());

        Mockito.verify(groupRepository, Mockito.times(0)).save(loggedUserGroup);
    }
}


package pl.pollub.backend.group;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.dto.GroupMemberDto;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;

import java.util.List;

/**
 * Proxy for GroupService that handles access control.
 */
@Service
@Primary
@RequiredArgsConstructor
public class GroupServiceProxy implements GroupService {

    private final GroupServiceImpl groupService;

    @Override
    public Group getGroupByIdOrThrow(long groupId) {
        return groupService.getGroupByIdOrThrow(groupId);
    }

    @Override
    public void checkMembershipOrThrow(User user, Group group) {
        groupService.checkMembershipOrThrow(user, group);
    }

    @Override
    public List<Group> getAllGroupsForUser(User user) {
        return groupService.getAllGroupsForUser(user);
    }

    @Override
    public Group createGroup(User user, GroupCreateDto groupCreateDto) {
        return groupService.createGroup(user, groupCreateDto);
    }

    @Override
    public List<GroupMemberDto>  getGroupOwners(User user, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);
        return groupService.getGroupOwners(user, groupId);
    }

    @Override
    public Group changeColor(User user, String color, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);
        return groupService.changeColor(user, color, groupId);
    }

    @Override
    public Group changeName(User user, String newName, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);
        return groupService.changeName(user, newName, groupId);
    }

    @Override
    public Group changeExpenseLimit(User user, Double expenseLimit, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);
        return groupService.changeExpenseLimit(user, expenseLimit, groupId);
    }

    @Override
    public Group deleteMember(User user, Long groupId, Long memberId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);
        return groupService.deleteMember(user, groupId, memberId);
    }

    @Override
    public void importTransactions(User user, Long groupId, ImportExportDto importExportDto) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);
        groupService.importTransactions(user, groupId, importExportDto);
    }

    @Override
    public ImportExportDto exportTransactions(User user, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);
        return groupService.exportTransactions(user, groupId);
    }

    @Override
    public void removeGroup(User user, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);
        groupService.removeGroup(user, groupId);
    }

    @Override
    public void leaveGroup(User user, Long groupId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);
        groupService.leaveGroup(user, groupId);
    }

    @Override
    public void save(Group group) {
        groupService.save(group);
    }

    @Override
    public Group undoGroupChange(User user, Long groupId) {
        return groupService.undoGroupChange(user, groupId);
    }

    @Override
    public boolean canUndo(Long groupId) {
        return groupService.canUndo(groupId);
    }
}

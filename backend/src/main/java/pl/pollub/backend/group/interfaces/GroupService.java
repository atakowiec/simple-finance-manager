package pl.pollub.backend.group.interfaces;

import org.springframework.transaction.annotation.Transactional;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.model.Group;

import java.util.List;

/**
 * Service for managing groups. It provides methods for creating, updating and deleting groups.
 */
public interface GroupService {
    /**
     * Returns the group with the specified id or throws an exception if the group does not exist.
     *
     * @param groupId id of the group
     * @return group with the specified id
     */
    Group getGroupByIdOrThrow(long groupId);

    /**
     * Checks if the specified user is a member of the specified group. If not, throws an exception.
     *
     * @param user  user to check
     * @param group group to check
     * @throws HttpException if the user is not a member of the group
     */
    void checkMembershipOrThrow(User user, Group group);
    /**
     * Returns all groups for the specified user.
     *
     * @param user user whose groups will be returned
     * @return list of groups
     */
    List<Group> getAllGroupsForUser(User user);

    /**
     * Saves the specified group to the database
     */
    Group createGroup(User user, GroupCreateDto groupCreateDto) ;

    /**
     * Changes the color of the specified group.
     *
     * @param user    user who wants to change the color
     * @param color   new color
     * @param groupId id of the group
     * @return updated group
     */
    Group changeColor(User user, String color, Long groupId);

    /**
     * Changes the name of the specified group.
     *
     * @param user    user who wants to change the name
     * @param newName new name
     * @param groupId id of the group
     * @return updated group
     */
    Group changeName(User user, String newName, Long groupId);

    /**
     * Changes the expense limit of the specified group.
     *
     * @param user         user who wants to change the expense limit
     * @param expenseLimit new expense limit
     * @param groupId      id of the group
     * @return updated group
     */
    Group changeExpenseLimit(User user, Double expenseLimit, Long groupId);

    /**
     * Deletes the specified member from the specified group.
     *
     * @param user     user who wants to delete the member
     * @param groupId  id of the group
     * @param memberId id of the member to delete
     * @return updated group
     */
    Group deleteMember(User user, Long groupId, Long memberId) ;

    /**
     * Invites the specified user to the specified group.
     *
     * @param user            user who wants to invite the member
     * @param groupId         id of the group
     * @param importExportDto data transfer object with the list of expenses and incomes to import
     */
    void importTransactions(User user, Long groupId, ImportExportDto importExportDto);

    /**
     * Removes the specified group and all its data from the database.
     *
     * @param user    user who wants to remove the group
     * @param groupId id of the group
     */
    @Transactional
    void removeGroup(User user, Long groupId);

    void leaveGroup(User user, Long groupId);

    void save(Group group);
}

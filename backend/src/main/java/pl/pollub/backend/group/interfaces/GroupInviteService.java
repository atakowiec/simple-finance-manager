package pl.pollub.backend.group.interfaces;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.dto.InviteTargetDto;
import pl.pollub.backend.group.enums.MembershipStatus;
import pl.pollub.backend.group.model.GroupInvite;

import java.util.List;

/**
 * Service for group invites management.
 */
public interface GroupInviteService {

    /**
     * Invite user to group.
     *
     * @param user    inviter
     * @param groupId group id that user is invited to
     * @param userId  invitee id
     * @return new membership status
     */
    MembershipStatus inviteUser(User user, Long groupId, Long userId);

    /**
     * Delete invitation.
     *
     * @param user    user that wants to delete invitation
     * @param groupId group id that invitation is related to
     * @param userId  user id that invitation is related to
     * @return new membership status
     */
    MembershipStatus deleteInvitation(User user, Long groupId, Long userId);

    /**
     * Deny invitation.
     *
     * @param user     user that wants to deny invitation
     * @param inviteId invitation id
     * @return new membership status
     */
    MembershipStatus denyInvitation(User user, Long inviteId);

    /**
     * Accept invitation.
     * @param user user that wants to accept invitation
     * @param inviteId invitation id
     * @return new membership status
     */
    MembershipStatus acceptInvitation(User user, Long inviteId);

    /**
     * Find users to invite to group with all needed information about their membership status.
     * @param user user that wants to invite
     * @param groupId group id that user wants to invite to
     * @param query query to search for users
     * @return list of users with their membership status
     */
    List<InviteTargetDto> findInviteTargets(User user, Long groupId, String query);

    /**
     * Get all active invitations for user.
     * @param user user that wants to get own invitations
     * @return list of active invitations
     */
    List<GroupInvite> getActiveInvitations(User user);
}

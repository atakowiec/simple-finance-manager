package pl.pollub.backend.group;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.auth.user.UserService;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.dto.InviteTargetDto;
import pl.pollub.backend.group.enums.MembershipStatus;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for group invites management.
 */
@Service
@RequiredArgsConstructor
public class GroupInviteService {
    private final GroupInviteRepository inviteRepository;
    private final GroupService groupService;
    private final UserService userService;

    /**
     * Invite user to group.
     *
     * @param user    inviter
     * @param groupId group id that user is invited to
     * @param userId  invitee id
     * @return new membership status
     */
    public MembershipStatus inviteUser(User user, Long groupId, Long userId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        User target = userService.getUserById(userId);

        if (target == null)
            throw new HttpException(404, "Nie znaleziono podanego użytkownika");

        if (group.getUsers().stream().anyMatch(other -> other.getId().equals(target.getId())))
            throw new HttpException(409, "Ten użytkownik jest juz w tej grupie");

        GroupInvite invite = inviteRepository.findGroupInviteByInviteeAndGroup(target, group);

        if (invite != null)
            throw new HttpException(409, "Ten użytkownik ma już zaproszenie do grupy");

        invite = new GroupInvite();
        invite.setGroup(group);
        invite.setInviter(user);
        invite.setInvitee(target);
        invite.setCreatedAt(LocalDateTime.now());
        inviteRepository.save(invite);

        return MembershipStatus.INVITED;
    }

    /**
     * Delete invitation.
     *
     * @param user    user that wants to delete invitation
     * @param groupId group id that invitation is related to
     * @param userId  user id that invitation is related to
     * @return new membership status
     */
    public MembershipStatus deleteInvitation(User user, Long groupId, Long userId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        User target = userService.getUserById(userId);

        if (target == null)
            throw new HttpException(404, "Nie znaleziono podanego użytkownika");

        GroupInvite invite = inviteRepository.findGroupInviteByInviteeAndGroup(target, group);

        if (invite == null)
            throw new HttpException(404, "Ten użytkownik nie ma zaproszenia do grupy");

        inviteRepository.delete(invite);

        return MembershipStatus.NONE;
    }

    /**
     * Deny invitation.
     *
     * @param user     user that wants to deny invitation
     * @param inviteId invitation id
     * @return new membership status
     */
    public MembershipStatus denyInvitation(User user, Long inviteId) {
        GroupInvite groupInvite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new HttpException(404, "Nie znaleniono zaprosznia"));

        if (!groupInvite.getInvitee().equals(user))
            throw new HttpException(HttpStatus.UNAUTHORIZED, "To zaproszenie nie dotyczy ciebie!");

        inviteRepository.delete(groupInvite);

        return MembershipStatus.NONE;
    }

    /**
     * Accept invitation.
     * @param user user that wants to accept invitation
     * @param inviteId invitation id
     * @return new membership status
     */
    public MembershipStatus acceptInvitation(User user, Long inviteId) {
        GroupInvite groupInvite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new HttpException(404, "Nie znaleniono zaprosznia"));

        if (!groupInvite.getInvitee().equals(user))
            throw new HttpException(HttpStatus.UNAUTHORIZED, "To zaproszenie nie dotyczy ciebie!");

        Group group = groupService.getGroupByIdOrThrow(groupInvite.getGroup().getId());
        if (group.getUsers().stream().anyMatch(otherUser -> otherUser.equals(user)))
            throw new HttpException(409, "Już jesteś w tej grupie!");

        inviteRepository.delete(groupInvite);

        group.getUsers().add(user);

        groupService.getGroupRepository().save(group);

        return MembershipStatus.IN_GROUP;
    }

    /**
     * Find users to invite to group with all needed information about their membership status.
     * @param user user that wants to invite
     * @param groupId group id that user wants to invite to
     * @param query query to search for users
     * @return list of users with their membership status
     */
    public List<InviteTargetDto> findInviteTargets(User user, Long groupId, String query) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        List<InviteTargetDto> result = new ArrayList<>();
        List<Object[]> dbResult = userService.getUserRepository().findUsersByNicknameWithInviteStatus(query, groupId, Pageable.ofSize(10));

        for (Object[] row : dbResult) {
            User inviteeUser = (User) row[0];
            boolean isInvited = (boolean) row[1];

            if (inviteeUser.equals(user))
                continue;

            InviteTargetDto dto = new InviteTargetDto();
            dto.setId(inviteeUser.getId());
            dto.setUsername(inviteeUser.getUsername());

            if (group.getUsers().stream().anyMatch(inviteeUser::equals)) {
                dto.setMembershipStatus(MembershipStatus.IN_GROUP);
            } else {
                dto.setMembershipStatus(isInvited ? MembershipStatus.INVITED : MembershipStatus.NONE);
            }

            result.add(dto);
        }

        return result;
    }

    /**
     * Get all active invitations for user.
     * @param user user that wants to get own invitations
     * @return list of active invitations
     */
    public List<GroupInvite> getActiveInvitations(User user) {
        return inviteRepository.findAllByInvitee(user);
    }
}

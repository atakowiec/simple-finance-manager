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
import pl.pollub.backend.group.interfaces.GroupInviteService;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;
import pl.pollub.backend.group.membership.UserMembership;
import pl.pollub.backend.group.repository.GroupInviteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for group invites management.
 * Uses the State Design Pattern (via UserMembership) to manage state transitions
 * and prevent invalid membership operations.
 */
@Service
@RequiredArgsConstructor
public class GroupInviteServiceImpl implements GroupInviteService {
    private final GroupInviteRepository inviteRepository;
    private final GroupService groupService;
    private final UserService userService;

    @Override
    public MembershipStatus inviteUser(User user, Long groupId, Long userId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        User target = getUserOrThrow(userId);
        GroupInvite existingInvite = inviteRepository.findGroupInviteByInviteeAndGroup(target, group);

        GroupInvite invite = createInviteObject(user, group, target);

        UserMembership membership = UserMembership.create(target, group, existingInvite);
        membership.inviteUser(user, invite);

        inviteRepository.save(invite);

        return membership.getStatus();
    }

    private User getUserOrThrow(Long userId) {
        User target = userService.getUserById(userId);
        if (target == null)
            throw new HttpException(404, "Nie znaleziono podanego użytkownika");
        return target;
    }

    private GroupInvite createInviteObject(User inviter, Group group, User invitee) {
        GroupInvite invite = new GroupInvite();
        invite.setGroup(group);
        invite.setInviter(inviter);
        invite.setInvitee(invitee);
        invite.setCreatedAt(LocalDateTime.now());
        return invite;
    }

    @Override
    public MembershipStatus deleteInvitation(User user, Long groupId, Long userId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        User target = getUserOrThrow(userId);
        GroupInvite invite = getInviteOrThrow(target, group);

        UserMembership membership = UserMembership.create(target, group, invite);
        membership.denyInvitation(invite);

        inviteRepository.delete(invite);

        return membership.getStatus();
    }

    private GroupInvite getInviteOrThrow(User target, Group group) {
        GroupInvite invite = inviteRepository.findGroupInviteByInviteeAndGroup(target, group);
        if (invite == null)
            throw new HttpException(404, "Ten użytkownik nie ma zaproszenia do grupy");
        return invite;
    }

    @Override
    public MembershipStatus denyInvitation(User user, Long inviteId) {
        GroupInvite groupInvite = getInviteByIdOrThrow(inviteId);
        validateInviteOwnership(user, groupInvite);

        Group group = groupService.getGroupByIdOrThrow(groupInvite.getGroup().getId());

        UserMembership membership = UserMembership.create(user, group, groupInvite);
        membership.denyInvitation(groupInvite);

        inviteRepository.delete(groupInvite);

        return membership.getStatus();
    }

    private GroupInvite getInviteByIdOrThrow(Long inviteId) {
        return inviteRepository.findById(inviteId)
                .orElseThrow(() -> new HttpException(404, "Nie znaleziono zaproszenia"));
    }

    private void validateInviteOwnership(User user, GroupInvite groupInvite) {
        if (!groupInvite.getInvitee().equals(user))
            throw new HttpException(HttpStatus.UNAUTHORIZED, "To zaproszenie nie dotyczy ciebie!");
    }

    @Override
    public MembershipStatus acceptInvitation(User user, Long inviteId) {
        GroupInvite groupInvite = getInviteByIdOrThrow(inviteId);
        validateInviteOwnership(user, groupInvite);

        Group group = groupService.getGroupByIdOrThrow(groupInvite.getGroup().getId());

        UserMembership membership = UserMembership.create(user, group, groupInvite);
        membership.acceptInvitation(groupInvite);

        inviteRepository.delete(groupInvite);
        groupService.save(group);

        return membership.getStatus();
    }

    @Override
    public List<InviteTargetDto> findInviteTargets(User user, Long groupId, String query) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        List<Object[]> dbResult = userService.getUserRepository().findUsersByNicknameWithInviteStatus(query, groupId, Pageable.ofSize(10));
        return fetchInviteTargets(user, group, dbResult);
    }

    private List<InviteTargetDto> fetchInviteTargets(User user, Group group, List<Object[]> dbResult) {
        List<InviteTargetDto> result = new ArrayList<>();

        for (Object[] row : dbResult) {
            User inviteeUser = (User) row[0];
            boolean isInvited = (boolean) row[1];

            if (inviteeUser.equals(user))
                continue;

            InviteTargetDto dto = mapUserToDto(inviteeUser, group, isInvited);
            result.add(dto);
        }

        return result;
    }

    private InviteTargetDto mapUserToDto(User inviteeUser, Group group, boolean isInvited) {
        InviteTargetDto dto = new InviteTargetDto();
        dto.setId(inviteeUser.getId());
        dto.setUsername(inviteeUser.getUsername());
        dto.setMembershipStatus(determineMembershipStatus(group, inviteeUser, isInvited));
        return dto;
    }

    private MembershipStatus determineMembershipStatus(Group group, User inviteeUser, boolean isInvited) {
        if (group.getUsers().stream().anyMatch(inviteeUser::equals)) {
            return MembershipStatus.IN_GROUP;
        }
        return isInvited ? MembershipStatus.INVITED : MembershipStatus.NONE;
    }

    @Override
    public List<GroupInvite> getActiveInvitations(User user) {
        return inviteRepository.findAllByInvitee(user);
    }
}

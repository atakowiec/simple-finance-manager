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

        User target = userService.getUserById(userId);

        if (target == null)
            throw new HttpException(404, "Nie znaleziono podanego użytkownika");

        // Check if invitation already exists
        GroupInvite existingInvite = inviteRepository.findGroupInviteByInviteeAndGroup(target, group);

        // Create invitation object
        GroupInvite invite = new GroupInvite();
        invite.setGroup(group);
        invite.setInviter(user);
        invite.setInvitee(target);
        invite.setCreatedAt(LocalDateTime.now());

        // Use State Pattern: Let the state object validate the operation
        UserMembership membership = UserMembership.create(target, group, existingInvite);
        membership.inviteUser(user, invite);

        // Save the invitation
        inviteRepository.save(invite);

        return membership.getStatus();
    }

    @Override
    public MembershipStatus deleteInvitation(User user, Long groupId, Long userId) {
        Group group = groupService.getGroupByIdOrThrow(groupId);
        groupService.checkMembershipOrThrow(user, group);

        User target = userService.getUserById(userId);

        if (target == null)
            throw new HttpException(404, "Nie znaleziono podanego użytkownika");

        GroupInvite invite = inviteRepository.findGroupInviteByInviteeAndGroup(target, group);

        if (invite == null)
            throw new HttpException(404, "Ten użytkownik nie ma zaproszenia do grupy");

        // Use State Pattern: Let the state object validate the operation
        UserMembership membership = UserMembership.create(target, group, invite);
        membership.denyInvitation(invite);

        inviteRepository.delete(invite);

        return membership.getStatus();
    }

    @Override
    public MembershipStatus denyInvitation(User user, Long inviteId) {
        GroupInvite groupInvite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new HttpException(404, "Nie znaleziono zaproszenia"));

        if (!groupInvite.getInvitee().equals(user))
            throw new HttpException(HttpStatus.UNAUTHORIZED, "To zaproszenie nie dotyczy ciebie!");

        Group group = groupService.getGroupByIdOrThrow(groupInvite.getGroup().getId());

        // Use State Pattern: Let the state object validate the operation
        UserMembership membership = UserMembership.create(user, group, groupInvite);
        membership.denyInvitation(groupInvite);

        // Delete invitation
        inviteRepository.delete(groupInvite);

        return membership.getStatus();
    }

    @Override
    public MembershipStatus acceptInvitation(User user, Long inviteId) {
        GroupInvite groupInvite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new HttpException(404, "Nie znaleziono zaproszenia"));

        if (!groupInvite.getInvitee().equals(user))
            throw new HttpException(HttpStatus.UNAUTHORIZED, "To zaproszenie nie dotyczy ciebie!");

        Group group = groupService.getGroupByIdOrThrow(groupInvite.getGroup().getId());

        // Use State Pattern: Let the state object validate the operation
        UserMembership membership = UserMembership.create(user, group, groupInvite);
        membership.acceptInvitation(groupInvite);

        // Delete invitation and save group
        inviteRepository.delete(groupInvite);
        groupService.save(group);

        return membership.getStatus();
    }

    @Override
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

    @Override
    public List<GroupInvite> getActiveInvitations(User user) {
        return inviteRepository.findAllByInvitee(user);
    }
}

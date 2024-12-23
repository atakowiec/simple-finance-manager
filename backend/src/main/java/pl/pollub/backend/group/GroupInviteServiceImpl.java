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
import pl.pollub.backend.group.repository.GroupInviteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for group invites management.
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

        inviteRepository.delete(invite);

        return MembershipStatus.NONE;
    }

    @Override
    public MembershipStatus denyInvitation(User user, Long inviteId) {
        GroupInvite groupInvite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new HttpException(404, "Nie znaleniono zaprosznia"));

        if (!groupInvite.getInvitee().equals(user))
            throw new HttpException(HttpStatus.UNAUTHORIZED, "To zaproszenie nie dotyczy ciebie!");

        inviteRepository.delete(groupInvite);

        return MembershipStatus.NONE;
    }

    @Override
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

        groupService.save(group);

        return MembershipStatus.IN_GROUP;
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

package pl.pollub.backend.group;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.dto.InviteTargetDto;
import pl.pollub.backend.group.enums.MembershipStatus;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupInviteService groupInviteService;

    @GetMapping
    public List<Group> getGroups(@AuthenticationPrincipal User user) {
        return groupService.getAllGroupsForUser(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Group createGroup(@AuthenticationPrincipal User user, @RequestBody @Valid GroupCreateDto groupCreateDto) {
        return groupService.createGroup(user, groupCreateDto);
    }

    @PatchMapping("/{groupId}/color")
    public Group changeColor(@AuthenticationPrincipal User user, @RequestBody String color, @PathVariable Long groupId) {
        return groupService.changeColor(user, color, groupId);
    }

    @PatchMapping("/{groupId}/name")
    public Group changeName(@AuthenticationPrincipal User user, @RequestBody String newName, @PathVariable Long groupId) {
        return groupService.changeName(user, newName, groupId);
    }

    @PatchMapping("/{groupId}/expense-limit")
    public Group changeExpenseLimit(@AuthenticationPrincipal User user, @RequestBody Double expenseLimit, @PathVariable Long groupId) {
        return groupService.changeExpenseLimit(user, expenseLimit, groupId);
    }

    @DeleteMapping("/{groupId}/member/{memberId}")
    public Group deleteMember(@AuthenticationPrincipal User user, @PathVariable Long groupId, @PathVariable Long memberId) {
        return groupService.deleteMember(user, groupId, memberId);
    }

    @PostMapping("/{groupId}/invite/{userId}")
    public MembershipStatus inviteUser(@AuthenticationPrincipal User user, @PathVariable Long groupId, @PathVariable Long userId) {
        return groupInviteService.inviteUser(user, groupId, userId);
    }

    @DeleteMapping("/{groupId}/invite/{userId}")
    public MembershipStatus deleteInvitation(@AuthenticationPrincipal User user, @PathVariable Long groupId, @PathVariable Long userId) {
        return groupInviteService.deleteInvitation(user, groupId, userId);
    }

    @DeleteMapping("/invite/{inviteId}/deny")
    public MembershipStatus denyInvitation(@AuthenticationPrincipal User user, @PathVariable Long inviteId) {
        return groupInviteService.denyInvitation(user, inviteId);
    }

    @PostMapping("/invite/{inviteId}/accept")
    public MembershipStatus acceptInvitation(@AuthenticationPrincipal User user, @PathVariable Long inviteId) {
        return groupInviteService.acceptInvitation(user, inviteId);
    }

    @GetMapping("/{groupId}/users/{query}")
    public List<InviteTargetDto> findUsers(@AuthenticationPrincipal User user, @PathVariable Long groupId, @PathVariable String query) {
        return groupInviteService.findInviteTargets(user, groupId, query);
    }

    @GetMapping("/invites")
    public List<GroupInvite> getActiveInvitations(@AuthenticationPrincipal User user) {
        return groupInviteService.getActiveInvitations(user);
    }
}

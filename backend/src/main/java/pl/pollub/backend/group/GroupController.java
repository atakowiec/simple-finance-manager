package pl.pollub.backend.group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.dto.GroupCreateDto;
import pl.pollub.backend.group.dto.ImportExportDto;
import pl.pollub.backend.group.dto.InviteTargetDto;
import pl.pollub.backend.group.enums.MembershipStatus;
import pl.pollub.backend.group.interfaces.GroupInviteService;
import pl.pollub.backend.group.interfaces.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.group.model.GroupInvite;

import java.util.List;

/**
 * Controller for group management.
 */
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Tag(name = "Grupy", description = "Zarządzanie grupami")
public class GroupController {
    private final GroupService groupService;
    private final GroupInviteService groupInviteService;

    @Operation(summary = "Pobierz wszystkie grupy dla zalogoanego użytkownika")
    @ApiResponse(responseCode = "200", description = "Lista grup zalogowanego użytkownika")
    @GetMapping
    public List<Group> getGroups(@AuthenticationPrincipal User user) {
        return groupService.getAllGroupsForUser(user);
    }

    @Operation(summary = "Stwórz nową grupę")
    @ApiResponse(responseCode = "201", description = "Stworzono grupę")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Group createGroup(@AuthenticationPrincipal User user, @RequestBody @Valid GroupCreateDto groupCreateDto) {
        return groupService.createGroup(user, groupCreateDto);
    }

    @Operation(summary = "Zmień kolor grupy")
    @ApiResponse(responseCode = "200", description = "Zmieniono kolor grupy")
    @PatchMapping("/{groupId}/color")
    public Group changeColor(@AuthenticationPrincipal User user, @RequestBody String color, @PathVariable Long groupId) {
        return groupService.changeColor(user, color, groupId);
    }

    @Operation(summary = "Zmień nazwę grupy")
    @ApiResponse(responseCode = "200", description = "Zmieniono nazwę grupy")
    @PatchMapping("/{groupId}/name")
    public Group changeName(@AuthenticationPrincipal User user, @RequestBody String newName, @PathVariable Long groupId) {
        return groupService.changeName(user, newName, groupId);
    }

    @Operation(summary = "Zmień limit wydatków grupy")
    @ApiResponse(responseCode = "200", description = "Zmieniono limit wydatków grupy")
    @PatchMapping("/{groupId}/expense-limit")
    public Group changeExpenseLimit(@AuthenticationPrincipal User user, @RequestBody Double expenseLimit, @PathVariable Long groupId) {
        return groupService.changeExpenseLimit(user, expenseLimit, groupId);
    }

    @Operation(summary = "Usuń członka z grupy")
    @ApiResponse(responseCode = "200", description = "Usunięto członka z grupy")
    @DeleteMapping("/{groupId}/member/{memberId}")
    public Group deleteMember(@AuthenticationPrincipal User user, @PathVariable Long groupId, @PathVariable Long memberId) {
        return groupService.deleteMember(user, groupId, memberId);
    }

    @Operation(summary = "Zaproszenie użytkownika do grupy")
    @ApiResponse(responseCode = "200", description = "Zaproszono użytkownika do grupy")
    @PostMapping("/{groupId}/invite/{userId}")
    public MembershipStatus inviteUser(@AuthenticationPrincipal User user, @PathVariable Long groupId, @PathVariable Long userId) {
        return groupInviteService.inviteUser(user, groupId, userId);
    }

    @Operation(summary = "Usuń zaproszenie")
    @ApiResponse(responseCode = "200", description = "Usunięto zaproszenie")
    @DeleteMapping("/{groupId}/invite/{userId}")
    public MembershipStatus deleteInvitation(@AuthenticationPrincipal User user, @PathVariable Long groupId, @PathVariable Long userId) {
        return groupInviteService.deleteInvitation(user, groupId, userId);
    }

    @Operation(summary = "Odrzuć zaproszenie")
    @ApiResponse(responseCode = "200", description = "Odrzucono zaproszenie")
    @DeleteMapping("/invite/{inviteId}/deny")
    public MembershipStatus denyInvitation(@AuthenticationPrincipal User user, @PathVariable Long inviteId) {
        return groupInviteService.denyInvitation(user, inviteId);
    }

    @Operation(summary = "Akceptuj zaproszenie")
    @ApiResponse(responseCode = "200", description = "Zaakceptowano zaproszenie")
    @PostMapping("/invite/{inviteId}/accept")
    public MembershipStatus acceptInvitation(@AuthenticationPrincipal User user, @PathVariable Long inviteId) {
        return groupInviteService.acceptInvitation(user, inviteId);
    }

    @Operation(summary = "Znajdź użytkowników do zaproszenia do grupy")
    @ApiResponse(responseCode = "200", description = "Lista użytkowników")
    @GetMapping("/{groupId}/users/{query}")
    public List<InviteTargetDto> findUsers(@AuthenticationPrincipal User user, @PathVariable Long groupId, @PathVariable String query) {
        return groupInviteService.findInviteTargets(user, groupId, query);
    }

    @Operation(summary = "Pobierz wszystkie aktywne zaproszenia zalogowanego użytkownika")
    @ApiResponse(responseCode = "200", description = "Lista zaproszeń")
    @GetMapping("/invites")
    public List<GroupInvite> getActiveInvitations(@AuthenticationPrincipal User user) {
        return groupInviteService.getActiveInvitations(user);
    }

    @Operation(summary = "Importuj transakcje do grupy")
    @ApiResponse(responseCode = "200", description = "Zaimportowano transakcje")
    @PostMapping("/{groupId}/import")
    public void handleImport(@AuthenticationPrincipal User user, @PathVariable Long groupId, @RequestBody ImportExportDto importExportDto) {
        groupService.importTransactions(user, groupId, importExportDto);
    }

    @Operation(summary = "Usuń grupę")
    @ApiResponse(responseCode = "200", description = "Usunięto grupę")
    @DeleteMapping("/{groupId}")
    public void removeGroup(@AuthenticationPrincipal User user, @PathVariable Long groupId) {
        groupService.removeGroup(user, groupId);
    }

    @Operation(summary = "Opuść grupę")
    @ApiResponse(responseCode = "200", description = "Opuszczono grupę")
    @PostMapping("/{groupId}/leave")
    public void leaveGroup(@AuthenticationPrincipal User user, @PathVariable Long groupId) {
        groupService.leaveGroup(user, groupId);
    }
}

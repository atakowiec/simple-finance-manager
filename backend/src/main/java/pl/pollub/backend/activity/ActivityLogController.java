package pl.pollub.backend.activity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.interfaces.GroupService;

import java.util.List;

/**
 * REST controller exposing activity log queries.
 * Write operations are performed exclusively through the {@link ActivityMediator}.
 */
@RestController
@RequestMapping("/activity-logs")
@RequiredArgsConstructor
@Tag(name = "Logi aktywności", description = "Historia aktywności użytkowników w grupach")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final GroupService groupService;

    @Operation(summary = "Pobierz logi aktywności dla grupy")
    @ApiResponse(responseCode = "200", description = "Lista logów aktywności dla grupy")
    @GetMapping("/group/{groupId}")
    public List<ActivityLog> getGroupActivityLogs(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User user) {
        // Verify membership before exposing group logs
        groupService.checkMembershipOrThrow(user, groupService.getGroupByIdOrThrow(groupId));
        return activityLogService.getLogsForGroup(groupId);
    }

    @Operation(summary = "Pobierz logi aktywności zalogowanego użytkownika")
    @ApiResponse(responseCode = "200", description = "Lista logów aktywności użytkownika")
    @GetMapping("/user")
    public List<ActivityLog> getUserActivityLogs(@AuthenticationPrincipal User user) {
        return activityLogService.getLogsForUser(user);
    }
}


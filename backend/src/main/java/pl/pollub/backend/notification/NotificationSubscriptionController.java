package pl.pollub.backend.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.notification.dto.NotificationSubscriptionDto;
import pl.pollub.backend.notification.dto.NotificationSubscriptionRequest;

/**
 * Controller for managing notification subscriptions.
 */
@RestController
@RequestMapping("/notifications/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Powiadomienia", description = "Zarządzanie subskrypcjami powiadomień")
public class NotificationSubscriptionController {
    private final NotificationSubscriptionService subscriptionService;

    @Operation(summary = "Zmień subskrypcję powiadomień")
    @ApiResponse(responseCode = "200", description = "Zmieniono subskrypcję")
    @PostMapping
    public NotificationSubscriptionDto updateSubscription(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid NotificationSubscriptionRequest request
    ) {
        return subscriptionService.updateSubscription(user, request.getType(), request.isSubscribe());
    }
}

package pl.pollub.backend.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.pollub.backend.notification.NotificationType;

/**
 * Request for updating a notification subscription.
 */
@Data
public class NotificationSubscriptionRequest {
    @NotNull
    private NotificationType type;

    private boolean subscribe;
}

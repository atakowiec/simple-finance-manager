package pl.pollub.backend.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.pollub.backend.notification.NotificationType;

/**
 * DTO describing subscription status.
 */
@Data
@AllArgsConstructor
public class NotificationSubscriptionDto {
    private NotificationType type;
    private boolean enabled;
}

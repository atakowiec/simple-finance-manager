package pl.pollub.backend.notification.command;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.notification.NotificationSubscriptionRepository;
import pl.pollub.backend.notification.NotificationType;

/**
 * Factory Method for creating notification commands.
 */
public final class NotificationCommandFactory {
    private NotificationCommandFactory() {
    }

    public static NotificationCommand create(
            NotificationSubscriptionRepository repository,
            User user,
            NotificationType type,
            boolean subscribe
    ) {
        return subscribe
                ? new SubscribeNotificationCommand(repository, user, type)
                : new UnsubscribeNotificationCommand(repository, user, type);
    }
}

package pl.pollub.backend.notification.command;

import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.notification.NotificationSubscription;
import pl.pollub.backend.notification.NotificationSubscriptionRepository;
import pl.pollub.backend.notification.NotificationType;

/**
 * Command that disables a notification subscription for a user.
 */
public class UnsubscribeNotificationCommand implements NotificationCommand {
    private final NotificationSubscriptionRepository repository;
    private final User user;
    private final NotificationType type;

    public UnsubscribeNotificationCommand(
            NotificationSubscriptionRepository repository,
            User user,
            NotificationType type
    ) {
        this.repository = repository;
        this.user = user;
        this.type = type;
    }

    @Override
    public void execute() {
        NotificationSubscription subscription = repository
                .findByUserAndType(user, type)
                .orElseGet(() -> {
                    NotificationSubscription created = new NotificationSubscription();
                    created.setUser(user);
                    created.setType(type);
                    return created;
                });
        subscription.setEnabled(false);
        repository.save(subscription);
    }
}

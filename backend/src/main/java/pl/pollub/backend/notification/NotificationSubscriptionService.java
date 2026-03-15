package pl.pollub.backend.notification;

import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.notification.command.NotificationCommand;
import pl.pollub.backend.notification.command.NotificationCommandFactory;
import pl.pollub.backend.notification.command.NotificationCommandInvoker;
import pl.pollub.backend.notification.dto.NotificationSubscriptionDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing user notification subscriptions.
 */
@Service
public class NotificationSubscriptionService {
    private final NotificationSubscriptionRepository repository;
    private final NotificationCommandInvoker commandInvoker = new NotificationCommandInvoker();

    public NotificationSubscriptionService(NotificationSubscriptionRepository repository) {
        this.repository = repository;
    }

    public NotificationSubscriptionDto updateSubscription(User user, NotificationType type, boolean subscribe) {
        NotificationCommand command = NotificationCommandFactory.create(repository, user, type, subscribe);
        commandInvoker.execute(command);
        return new NotificationSubscriptionDto(type, subscribe);
    }

    public boolean isSubscribed(User user, NotificationType type) {
        return repository.findByUserAndType(user, type)
                .map(NotificationSubscription::isEnabled)
                .orElse(true);
    }

    public List<NotificationSubscriptionDto> getActiveSubscriptions(User user) {
        List<NotificationSubscription> subscriptions = repository.findAllByUser(user);
        ActiveNotificationSubscriptionIterator iterator = new ActiveNotificationSubscriptionIterator(subscriptions);
        List<NotificationSubscriptionDto> activeSubscriptions = new ArrayList<>();

        while (iterator.hasNext()) {
            activeSubscriptions.add(iterator.next());
        }

        return activeSubscriptions;
    }
}

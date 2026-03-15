package pl.pollub.backend.notification;

import pl.pollub.backend.notification.dto.NotificationSubscriptionDto;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterates over subscriptions and returns only active ones.
 */
public class ActiveNotificationSubscriptionIterator implements Iterator<NotificationSubscriptionDto> {
    private final List<NotificationSubscription> subscriptions;
    private int currentIndex = 0;
    private NotificationSubscriptionDto nextActive;

    public ActiveNotificationSubscriptionIterator(List<NotificationSubscription> subscriptions) {
        this.subscriptions = subscriptions != null ? subscriptions : Collections.emptyList();
        this.nextActive = findNextActive();
    }

    @Override
    public boolean hasNext() {
        return nextActive != null;
    }

    @Override
    public NotificationSubscriptionDto next() {
        if (nextActive == null) {
            throw new NoSuchElementException("No more active notification subscriptions available");
        }

        NotificationSubscriptionDto current = nextActive;
        nextActive = findNextActive();
        return current;
    }

    private NotificationSubscriptionDto findNextActive() {
        while (currentIndex < subscriptions.size()) {
            NotificationSubscription subscription = subscriptions.get(currentIndex++);
            if (subscription.isEnabled()) {
                return new NotificationSubscriptionDto(subscription.getType(), true);
            }
        }
        return null;
    }
}

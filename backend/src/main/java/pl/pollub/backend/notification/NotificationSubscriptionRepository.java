package pl.pollub.backend.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.backend.auth.user.User;

import java.util.Optional;

public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, Long> {
    Optional<NotificationSubscription> findByUserAndType(User user, NotificationType type);
}

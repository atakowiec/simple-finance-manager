package pl.pollub.backend.notification;

import jakarta.persistence.*;
import lombok.Data;
import pl.pollub.backend.auth.user.User;

/**
 * User subscription settings for a given notification type.
 */
@Entity
@Table(
        name = "notification_subscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "type"})
)
@Data
public class NotificationSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}

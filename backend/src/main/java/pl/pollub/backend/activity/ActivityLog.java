package pl.pollub.backend.activity;

import jakarta.persistence.*;
import lombok.Data;
import pl.pollub.backend.auth.user.User;

import java.time.LocalDateTime;

/**
 * Persistent record of a single user activity event.
 * Created exclusively by {@link ActivityLoggingMediator} – services never instantiate this directly.
 */
@Entity
@Table(name = "activity_logs")
@Data
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User who performed the action. */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Optional group context.
     * Stored as a plain Long (not a FK relation) to survive group deletion
     * without cascading log removal.
     */
    @Column(name = "group_id")
    private Long groupId;

    /** Semantic classification of the event. */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private ActivityEventType eventType;

    /** Human-readable description produced by the mediator. */
    @Column(name = "message", nullable = false, length = 512)
    private String message;

    /** Wall-clock time at which the event was recorded. */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}


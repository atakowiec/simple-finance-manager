package pl.pollub.backend.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.auth.user.User;

import java.time.LocalDateTime;

/**
 * Concrete Mediator that centralises all activity-log creation logic.
 *
 * <p>Responsibilities:
 * <ol>
 *   <li>Receive raw {@link ActivityEventType} + {@link ActivityEventData} from any service.</li>
 *   <li>Map the event type to a human-readable message using the provided data.</li>
 *   <li>Assemble an {@link ActivityLog} entity and delegate persistence to {@link ActivityLogService}.</li>
 * </ol>
 *
 * <p>Services are completely unaware of how or where logs are stored.
 * Adding a new event type only requires adding an entry to {@link ActivityEventType}
 * and a case in {@link #formatMessage}.</p>
 *
 * <p>Role in the Mediator pattern: <b>ConcreteMediator</b></p>
 */
@Component
@RequiredArgsConstructor
public class ActivityLoggingMediator implements ActivityMediator {

    private final ActivityLogService activityLogService;

    // -------------------------------------------------------------------------
    // ActivityMediator
    // -------------------------------------------------------------------------

    /**
     * Receive an event from a colleague service, build a log entry and persist it.
     *
     * @param eventType semantic classification of the event
     * @param data      contextual payload for message formatting
     */
    @Override
    public void notify(ActivityEventType eventType, ActivityEventData data) {
        String message = formatMessage(eventType, data);

        ActivityLog log = new ActivityLog();
        log.setUser(data.getUser());
        log.setGroupId(data.getGroup() != null ? data.getGroup().getId() : null);
        log.setEventType(eventType);
        log.setMessage(message);
        log.setTimestamp(LocalDateTime.now());

        activityLogService.save(log);
    }

    // -------------------------------------------------------------------------
    // Message formatting — single place for all log message templates
    // -------------------------------------------------------------------------

    /**
     * Translate an event type + data into a human-readable activity message.
     * All message templates live here, making it easy to audit or change wording.
     */
    private String formatMessage(ActivityEventType eventType, ActivityEventData data) {
        String actor     = resolveUsername(data.getUser());
        String groupName = data.getGroup() != null ? data.getGroup().getName() : "unknown group";
        String resource  = data.getResourceName() != null ? data.getResourceName() : "";
        double amount    = data.getAmount() != null ? data.getAmount() : 0.0;
        String extra     = data.getAdditionalInfo() != null ? data.getAdditionalInfo() : "";

        return switch (eventType) {
            case EXPENSE_CREATED ->
                    String.format("%s added expense '%s' ($%.2f) in group '%s'",
                            actor, resource, amount, groupName);

            case EXPENSE_DELETED ->
                    String.format("%s deleted expense '%s' from group '%s'",
                            actor, resource, groupName);

            case EXPENSE_UPDATED ->
                    String.format("%s updated expense '%s' in group '%s'",
                            actor, resource, groupName);

            case INCOME_CREATED ->
                    String.format("%s added income '%s' ($%.2f) in group '%s'",
                            actor, resource, amount, groupName);

            case INCOME_DELETED ->
                    String.format("%s deleted income '%s' from group '%s'",
                            actor, resource, groupName);

            case INCOME_UPDATED ->
                    String.format("%s updated income '%s' in group '%s'",
                            actor, resource, groupName);

            case TRANSACTION_CREATED ->
                    String.format("%s created transaction '%s' ($%.2f) in group '%s'",
                            actor, resource, amount, groupName);

            case MEMBER_JOINED_GROUP ->
                    String.format("%s joined group '%s'", actor, groupName);

            case MEMBER_LEFT_GROUP ->
                    String.format("%s left group '%s'", actor, groupName);

            case MEMBER_REMOVED_FROM_GROUP ->
                    String.format("%s was removed from group '%s'", actor, groupName);

            case GROUP_CREATED ->
                    String.format("%s created group '%s'", actor, groupName);

            case GROUP_DELETED ->
                    String.format("%s deleted group '%s'", actor, groupName);

            case BUDGET_EXCEEDED ->
                    extra.isEmpty()
                            ? String.format("Budget limit exceeded in group '%s' (triggered by %s)", groupName, actor)
                            : String.format("Budget limit exceeded in group '%s' (triggered by %s) — %s", groupName, actor, extra);
        };
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String resolveUsername(User user) {
        return user != null ? user.getUsername() : "Unknown user";
    }
}


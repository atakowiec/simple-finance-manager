package pl.pollub.backend.activity.command;

import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityLog;
import pl.pollub.backend.activity.ActivityLogService;
import pl.pollub.backend.auth.user.User;

import java.time.LocalDateTime;

/**
 * Shared helpers for ActivityLogCommand implementations.
 */
public abstract class AbstractActivityLogCommand implements ActivityLogCommand {

    private final ActivityLogService activityLogService;

    protected AbstractActivityLogCommand(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @Override
    public void execute(ActivityEventType eventType, ActivityEventData data) {
        String message = createMessage(eventType, data);

        ActivityLog log = new ActivityLog();
        log.setUser(data.getUser());
        log.setGroupId(data.getGroup() != null ? data.getGroup().getId() : null);
        log.setEventType(eventType);
        log.setMessage(message);
        log.setTimestamp(LocalDateTime.now());

        activityLogService.save(log);
    }

    protected abstract String createMessage(ActivityEventType eventType, ActivityEventData data);

    protected String actor(ActivityEventData data) {
        User user = data != null ? data.getUser() : null;
        return user != null ? user.getUsername() : "Unknown user";
    }

    protected String groupName(ActivityEventData data) {
        if (data == null || data.getGroup() == null || data.getGroup().getName() == null) {
            return "unknown group";
        }
        return data.getGroup().getName();
    }

    protected String resource(ActivityEventData data) {
        if (data == null || data.getResourceName() == null) {
            return "";
        }
        return data.getResourceName();
    }

    protected double amount(ActivityEventData data) {
        if (data == null || data.getAmount() == null) {
            return 0.0;
        }
        return data.getAmount();
    }

    protected String extra(ActivityEventData data) {
        if (data == null || data.getAdditionalInfo() == null) {
            return "";
        }
        return data.getAdditionalInfo();
    }
}


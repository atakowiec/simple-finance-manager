package pl.pollub.backend.activity.command;

import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityLogService;

@Component
public class MembershipActivityLogCommand extends AbstractActivityLogCommand {

    public MembershipActivityLogCommand(ActivityLogService activityLogService) {
        super(activityLogService);
    }

    @Override
    public boolean supports(ActivityEventType eventType) {
        return eventType == ActivityEventType.MEMBER_JOINED_GROUP
                || eventType == ActivityEventType.MEMBER_LEFT_GROUP
                || eventType == ActivityEventType.MEMBER_REMOVED_FROM_GROUP;
    }

    @Override
    protected String createMessage(ActivityEventType eventType, ActivityEventData data) {
        return switch (eventType) {
            case MEMBER_JOINED_GROUP -> String.format("%s joined group '%s'", actor(data), groupName(data));
            case MEMBER_LEFT_GROUP -> String.format("%s left group '%s'", actor(data), groupName(data));
            case MEMBER_REMOVED_FROM_GROUP -> String.format("%s was removed from group '%s'", actor(data), groupName(data));
            default -> throw new IllegalArgumentException("Unsupported event type for membership command: " + eventType);
        };
    }
}


package pl.pollub.backend.activity.command;

import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityLogService;

@Component
public class GroupActivityLogCommand extends AbstractActivityLogCommand {

    public GroupActivityLogCommand(ActivityLogService activityLogService) {
        super(activityLogService);
    }

    @Override
    public boolean supports(ActivityEventType eventType) {
        return eventType == ActivityEventType.GROUP_CREATED
                || eventType == ActivityEventType.GROUP_DELETED;
    }

    @Override
    protected String createMessage(ActivityEventType eventType, ActivityEventData data) {
        return switch (eventType) {
            case GROUP_CREATED -> String.format("%s created group '%s'", actor(data), groupName(data));
            case GROUP_DELETED -> String.format("%s deleted group '%s'", actor(data), groupName(data));
            default -> throw new IllegalArgumentException("Unsupported event type for group command: " + eventType);
        };
    }
}


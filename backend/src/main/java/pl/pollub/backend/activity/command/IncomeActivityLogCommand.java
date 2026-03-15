package pl.pollub.backend.activity.command;

import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityLogService;

@Component
public class IncomeActivityLogCommand extends AbstractActivityLogCommand {

    public IncomeActivityLogCommand(ActivityLogService activityLogService) {
        super(activityLogService);
    }

    @Override
    public boolean supports(ActivityEventType eventType) {
        return eventType == ActivityEventType.INCOME_CREATED
                || eventType == ActivityEventType.INCOME_UPDATED
                || eventType == ActivityEventType.INCOME_DELETED;
    }

    @Override
    protected String createMessage(ActivityEventType eventType, ActivityEventData data) {
        return switch (eventType) {
            case INCOME_CREATED -> String.format("%s added income '%s' ($%.2f) in group '%s'",
                    actor(data), resource(data), amount(data), groupName(data));
            case INCOME_UPDATED -> String.format("%s updated income '%s' in group '%s'",
                    actor(data), resource(data), groupName(data));
            case INCOME_DELETED -> String.format("%s deleted income '%s' from group '%s'",
                    actor(data), resource(data), groupName(data));
            default -> throw new IllegalArgumentException("Unsupported event type for income command: " + eventType);
        };
    }
}


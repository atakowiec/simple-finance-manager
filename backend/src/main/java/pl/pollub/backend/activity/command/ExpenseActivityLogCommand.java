package pl.pollub.backend.activity.command;

import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityLogService;

@Component
public class ExpenseActivityLogCommand extends AbstractActivityLogCommand {

    public ExpenseActivityLogCommand(ActivityLogService activityLogService) {
        super(activityLogService);
    }

    @Override
    public boolean supports(ActivityEventType eventType) {
        return eventType == ActivityEventType.EXPENSE_CREATED
                || eventType == ActivityEventType.EXPENSE_UPDATED
                || eventType == ActivityEventType.EXPENSE_DELETED;
    }

    @Override
    protected String createMessage(ActivityEventType eventType, ActivityEventData data) {
        return switch (eventType) {
            case EXPENSE_CREATED -> String.format("%s added expense '%s' ($%.2f) in group '%s'",
                    actor(data), resource(data), amount(data), groupName(data));
            case EXPENSE_UPDATED -> String.format("%s updated expense '%s' in group '%s'",
                    actor(data), resource(data), groupName(data));
            case EXPENSE_DELETED -> String.format("%s deleted expense '%s' from group '%s'",
                    actor(data), resource(data), groupName(data));
            default -> throw new IllegalArgumentException("Unsupported event type for expense command: " + eventType);
        };
    }
}


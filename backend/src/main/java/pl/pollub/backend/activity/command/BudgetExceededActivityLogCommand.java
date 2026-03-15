package pl.pollub.backend.activity.command;

import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityLogService;

@Component
public class BudgetExceededActivityLogCommand extends AbstractActivityLogCommand {

    public BudgetExceededActivityLogCommand(ActivityLogService activityLogService) {
        super(activityLogService);
    }

    @Override
    public boolean supports(ActivityEventType eventType) {
        return eventType == ActivityEventType.BUDGET_EXCEEDED;
    }

    @Override
    protected String createMessage(ActivityEventType eventType, ActivityEventData data) {
        if (eventType != ActivityEventType.BUDGET_EXCEEDED) {
            throw new IllegalArgumentException("Unsupported event type for budget command: " + eventType);
        }

        String extraInfo = extra(data);
        if (extraInfo.isEmpty()) {
            return String.format("Budget limit exceeded in group '%s' (triggered by %s)",
                    groupName(data), actor(data));
        }

        return String.format("Budget limit exceeded in group '%s' (triggered by %s) - %s",
                groupName(data), actor(data), extraInfo);
    }
}


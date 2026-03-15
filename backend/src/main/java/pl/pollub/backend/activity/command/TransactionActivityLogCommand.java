package pl.pollub.backend.activity.command;

import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;
import pl.pollub.backend.activity.ActivityLogService;

@Component
public class TransactionActivityLogCommand extends AbstractActivityLogCommand {

    public TransactionActivityLogCommand(ActivityLogService activityLogService) {
        super(activityLogService);
    }

    @Override
    public boolean supports(ActivityEventType eventType) {
        return eventType == ActivityEventType.TRANSACTION_CREATED;
    }

    @Override
    protected String createMessage(ActivityEventType eventType, ActivityEventData data) {
        if (eventType != ActivityEventType.TRANSACTION_CREATED) {
            throw new IllegalArgumentException("Unsupported event type for transaction command: " + eventType);
        }

        return String.format("%s created transaction '%s' ($%.2f) in group '%s'",
                actor(data), resource(data), amount(data), groupName(data));
    }
}


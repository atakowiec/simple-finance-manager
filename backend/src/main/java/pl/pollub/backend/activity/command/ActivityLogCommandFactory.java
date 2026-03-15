package pl.pollub.backend.activity.command;

import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.ActivityEventType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves a command for each activity event type.
 */
@Component
public class ActivityLogCommandFactory {

    private final Map<ActivityEventType, ActivityLogCommand> commandByType = new EnumMap<>(ActivityEventType.class);

    public ActivityLogCommandFactory(List<ActivityLogCommand> commands) {
        for (ActivityEventType eventType : ActivityEventType.values()) {
            ActivityLogCommand selected = null;

            for (ActivityLogCommand command : commands) {
                if (!command.supports(eventType)) {
                    continue;
                }

                if (selected != null) {
                    throw new IllegalStateException("Multiple commands registered for event type: " + eventType);
                }

                selected = command;
            }

            if (selected == null) {
                throw new IllegalStateException("No command registered for event type: " + eventType);
            }

            commandByType.put(eventType, selected);
        }
    }

    public ActivityLogCommand get(ActivityEventType eventType) {
        ActivityLogCommand command = commandByType.get(eventType);
        if (command == null) {
            throw new IllegalStateException("No command for event type: " + eventType);
        }
        return command;
    }
}


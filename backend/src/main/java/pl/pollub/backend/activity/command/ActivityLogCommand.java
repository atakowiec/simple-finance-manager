package pl.pollub.backend.activity.command;

import pl.pollub.backend.activity.ActivityEventData;
import pl.pollub.backend.activity.ActivityEventType;

/**
 * Command contract used by ActivityLoggingMediator to execute full activity logging.
 */
public interface ActivityLogCommand {

    /**
     * Returns true when this command can handle the given event type.
     */
    boolean supports(ActivityEventType eventType);

    /**
     * Creates and persists an activity log entry for the given event.
     */
    void execute(ActivityEventType eventType, ActivityEventData data);
}


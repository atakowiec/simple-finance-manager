package pl.pollub.backend.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pollub.backend.activity.command.ActivityLogCommand;
import pl.pollub.backend.activity.command.ActivityLogCommandFactory;

// start mediator
/**
 * Concrete Mediator that centralises all activity-log creation logic.
 *
 * <p>Responsibilities:
 * <ol>
 *   <li>Receive raw {@link ActivityEventType} + {@link ActivityEventData} from any service.</li>
 *   <li>Resolve and execute an {@link ActivityLogCommand} for the event type.</li>
 * </ol>
 *
 * <p>Role in the Mediator pattern: <b>ConcreteMediator</b></p>
 */
@Component
@RequiredArgsConstructor
public class ActivityLoggingMediator implements ActivityMediator {

    private final ActivityLogCommandFactory commandFactory;

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
        ActivityLogCommand command = commandFactory.get(eventType);
        command.execute(eventType, data);
    }
}

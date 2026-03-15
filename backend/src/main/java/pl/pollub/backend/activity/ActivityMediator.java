package pl.pollub.backend.activity;

/**
 * Mediator interface for activity logging.
 *
 * <p>Services must not write activity logs directly. Instead, they emit events
 * through this mediator, which decides how each event is formatted and persisted.
 * This keeps services fully decoupled from the logging infrastructure and makes
 * it trivial to extend or swap the logging strategy.</p>
 *
 * <p>Role in the Mediator pattern: <b>Mediator</b></p>
 */
public interface ActivityMediator {

    /**
     * Notify the mediator that an activity event has occurred.
     *
     * @param eventType the semantic type of the event (what happened)
     * @param data      contextual data describing who did what, where, and with what resource
     */
    void notify(ActivityEventType eventType, ActivityEventData data);
}


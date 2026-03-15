package pl.pollub.backend.activity;

import lombok.Builder;
import lombok.Getter;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.transaction.model.Transaction;

/**
 * Immutable data object carrying all contextual information for an activity event.
 * Built using the Builder pattern for clean construction at call sites.
 * The ActivityLoggingMediator reads this data to produce a formatted log entry.
 */
@Getter
@Builder
public class ActivityEventData {

    /** The user who triggered the event (required). */
    private final User user;

    /** The group context for this event (optional, null for user-only events). */
    private final Group group;

    /** The transaction associated with this event (optional). */
    private final Transaction transaction;

    /** Human-readable name of the primary resource (e.g. expense name, group name). */
    private final String resourceName;

    /** Monetary amount involved in the event (e.g. expense/income amount). */
    private final Double amount;

    /** Free-form supplementary information for richer log messages. */
    private final String additionalInfo;
}


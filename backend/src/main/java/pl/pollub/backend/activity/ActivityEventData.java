package pl.pollub.backend.activity;

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

    private ActivityEventData(Builder builder) {
        this.user = builder.user;
        this.group = builder.group;
        this.transaction = builder.transaction;
        this.resourceName = builder.resourceName;
        this.amount = builder.amount;
        this.additionalInfo = builder.additionalInfo;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    // start builder
    public static final class Builder {
        private User user;
        private Group group;
        private Transaction transaction;
        private String resourceName;
        private Double amount;
        private String additionalInfo;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder group(Group group) {
            this.group = group;
            return this;
        }

        public Builder transaction(Transaction transaction) {
            this.transaction = transaction;
            return this;
        }

        public Builder resourceName(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }

        public Builder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder additionalInfo(String additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public ActivityEventData build() {
            return new ActivityEventData(this);
        }
    }
}


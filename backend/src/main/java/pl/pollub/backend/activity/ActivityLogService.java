package pl.pollub.backend.activity;

import pl.pollub.backend.auth.user.User;

import java.util.List;

/**
 * Service interface for storing and querying {@link ActivityLog} entries.
 * Used internally by {@link ActivityLoggingMediator} – not intended for direct
 * use by business services.
 */
public interface ActivityLogService {

    /**
     * Persist a single log entry.
     *
     * @param log the log entry to save
     * @return the saved (managed) entity
     */
    ActivityLog save(ActivityLog log);

    /**
     * Return all activity logs associated with the given group, ordered newest-first.
     *
     * @param groupId target group identifier
     * @return list of log entries
     */
    List<ActivityLog> getLogsForGroup(Long groupId);

    /**
     * Return all activity logs produced by the given user, ordered newest-first.
     *
     * @param user the authenticated user
     * @return list of log entries
     */
    List<ActivityLog> getLogsForUser(User user);
}


package pl.pollub.backend.exception.reporting.strategy;

import pl.pollub.backend.exception.reporting.ExceptionReportEvent;

// start strategy
// start liskov substitution
/**
 * Strategy that decides how a reported exception should trigger notifications.
 */
public interface ExceptionNotificationStrategy {
    boolean supports(ExceptionReportEvent event);

    void notify(ExceptionReportEvent event);
}


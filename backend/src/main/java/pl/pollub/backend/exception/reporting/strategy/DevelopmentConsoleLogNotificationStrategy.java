package pl.pollub.backend.exception.reporting.strategy;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.reporting.ExceptionReportEvent;

/**
 * Strategy that prints reported exceptions to the console during development.
 */
@Component
@Profile("dev")
public class DevelopmentConsoleLogNotificationStrategy implements ExceptionNotificationStrategy {

    @Override
    public boolean supports(ExceptionReportEvent event) {
        return true; // it is supported by @Profile("dev")
    }

    @Override
    public void notify(ExceptionReportEvent event) {
        System.out.printf(
                "[DEV][EXCEPTION] status=%d method=%s path=%s type=%s message=%s%n",
                event.status().value(),
                event.method(),
                event.path(),
                event.exception().getClass().getName(),
                event.exception().getMessage()
        );
        event.exception().printStackTrace(System.out);
    }
}


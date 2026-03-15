package pl.pollub.backend.exception.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observer that prints every reported exception to the application logs.
 */
@Component
public class ExceptionLoggingObserver implements ExceptionReportObserver {
    private static final Logger log = LoggerFactory.getLogger(ExceptionLoggingObserver.class);

    @Override
    public void update(ExceptionReportEvent event) {
        String message = "Captured exception for {} {} -> status={} type={} message={}";

        if (event.isServerError()) {
            log.error(message,
                    event.method(),
                    event.path(),
                    event.status().value(),
                    event.exception().getClass().getSimpleName(),
                    event.exception().getMessage(),
                    event.exception());
            return;
        }

        log.warn(message,
                event.method(),
                event.path(),
                event.status().value(),
                event.exception().getClass().getSimpleName(),
                event.exception().getMessage(),
                event.exception());
    }
}


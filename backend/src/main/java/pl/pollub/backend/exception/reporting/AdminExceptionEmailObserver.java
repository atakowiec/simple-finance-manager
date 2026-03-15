package pl.pollub.backend.exception.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.reporting.strategy.ExceptionNotificationStrategy;

import java.util.List;

/**
 * Observer that delegates notification behavior to pluggable strategies.
 */
@Component
public class AdminExceptionEmailObserver implements ExceptionReportObserver {
    private static final Logger log = LoggerFactory.getLogger(AdminExceptionEmailObserver.class);

    private final List<ExceptionNotificationStrategy> notificationStrategies;

    public AdminExceptionEmailObserver(List<ExceptionNotificationStrategy> notificationStrategies) {
        this.notificationStrategies = notificationStrategies;
    }

    @Override
    public void update(ExceptionReportEvent event) {
        for (ExceptionNotificationStrategy strategy : notificationStrategies) {
            try {
                if (strategy.supports(event)) {
                    strategy.notify(event);
                }
            } catch (Exception exception) {
                log.error("Exception reporting strategy {} failed for {} {}",
                        strategy.getClass().getSimpleName(),
                        event.method(),
                        event.path(),
                        exception);
            }
        }
    }
}


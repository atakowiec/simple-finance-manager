package pl.pollub.backend.exception.reporting;

/**
 * Observer that reacts to reported exceptions.
 */
@FunctionalInterface
public interface ExceptionReportObserver {
    void update(ExceptionReportEvent event);
}


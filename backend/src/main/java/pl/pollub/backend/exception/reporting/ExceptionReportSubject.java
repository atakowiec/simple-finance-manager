package pl.pollub.backend.exception.reporting;

/**
 * Subject responsible for publishing exception report events to observers.
 */
public interface ExceptionReportSubject {
    void registerObserver(ExceptionReportObserver observer);

    void removeObserver(ExceptionReportObserver observer);

    void notifyObservers(ExceptionReportEvent event);
}


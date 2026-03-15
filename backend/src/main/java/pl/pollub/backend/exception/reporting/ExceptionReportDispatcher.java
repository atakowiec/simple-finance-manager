package pl.pollub.backend.exception.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observer subject that dispatches exception report events to all registered observers.
 */
@Service
public class ExceptionReportDispatcher implements ExceptionReportSubject {
    private static final Logger log = LoggerFactory.getLogger(ExceptionReportDispatcher.class);

    private final List<ExceptionReportObserver> observers = new CopyOnWriteArrayList<>();

    public ExceptionReportDispatcher(List<ExceptionReportObserver> initialObservers) {
        observers.addAll(initialObservers);
    }

    @Override
    public void registerObserver(ExceptionReportObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ExceptionReportObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(ExceptionReportEvent event) {
        for (ExceptionReportObserver observer : observers) {
            try {
                observer.update(event);
            } catch (Exception exception) {
                log.error("Exception observer {} failed while handling {} {}",
                        observer.getClass().getSimpleName(),
                        event.method(),
                        event.path(),
                        exception);
            }
        }
    }
}


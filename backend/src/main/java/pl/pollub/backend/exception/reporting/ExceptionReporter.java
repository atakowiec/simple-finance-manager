package pl.pollub.backend.exception.reporting;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Facade used by exception handlers to publish reporting events.
 */
@Service
public class ExceptionReporter {
    private final ExceptionReportSubject subject;

    public ExceptionReporter(ExceptionReportSubject subject) {
        this.subject = subject;
    }

    public void report(Throwable exception, HttpStatus status, HttpServletRequest request) {
        subject.notifyObservers(ExceptionReportEvent.from(exception, status, request));
    }
}


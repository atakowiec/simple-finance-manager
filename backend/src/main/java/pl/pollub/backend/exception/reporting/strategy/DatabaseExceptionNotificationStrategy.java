package pl.pollub.backend.exception.reporting.strategy;

import org.springframework.stereotype.Component;
import pl.pollub.backend.exception.reporting.ExceptionReportEvent;
import pl.pollub.backend.exception.reporting.persistence.ReportedExceptionEntity;
import pl.pollub.backend.exception.reporting.persistence.ReportedExceptionRepository;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Strategy that saves every reported exception to the database.
 */
@Component
public class DatabaseExceptionNotificationStrategy implements ExceptionNotificationStrategy {
    private final ReportedExceptionRepository reportedExceptionRepository;

    public DatabaseExceptionNotificationStrategy(ReportedExceptionRepository reportedExceptionRepository) {
        this.reportedExceptionRepository = reportedExceptionRepository;
    }

    @Override
    public boolean supports(ExceptionReportEvent event) {
        return true;
    }

    @Override
    public void notify(ExceptionReportEvent event) {
        ReportedExceptionEntity entity = new ReportedExceptionEntity();
        entity.setHttpStatus(event.status().value());
        entity.setHttpMethod(event.method());
        entity.setRequestPath(event.path());
        entity.setServerError(event.isServerError());
        entity.setExceptionType(event.exception().getClass().getName());
        entity.setExceptionMessage(event.exception().getMessage());
        entity.setStackTrace(toStackTrace(event.exception()));
        entity.setOccurredAt(event.occurredAt());

        reportedExceptionRepository.save(entity);
    }

    private String toStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
}


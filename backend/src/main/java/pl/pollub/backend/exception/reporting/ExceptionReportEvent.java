package pl.pollub.backend.exception.reporting;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Immutable event published whenever the application reports an exception.
 */
public record ExceptionReportEvent(
        Throwable exception,
        HttpStatus status,
        String method,
        String path,
        Instant occurredAt
) {
    public ExceptionReportEvent {
        if (exception == null) {
            throw new IllegalArgumentException("exception cannot be null");
        }
        status = status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
        method = method == null || method.isBlank() ? "UNKNOWN" : method;
        path = path == null || path.isBlank() ? "UNKNOWN" : path;
        occurredAt = occurredAt == null ? Instant.now() : occurredAt;
    }

    public static ExceptionReportEvent from(Throwable exception, HttpStatus status, HttpServletRequest request) {
        String method = request != null ? request.getMethod() : null;
        String path = request != null ? request.getRequestURI() : null;
        return new ExceptionReportEvent(exception, status, method, path, Instant.now());
    }

    public boolean isServerError() {
        return status.is5xxServerError();
    }
}



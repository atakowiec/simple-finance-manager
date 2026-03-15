package pl.pollub.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pollub.backend.exception.reporting.ExceptionReporter;

/**
 * Exception handler for HttpException. It is used before the default exception handler.
 */
@RestControllerAdvice
@Order(1)
@RequiredArgsConstructor
public class HttpExceptionHandler {
    private final ExceptionReporter exceptionReporter;

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> handleHttpException(HttpException ex, HttpServletRequest request) {
        exceptionReporter.report(ex, ex.getHttpStatus(), request);
        return ex.toResponseEntity();
    }
}

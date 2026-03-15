package pl.pollub.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pollub.backend.exception.reporting.ExceptionReporter;

/**
 * Default exception handler for the server. It catches all exceptions that are not caught by other handlers and returns 500 status code.
 */
@RestControllerAdvice
@Order()
@RequiredArgsConstructor
public class ServerExceptionHandler {
    private final ExceptionReporter exceptionReporter;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleHttpException(Exception ex, HttpServletRequest request) {
        exceptionReporter.report(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
        return new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error").toResponseEntity();
    }
}

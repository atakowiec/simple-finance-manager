package pl.pollub.backend.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Default exception handler for the server. It catches all exceptions that are not caught by other handlers and returns 500 status code.
 */
@RestControllerAdvice
@Order()
public class ServerExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleHttpException(Exception ex) {
        ex.printStackTrace();
        return new HttpException(500, "Internal server error").toResponseEntity();
    }
}

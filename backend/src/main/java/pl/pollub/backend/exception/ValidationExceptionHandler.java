package pl.pollub.backend.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pollub.backend.util.SimpleJsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exception handler for validation exceptions.
 */
@RestControllerAdvice
@Order(0)
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        Map<String, List<String>> errors = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();

            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        }

        for (ObjectError objectError : bindingResult.getGlobalErrors()) {
            String message = objectError.getDefaultMessage();
            errors.computeIfAbsent("message", k -> new ArrayList<>()).add(message);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(SimpleJsonBuilder.of("error", HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .add("message", "Validation failed")
                                .add("status", HttpStatus.BAD_REQUEST.value())
                                .add("errors", errors)
                                .toJson());
    }
}

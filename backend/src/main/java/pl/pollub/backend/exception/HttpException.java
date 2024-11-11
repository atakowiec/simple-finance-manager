package pl.pollub.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pl.pollub.backend.util.SimpleJsonBuilder;

/**
 * Exception that represents an HTTP error. It contains the HTTP status code and a message.
 * If the exception is thrown in a controller, it will be automatically converted to a JSON response and sent to the client.
 */
@Getter
public class HttpException extends RuntimeException {
    private final HttpStatus httpStatus;

    /**
     * Creates a new HttpException with the specified status code and message.
     *
     * @param httpStatus HTTP status code
     * @param message    error message
     */
    public HttpException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /**
     * Creates a new HttpException with the specified status code and message.
     *
     * @param statusCode HTTP status code
     * @param message    error message
     */
    public HttpException(int statusCode, String message) {
        this(HttpStatus.valueOf(statusCode), message);
    }

    /**
     * Converts the exception to a ResponseEntity object.
     *
     * @return ResponseEntity object with the HTTP status code and a JSON body containing the error message
     */
    public ResponseEntity<String> toResponseEntity() {
        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(SimpleJsonBuilder.of("error", httpStatus.getReasonPhrase())
                        .add("message", getMessage())
                        .add("status", httpStatus.value())
                        .toJson());
    }
}

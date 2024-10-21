package pl.pollub.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pl.pollub.backend.util.SimpleJsonBuilder;

@Getter
public class HttpException extends RuntimeException {
    private final HttpStatus httpStatus;

    public HttpException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpException(int statusCode, String message) {
        this(HttpStatus.valueOf(statusCode), message);
    }

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

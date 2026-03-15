package pl.pollub.backend.exception.reporting.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

/**
 * Persistent record of one reported exception event.
 */
@Entity
@Table(name = "reported_exceptions")
@Data
public class ReportedExceptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "http_status", nullable = false)
    private Integer httpStatus;

    @Column(name = "http_method", nullable = false, length = 16)
    private String httpMethod;

    @Column(name = "request_path", nullable = false, length = 512)
    private String requestPath;

    @Column(name = "server_error", nullable = false)
    private boolean serverError;

    @Column(name = "exception_type", nullable = false)
    private String exceptionType;

    @Column(name = "exception_message", length = 2048)
    private String exceptionMessage;

    @Lob
    @Column(name = "stack_trace")
    private String stackTrace;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
}


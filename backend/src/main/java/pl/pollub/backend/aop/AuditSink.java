package pl.pollub.backend.aop;

// start bridge
/**
 * Implementor for Bridge pattern - audit sink.
 */
public interface AuditSink {
    void info(String message);

    void warn(String message);

    void debug(String message);
}

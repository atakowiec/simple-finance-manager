package pl.pollub.backend.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SLF4J-based audit sink.
 */
@Component
public class LoggerAuditSink implements AuditSink {
    private static final Logger LOG = LoggerFactory.getLogger(LoggerAuditSink.class);

    @Override
    public void info(String message) {
        LOG.info(message);
    }

    @Override
    public void warn(String message) {
        LOG.warn(message);
    }

    @Override
    public void debug(String message) {
        LOG.debug(message);
    }
}

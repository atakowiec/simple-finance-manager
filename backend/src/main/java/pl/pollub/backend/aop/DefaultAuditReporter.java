package pl.pollub.backend.aop;

import org.springframework.stereotype.Component;

/**
 * Default reporter that formats audit messages and delegates to a sink.
 */
@Component
public class DefaultAuditReporter implements AuditReporter {
    private final AuditSink sink;

    public DefaultAuditReporter(AuditSink sink) {
        this.sink = sink;
    }

    @Override
    public void mutationSuccess(String method) {
        sink.info(String.format("[AOP][mutation] Success: %s", method));
    }

    @Override
    public void mutationFailure(String method, Throwable exception) {
        sink.warn(String.format("[AOP][mutation] Failure: %s -> %s", method, exception.getClass().getSimpleName()));
    }

    @Override
    public void controllerEnter(String method) {
        sink.debug(String.format("[AOP][controller] Enter %s", method));
    }

    @Override
    public void controllerExit(String method, long elapsedMs) {
        sink.info(String.format("[AOP][controller] Exit %s in %d ms", method, elapsedMs));
    }

    @Override
    public void controllerFailure(String method, long elapsedMs, Throwable exception) {
        sink.warn(String.format("[AOP][controller] Failed %s in %d ms: %s",
                method, elapsedMs, exception.getClass().getSimpleName()));
    }

    @Override
    public void serviceTiming(String method, long elapsedMs) {
        sink.info(String.format("[AOP][service] %s took %d ms", method, elapsedMs));
    }
}

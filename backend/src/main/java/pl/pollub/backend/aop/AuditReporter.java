package pl.pollub.backend.aop;

/**
 * Abstraction for audit reporting (Bridge pattern).
 */
public interface AuditReporter {
    void mutationSuccess(String method);

    void mutationFailure(String method, Throwable exception);

    void controllerEnter(String method);

    void controllerExit(String method, long elapsedMs);

    void controllerFailure(String method, long elapsedMs, Throwable exception);

    void serviceTiming(String method, long elapsedMs);
}

package pl.pollub.backend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerTracingAspect {
    private final AuditReporter reporter;

    public ControllerTracingAspect(AuditReporter reporter) {
        this.reporter = reporter;
    }

    @Around("execution(public * pl.pollub.backend..*Controller.*(..))")
    public Object traceControllerCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        String method = joinPoint.getSignature().toShortString();

        reporter.controllerEnter(method);
        try {
            Object result = joinPoint.proceed();
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            reporter.controllerExit(method, elapsedMs);
            return result;
        } catch (Throwable ex) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            reporter.controllerFailure(method, elapsedMs, ex);
            throw ex;
        }
    }
}


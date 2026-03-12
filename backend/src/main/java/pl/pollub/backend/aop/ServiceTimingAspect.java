package pl.pollub.backend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceTimingAspect {
    private final AuditReporter reporter;

    public ServiceTimingAspect(AuditReporter reporter) {
        this.reporter = reporter;
    }

    @Around("execution(public * pl.pollub.backend..*ServiceImpl.*(..))")
    public Object measureServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        String method = joinPoint.getSignature().toShortString();

        try {
            return joinPoint.proceed();
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            reporter.serviceTiming(method, elapsedMs);
        }
    }
}


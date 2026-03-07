package pl.pollub.backend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerTracingAspect {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerTracingAspect.class);

    @Around("execution(public * pl.pollub.backend..*Controller.*(..))")
    public Object traceControllerCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        String method = joinPoint.getSignature().toShortString();

        LOG.debug("[AOP][controller] Enter {}", method);
        try {
            Object result = joinPoint.proceed();
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            LOG.info("[AOP][controller] Exit {} in {} ms", method, elapsedMs);
            return result;
        } catch (Throwable ex) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            LOG.warn("[AOP][controller] Failed {} in {} ms: {}", method, elapsedMs, ex.getClass().getSimpleName());
            throw ex;
        }
    }
}


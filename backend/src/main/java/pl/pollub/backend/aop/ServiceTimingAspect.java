package pl.pollub.backend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceTimingAspect {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceTimingAspect.class);

    @Around("execution(public * pl.pollub.backend..*ServiceImpl.*(..))")
    public Object measureServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        String method = joinPoint.getSignature().toShortString();

        try {
            return joinPoint.proceed();
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            LOG.info("[AOP][service] {} took {} ms", method, elapsedMs);
        }
    }
}


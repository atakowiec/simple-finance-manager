package pl.pollub.backend.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MutationAuditAspect {
    private static final Logger LOG = LoggerFactory.getLogger(MutationAuditAspect.class);

    @Pointcut("execution(public * pl.pollub.backend..*ServiceImpl.create*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.update*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.delete*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.remove*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.change*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.invite*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.accept*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.deny*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.import*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.export*(..)) || " +
              "execution(public * pl.pollub.backend..*ServiceImpl.clone*(..))")
    public void mutatingServiceOperation() {
        // Pointcut marker method.
    }

    @AfterReturning("mutatingServiceOperation()")
    public void auditMutationSuccess(JoinPoint joinPoint) {
        LOG.info("[AOP][mutation] Success: {}", joinPoint.getSignature().toShortString());
    }

    @AfterThrowing(pointcut = "mutatingServiceOperation()", throwing = "exception")
    public void auditMutationFailure(JoinPoint joinPoint, Throwable exception) {
        LOG.warn("[AOP][mutation] Failure: {} -> {}", joinPoint.getSignature().toShortString(), exception.getClass().getSimpleName());
    }
}


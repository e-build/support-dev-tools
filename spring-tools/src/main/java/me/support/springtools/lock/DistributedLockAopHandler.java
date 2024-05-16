package me.support.springtools.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;

@Aspect
@Component
public class DistributedLockAopHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LockRepository lockRepository;
    private final TransactionTemplate txTemplate;

    public DistributedLockAopHandler(
        LockRepository lockRepository,
        TransactionTemplate txTemplate
    ) {
        this.lockRepository = lockRepository;
        this.txTemplate = txTemplate;
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Around("@annotation(me.support.springtools.lock.DistributedLock)")
    Object handle(ProceedingJoinPoint joinPoint) throws InterruptedException {
        DistributedLock distributedLock = resolveDistributedLock(joinPoint);

        String key = parseKeyFromJointPoint(joinPoint);
        Lock lock = lockRepository.get(key);

        try {
            lock.lock(distributedLock.leaseTime, distributedLock.timeUnit);
            return txTemplate.execute(status -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                logger.info("Lock already release [${resolveSignature(joinPoint).method.name} $key]");
            }
        }
    }

    private Boolean availableLock(
        Lock lock,
        DistributedLock distributedLock
    ) {
        return lock.tryLock(
            distributedLock.waitTime,
            distributedLock.leaseTime,
            distributedLock.timeUnit
        );
    }

    private String parseKeyFromJointPoint(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = resolveSignature(joinPoint);
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        return LockKeys.FOO.getPrefix() +
            FooSpringELParser.resolveDynamicValue(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                distributedLock.key
            );
    }

    private DistributedLock resolveDistributedLock(ProceedingJoinPoint joinPoint) {
        return (resolveSignature(joinPoint)).getMethod()
            .getAnnotation(DistributedLock.class);
    }

    private MethodSignature resolveSignature(ProceedingJoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }
}

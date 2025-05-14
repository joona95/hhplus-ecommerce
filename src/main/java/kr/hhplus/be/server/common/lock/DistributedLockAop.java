package kr.hhplus.be.server.common.lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Order(1)
@Aspect
@Component
public class DistributedLockAop {

    private final LockKeyGenerator lockKeyGenerator;
    private final LockManager lockManager;
    private final AopForTransaction aopForTransaction;

    public DistributedLockAop(LockKeyGenerator lockKeyGenerator, LockManager lockManager, AopForTransaction aopForTransaction) {
        this.lockKeyGenerator = lockKeyGenerator;
        this.lockManager = lockManager;
        this.aopForTransaction = aopForTransaction;
    }

    @Around("@annotation(kr.hhplus.be.server.common.lock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("락 획득");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = lockKeyGenerator.generateKey(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key(), distributedLock.lockType());

        return lockManager.executeWithLock(key, distributedLock.lockType(), () -> aopForTransaction.proceed(joinPoint));
    }
}

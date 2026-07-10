package stitch.crew.hour.common.lock;

import org.aspectj.lang.ProceedingJoinPoint;

public interface AopTransactionManager {
    Object proceed(ProceedingJoinPoint joinPoint) throws Throwable;
}

package stitch.crew.hour.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import stitch.crew.hour.reservation.dto.ReservationRequest;

import java.time.LocalDate;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {
    private final AopTransactionManager aopTransactionManager;
    private final RedissonClient redissonClient;

    //pointcut으로 커스텀 어노테이션 사용
    // 해당 커스텀어노테이션이 선언된 메서드를 자동으로 인터셉트하여 AOP를 수행
    //메서드에 @Lock 애노테이션이 붙어있는지 확인하고 붙어있는 @Lock 애노테이션 객체를 lock이라는 이름의 Advice 파라미터에 바인딩
    @Around("@annotation(stitch.crew.hour.common.lock.Lock)&&@annotation(lock)")
    public Object lock(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        //같은 날짜를 기준으로 lock을 잡기
        LocalDate date = ((ReservationRequest)joinPoint.getArgs()[1]).date();
        RLock rLock = redissonClient.getLock("%s:%s".formatted(lock.key(),date));
//        log.info("lock key = {}", "%s:%s".formatted(lock.key(), date));
        try {
            // 커스텀어노테이션에서 설정된 필드값으로 동적으로 설정하여 tryLock()을 통해 락을 획득
            boolean available = rLock.tryLock(lock.waitTime(), lock.leaseTime(), lock.timeunit());
//            log.info("lock acquired = {}, key = {}", available, rLock.getName());
            if ( !available ) return false;
            return aopTransactionManager.proceed(joinPoint);
        } finally {
            // 트랜잭션의 작업이 끝나거나, Lock이 만료되어 진입
            // Lock이 만료된 경우 rLock.unlock() 호출 시 예외 발생하므로, 방어로직 작성
            if(rLock.isHeldByCurrentThread()) {
                rLock.unlock();
//                log.info("lock released, key = {}", rLock.getName());
            }
        }
    }
}

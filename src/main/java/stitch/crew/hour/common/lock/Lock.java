package stitch.crew.hour.common.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)  //런타임에서 어노테이션을 실행
@Target(ElementType.METHOD)  //메서드에 적용
public @interface Lock {
    Key key();
    long waitTime() default 20000L;
    long leaseTime() default 50000L;
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;
    enum Key {
        RESERVATION
    }
}

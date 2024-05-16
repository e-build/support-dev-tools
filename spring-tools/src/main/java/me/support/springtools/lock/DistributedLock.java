package me.support.springtools.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String key = "";
    TimeUnit timeUnit = TimeUnit.SECONDS;
    Long waitTime = 5L;
    Long leaseTime = 3L;
}

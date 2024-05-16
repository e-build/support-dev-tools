package me.support.springtools.lock;

import java.util.concurrent.TimeUnit;

public interface Lock {
    void lock(Long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    void unlock();

    Boolean tryLock(Long waitTime, Long leaseTime, TimeUnit timeUnit);
}

package me.support.springtools.lock.implementsample;

import me.support.springtools.lock.Lock;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

public class RedissonLock implements Lock {

    private final RLock rLock;

    public RedissonLock(RLock rLock) {
        this.rLock = rLock;
    }

    @Override
    public void lock(Long leaseTime, TimeUnit timeUnit) throws InterruptedException {
        rLock.lock(leaseTime, timeUnit);
    }

    @Override
    public void unlock() {
        rLock.unlock();
    }

    @Override
    public Boolean tryLock(Long waitTime, Long leaseTime, TimeUnit timeUnit) {
        return rLock.tryLock();
    }
}

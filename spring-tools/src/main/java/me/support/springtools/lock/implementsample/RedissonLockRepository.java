package me.support.springtools.lock.implementsample;

import me.support.springtools.lock.Lock;
import me.support.springtools.lock.LockRepository;
import org.redisson.api.RedissonClient;

public class RedissonLockRepository implements LockRepository {

    private final RedissonClient redissonClient;

    public RedissonLockRepository(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Lock get(String key) {
        return new RedissonLock(redissonClient.getLock(key));
    }
}

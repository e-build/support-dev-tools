package me.support.springtools.lock;

public interface LockRepository {

    Lock get(String key);
}

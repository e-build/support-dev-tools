package me.support.springtools.lock;

public enum LockKeys {

    FOO("foo_key");

    private final String prefix;

    LockKeys(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}

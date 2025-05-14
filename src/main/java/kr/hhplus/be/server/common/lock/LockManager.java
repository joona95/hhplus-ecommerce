package kr.hhplus.be.server.common.lock;

public interface LockManager {
    <T> T executeWithLock(String key, LockType lockType, LockSupplier<T> task) throws Throwable;
}

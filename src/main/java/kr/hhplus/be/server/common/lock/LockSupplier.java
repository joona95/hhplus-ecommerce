package kr.hhplus.be.server.common.lock;

@FunctionalInterface
public interface LockSupplier<T> {
    T get() throws Throwable;
}

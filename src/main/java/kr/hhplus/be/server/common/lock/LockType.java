package kr.hhplus.be.server.common.lock;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum LockType {

    COUPON("coupon", TimeUnit.SECONDS, 5L, 3L, 3, 5);

    private final String prefix;
    private final TimeUnit timeUnit;
    private final long waitTime;
    private final long leaseTime;
    private final int maxRetry;
    private final long retryDelay;

    LockType(String prefix, TimeUnit timeUnit, long waitTime, long leaseTime, int maxRetry, long retryDelay) {
        this.prefix = prefix;
        this.timeUnit = timeUnit;
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
        this.maxRetry = maxRetry;
        this.retryDelay = retryDelay;
    }

    public String createKey(String key) {
        return prefix + ":" + key;
    }
}

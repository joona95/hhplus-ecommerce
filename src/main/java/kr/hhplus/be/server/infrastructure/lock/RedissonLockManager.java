package kr.hhplus.be.server.infrastructure.lock;

import kr.hhplus.be.server.common.lock.LockManager;
import kr.hhplus.be.server.common.lock.LockSupplier;
import kr.hhplus.be.server.common.lock.LockType;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedissonLockManager implements LockManager {

    private final RedissonClient redissonClient;

    public RedissonLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public <T> T executeWithLock(String key, LockType lockType, LockSupplier<T> task) throws Throwable {

        RLock rLock = redissonClient.getLock(key);
        boolean isLocked = false;

        try {
            for (int i = 0; i < lockType.getMaxRetry(); i++) {
                isLocked = rLock.tryLock(lockType.getWaitTime(), lockType.getLeaseTime(), lockType.getTimeUnit());

                if (isLocked) {
                    break;
                }

                Thread.sleep(lockType.getRetryDelay());
            }

            if (!isLocked) {
                throw new IllegalStateException("Redisson 락 획득 실패 - key: " + key);
            }

            return task.get();
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            if (isLocked && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info("락 해제");
            }
        }
    }
}

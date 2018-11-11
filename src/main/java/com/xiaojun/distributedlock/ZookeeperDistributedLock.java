package com.xiaojun.distributedlock;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * zookeeper实现分布式锁
 *
 * @author xiaojun
 * @date 2018/11/10 23:33
 */
@Slf4j
public class ZookeeperDistributedLock extends AbstractDistributedLock {

    private static final String NODE_PATH = "/curator/lock/%s";

    private CuratorFramework curatorFramework;

    public ZookeeperDistributedLock(@NonNull CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        if (!CuratorFrameworkState.STARTED.equals(curatorFramework.getState())) {
            log.warn("instance must be started before calling this method");
            return false;
        }
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, String.format(NODE_PATH, key));
        boolean locked;
        try {
            locked = mutex.acquire(expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.info("get lock fail", e);
            return false;
        }
        ZookeeperLockContext.setContext(mutex);
        return locked;
    }

    @Override
    public boolean releaseLock(String key) {
        InterProcessLock interProcessLock = ZookeeperLockContext.getContext();
        if (null != interProcessLock) {
            try {
                interProcessLock.release();
                return true;
            } catch (Exception e) {
                log.warn("zookeeper lock release error", e);
                return true;
            } finally {
                ZookeeperLockContext.remove();
            }
        }
        return true;
    }
}

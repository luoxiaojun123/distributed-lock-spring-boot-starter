package com.xiaojun.distributedlock;

/**
 * 分布式锁顶级接口
 *
 * @author long.luo
 * @date 2018/11/10 23:03
 */
public interface DistributedLock {
    /**
     *  获取锁超时时间
     */
    long TIMEOUT_MILLIS = 30000;
    /**
     * 重试次数
     */
    int RETRY_TIMES = 10;
    /**
     * 睡眠时间
     */
    long SLEEP_MILLIS = 500;

    boolean lock(String key);

    boolean lock(String key, int retryTimes);

    boolean lock(String key, int retryTimes, long sleepMillis);

    boolean lock(String key, long expire);

    boolean lock(String key, long expire, int retryTimes);

    boolean lock(String key, long expire, int retryTimes, long sleepMillis);

    boolean releaseLock(String key);
}

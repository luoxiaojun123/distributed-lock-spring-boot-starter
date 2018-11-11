package com.xiaojun.aop;

import com.xiaojun.annotation.ZookeeperLock;
import com.xiaojun.distributedlock.DistributedLock;
import com.xiaojun.enums.LockFailActionEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author xiaojun
 * @date 2018/11/10 23:19
 */
@Aspect
@Component
@Slf4j
public class ZookeeperAop extends BaseAop {

    private DistributedLock distributedLock;

    public ZookeeperAop(@NonNull DistributedLock distributedLock) {
        this.distributedLock = distributedLock;
    }

    @Pointcut("@annotation(com.xiaojun.annotation.ZookeeperLock)")
    public void distributed() {

    }

    @Around("distributed()&&@annotation(zooKeeperLock)")
    public void execute(ProceedingJoinPoint pjp, ZookeeperLock zooKeeperLock) throws Throwable {
        String lockKey = zooKeeperLock.lockKey();
        Method method = getMethod(pjp);
        String key = zooKeeperLock.prefix() + "/" + parseKey(lockKey, method, pjp.getArgs());
        int retryTimes = zooKeeperLock.action().equals(LockFailActionEnum.CONTINUE) ? zooKeeperLock.retryTimes() : 0;

        boolean lock = distributedLock.lock(key, zooKeeperLock.expireTime(), retryTimes, zooKeeperLock.sleepMills());
        if (!lock) {
            log.debug("get lock failed : " + key);
            return;
        }
        //得到锁,执行方法，释放锁
        log.debug("get lock success : " + key);
        try {
            pjp.proceed();
        } catch (Exception e) {
            log.error("execute zookeeper locked method occured an exception", e);
        } finally {
            boolean releaseResult = distributedLock.releaseLock(key);
            log.debug("release zookeeper lock : " + key + (releaseResult ? " success" : " failed"));
        }
    }
}

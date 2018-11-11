package com.xiaojun.aop;

import com.xiaojun.annotation.RedisLock;
import com.xiaojun.distributedlock.DistributedLock;
import com.xiaojun.enums.LockFailActionEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author long.luo
 * @date 2018/10/9 10:13
 */
@Aspect
@Slf4j
public class RedisAop extends BaseAop{


    private DistributedLock distributedLock;

    public RedisAop(@NonNull DistributedLock distributedLock) {
        this.distributedLock = distributedLock;
    }

    @Pointcut("@annotation(com.xiaojun.annotation.RedisLock)")
    public void distributed() {

    }

    @Around("distributed()&&@annotation(redisLock)")
    public void execute(ProceedingJoinPoint pjp, RedisLock redisLock) throws Throwable {
        String lockKey = redisLock.lockKey();
        Method method = getMethod(pjp);
        String key = redisLock.prefix() + ":" + parseKey(lockKey, method, pjp.getArgs());
        int retryTimes = redisLock.action().equals(LockFailActionEnum.CONTINUE) ? redisLock.retryTimes() : 0;

        boolean lock = distributedLock.lock(key, redisLock.expireTime(), retryTimes, redisLock.sleepMills());
        if (!lock) {
            log.debug("get lock failed : " + key);
            return;
        }
        //得到锁,执行方法，释放锁
        log.debug("get lock success : " + key);
        try {
            pjp.proceed();
        } catch (Exception e) {
            log.error("execute redis locked method occured an exception", e);
        } finally {
            boolean releaseResult = distributedLock.releaseLock(key);
            log.debug("release redis lock : " + key + (releaseResult ? " success" : " failed"));
        }
    }
}

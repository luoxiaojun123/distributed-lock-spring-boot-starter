package com.xiaojun.annotation;

import com.xiaojun.enums.LockFailActionEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisLock {

    /**
     * key值
     *
     * @return
     */
    String lockKey() default "";

    String prefix() default "jun";

    /**
     * 到期时间
     *
     * @return
     */
    int expireTime() default 20000;

    /**
     * 当获取失败时候动作
     */
    LockFailActionEnum action() default LockFailActionEnum.GIVEUP;

    /**
     * 重试的间隔时间
     */
    long sleepMills() default 200;

    /**
     * 重试次数
     */
    int retryTimes() default 5;
}

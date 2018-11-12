package com.xiaojun.distributedlock;

import org.apache.curator.framework.recipes.locks.InterProcessLock;

/**
 * zookeeper上下文
 * @author xiaojun
 * @date 2018/11/10 23:43
 */
public class ZookeeperLockContext {

    private static final ThreadLocal<InterProcessLock> LOCK_CONTEXT = new ThreadLocal<>();

    public static InterProcessLock getContext(){
        return LOCK_CONTEXT.get();
    }

    public static void setContext(InterProcessLock interProcessLock){
        LOCK_CONTEXT.set(interProcessLock);
    }

    public static void remove(){
        LOCK_CONTEXT.remove();
    }
}

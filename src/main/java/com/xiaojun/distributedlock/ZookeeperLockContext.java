/**
 * ﻿Copyright © 2018 organization 苞米豆
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

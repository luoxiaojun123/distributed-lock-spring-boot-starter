package com.xiaojun.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * zookeeper注入条件判断类
 * @author xiaojun
 * @date 2018/11/10 23:28
 */
public class ZookeeperCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().containsProperty("spring.zookeeper.zkServers");
    }
}

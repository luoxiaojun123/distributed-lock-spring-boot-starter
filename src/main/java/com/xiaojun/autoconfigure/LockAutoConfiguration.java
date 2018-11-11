package com.xiaojun.autoconfigure;

import com.xiaojun.aop.RedisAop;
import com.xiaojun.aop.ZookeeperAop;
import com.xiaojun.condition.ZookeeperCondition;
import com.xiaojun.distributedlock.RedisDistributedLock;
import com.xiaojun.distributedlock.ZookeeperDistributedLock;
import lombok.Data;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 分布式锁自动配置器
 *
 * @author xiaojun
 * @date 2018/11/10 23:27
 */
@Configuration
public class LockAutoConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<Object, Object> redisObjectTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<Object, Object> redisObjectTemplate = new RedisTemplate<>();

        redisObjectTemplate.setConnectionFactory(redisConnectionFactory);
        redisObjectTemplate.setKeySerializer(keySerializer());
        redisObjectTemplate.setValueSerializer(valueSerializer());
        redisObjectTemplate.setHashKeySerializer(keySerializer());
        redisObjectTemplate.setHashValueSerializer(valueSerializer());
        return redisObjectTemplate;
    }

    @Bean("redisDistributedLock")
    public RedisDistributedLock RedisDistributedLock(RedisTemplate redisObjectTemplate){
        return new RedisDistributedLock(redisObjectTemplate);
    }

    @Bean
    public RedisAop RedisAop(RedisDistributedLock redisDistributedLock){
        return new RedisAop(redisDistributedLock);
    }

    @Conditional(ZookeeperCondition.class)
    @ConfigurationProperties(prefix = "spring.zookeeper")
    @Data
    public class CoordinateConfiguration {
        private String zkServers;

        private int sessionTimeout = 30000;

        private int connectionTimeout = 5000;

        private int baseSleepTimeMs = 1000;

        private int maxRetries = 3;

        @Bean(destroyMethod = "close")
        public CuratorFramework curatorFramework() {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(this.baseSleepTimeMs, this.maxRetries);
            CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                    .connectString(this.zkServers)
                    .sessionTimeoutMs(this.sessionTimeout)
                    .connectionTimeoutMs(this.connectionTimeout)
                    .retryPolicy(retryPolicy)
                    .build();
            curatorFramework.start();
            return curatorFramework;
        }

        @Bean("zookeeperDistributedLock")
        public ZookeeperDistributedLock zookeeperDistributedLock(CuratorFramework curatorFramework) {
            return new ZookeeperDistributedLock(curatorFramework);
        }

        @Bean
        public ZookeeperAop zookeeperAop(ZookeeperDistributedLock zookeeperDistributedLock) {
            return new ZookeeperAop(zookeeperDistributedLock);
        }
    }


    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}

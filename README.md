# distributed-lock-spring-boot-starter
引入
        <dependency>
            <groupId>com.xiaojun</groupId>
            <artifactId>distributed-lock-spring-boot-starter</artifactId>
            <version>1.0.0</version>
        </dependency>
        
       <- zookeeper->
#配置 application.yml
 spring:
  zookeeper:
    zkServers: 127.0.0.1:2181
使用
    @ZookeeperLock(lockKey = "#user.id", prefix = "luo")
    @Override
    public void update(User user) {
        try {
            log.info("进入 update 方法");
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    <- redis->
配置 application.yml
 spring:
  redis:
    host: 127.0.0.1
    database: 1
    port: 6379
    lettuce:
      pool:
        max-active: 8 # 连接池最大连接数（使用负值表示没有限制） 默认 8
        #max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
        max-idle: 8 #连接池中的最大空闲连接 默认 8
        min-idle: 0 # 连接池中的最小空闲连接 默认 0
使用
    @RedisLock(lockKey = "#user.id", prefix = "luo")
    @Override
    public void update(User user) {
        try {
            log.info("进入 update 方法");
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

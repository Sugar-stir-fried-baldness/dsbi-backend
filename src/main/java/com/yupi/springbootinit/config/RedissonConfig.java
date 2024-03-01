package com.yupi.springbootinit.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:tzy
 * @Description :
 * @Date:2024/1/1517:22
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private Integer database;

    private String host;

    private Integer port;

    private String password;


    //spring在启动的时候自动创建这个RedissonClient对象
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
//        没有redis集群就用单机
        config.useSingleServer()
                .setDatabase(database)
                .setAddress("redis://"+host+":"+port);
//                .setPassword(password);

        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }
}

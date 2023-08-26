package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate redisTemplate (RedisConnectionFactory redisConnectionFactory) {
        log.info ("開始創建redis模板對象...");
        RedisTemplate redisTemplate = new RedisTemplate ();
        
        // 設置redis的連接工廠對象
        redisTemplate.setConnectionFactory (redisConnectionFactory);
        // 設置redis key序列化器
        redisTemplate.setKeySerializer (new StringRedisSerializer ());
        
        System.out.println ("redis:" + redisTemplate);
        // TODO: 2023/8/23 redis無法獲取鏈接，原因未知，已解決，在yml移除密碼配置
        System.out.println (redisTemplate.getConnectionFactory ().getConnection ());
        return redisTemplate;
    }
}

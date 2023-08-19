package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置類：
 * springboot加載時，先將配置文件的屬性賦值給OssProperties這個類，
 * 然後通過此配置類將上傳文件具體操作的工具類util對象進行賦值和創建
 */
@Configuration
@Slf4j
public class OssConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil (AliOssProperties aliOssProperties) {
        log.info ("開始創建阿里云文件上傳工具類對象：{}", aliOssProperties);
        return new AliOssUtil (aliOssProperties.getEndpoint (),
                aliOssProperties.getAccessKeyId (),
                aliOssProperties.getAccessKeySecret (),
                aliOssProperties.getBucketName ());
    }
}

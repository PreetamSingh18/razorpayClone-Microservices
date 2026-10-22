package com.preesa.razorpay.operations_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;

@Configuration 
public class SchedulerLockConfig {

    @Bean
    public LockProvider schedulerLockProvider(RedisConnectionFactory redisConnectionFactory){
        return new RedisLockProvider(redisConnectionFactory);
    }


}

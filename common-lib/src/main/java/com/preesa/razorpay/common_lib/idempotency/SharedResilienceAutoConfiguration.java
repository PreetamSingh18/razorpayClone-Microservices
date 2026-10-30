package com.preesa.razorpay.common_lib.idempotency;


import com.preesa.razorpay.common_lib.cache.RedisApiKeyCache;
import com.preesa.razorpay.common_lib.context.MerchantContext;
import com.preesa.razorpay.common_lib.ratelimit.RateLimiter;
import com.preesa.razorpay.common_lib.ratelimit.ratelimiterImpl.FixedWindowRateLimiter;
import com.preesa.razorpay.common_lib.ratelimit.ratelimiterImpl.SlidingWindowLuaScripter;
import com.preesa.razorpay.common_lib.ratelimit.ratelimiterImpl.SlidingWindowRateLimiter;
import com.preesa.razorpay.common_lib.ratelimit.ratelimiterImpl.TokenBucketRateLimiter;

import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;

@AutoConfiguration
public class SharedResilienceAutoConfiguration {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory){
     return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public IdempotencyStore idempotencyStore(StringRedisTemplate stringRedisTemplate) {
        return new RedisIdempotencyStore(stringRedisTemplate);
    }

    @Bean
    public IdempotencyFilter idempotencyFilter(MerchantContext merchantContext, IdempotencyStore idempotencyStore
            , @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        return new IdempotencyFilter(merchantContext, idempotencyStore, handlerExceptionResolver);
    }

    @Bean
    public RedisApiKeyCache redisApiKeyCache(ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate ){
        return new RedisApiKeyCache(stringRedisTemplate, objectMapper);

    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "fixed")
    public RateLimiter fixedWindowRateLimiter(StringRedisTemplate stringRedisTemplate) {
        return new FixedWindowRateLimiter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "sliding")
    public RateLimiter slidingWindowRateLimiter(StringRedisTemplate stringRedisTemplate) {
        return new SlidingWindowRateLimiter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "sliding-lua")
    public RateLimiter slidingWindowLuaScripter(StringRedisTemplate stringRedisTemplate) {
        return new SlidingWindowLuaScripter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "bucket")
    public RateLimiter tokenBucketRateLimiter(StringRedisTemplate stringRedisTemplate) {
        return new TokenBucketRateLimiter(stringRedisTemplate);
    }
}

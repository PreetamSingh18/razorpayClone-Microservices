package com.preesa.razorpay.common_lib.idempotency;


import com.preesa.razorpay.common_lib.context.MerchantContext;
import com.preesa.razorpay.common_lib.ratelimit.RateLimiter;
import com.preesa.razorpay.common_lib.ratelimit.ratelimiterImpl.FixedWindowRateLimiter;
import com.preesa.razorpay.common_lib.ratelimit.ratelimiterImpl.SlidingWindowLuaScripter;
import com.preesa.razorpay.common_lib.ratelimit.ratelimiterImpl.SlidingWindowRateLimiter;
import com.preesa.razorpay.common_lib.ratelimit.ratelimiterImpl.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;

@AutoConfiguration
public class SharedResilienceAutoConfiguration {

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate();
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

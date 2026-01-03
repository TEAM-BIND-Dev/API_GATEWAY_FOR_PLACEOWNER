package com.teambind.springproject.ratelimit;

import com.teambind.springproject.config.properties.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class    RateLimiterService {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterService.class);

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<List<Long>> rateLimitScript;

    public RateLimiterService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.rateLimitScript = createRateLimitScript();
    }

    /**
     * Token Bucket 알고리즘 기반 Rate Limiting
     * Redis Lua 스크립트로 원자적 연산 보장
     */
    public Mono<RateLimitResult> isAllowed(String key, RateLimitProperties.Policy policy) {
        String redisKey = "rate_limit:" + key;
        long now = System.currentTimeMillis();
        int burst = policy.getBurstCapacity();
        double refillRate = policy.getRefillRate();
        long windowSeconds = policy.getDuration().toSeconds();

        return redisTemplate.execute(
                        rateLimitScript,
                        List.of(redisKey),
                        String.valueOf(now),
                        String.valueOf(burst),
                        String.valueOf(refillRate),
                        String.valueOf(windowSeconds * 2) // TTL
                )
                .single()
                .map(result -> {
                    long allowed = result.get(0);
                    long remaining = result.get(1);
                    long resetAfter = result.get(2);

                    if (allowed == 1) {
                        return RateLimitResult.allowed(remaining, resetAfter);
                    } else {
                        log.warn("Rate limit exceeded for key: {}", key);
                        return RateLimitResult.denied(resetAfter);
                    }
                })
                .onErrorResume(e -> {
                    log.error("Rate limiter error, allowing request: {}", e.getMessage());
                    return Mono.just(RateLimitResult.allowed(-1, -1));
                });
    }

    private RedisScript<List<Long>> createRateLimitScript() {
        String script = """
                local key = KEYS[1]
                local now = tonumber(ARGV[1])
                local burst = tonumber(ARGV[2])
                local refill_rate = tonumber(ARGV[3])
                local ttl = tonumber(ARGV[4])

                local data = redis.call('HMGET', key, 'tokens', 'last_update')
                local tokens = tonumber(data[1])
                local last_update = tonumber(data[2])

                if tokens == nil then
                    tokens = burst
                    last_update = now
                end

                local elapsed = (now - last_update) / 1000.0
                local refill = elapsed * refill_rate
                tokens = math.min(burst, tokens + refill)

                local allowed = 0
                local remaining = tokens
                local reset_after = 0

                if tokens >= 1 then
                    tokens = tokens - 1
                    remaining = tokens
                    allowed = 1
                else
                    reset_after = math.ceil((1 - tokens) / refill_rate)
                end

                redis.call('HMSET', key, 'tokens', tokens, 'last_update', now)
                redis.call('EXPIRE', key, ttl)

                return {allowed, math.floor(remaining), reset_after}
                """;

        return RedisScript.of(script, (Class<List<Long>>) (Class<?>) List.class);
    }
}

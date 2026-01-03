package com.teambind.springproject.ratelimit;

public record RateLimitResult(
        boolean allowed,
        long remainingTokens,
        long resetAfterSeconds
) {
    public static RateLimitResult allowed(long remaining, long resetAfter) {
        return new RateLimitResult(true, remaining, resetAfter);
    }

    public static RateLimitResult denied(long resetAfter) {
        return new RateLimitResult(false, 0, resetAfter);
    }
}

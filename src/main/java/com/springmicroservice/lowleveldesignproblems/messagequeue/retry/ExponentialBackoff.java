package com.springmicroservice.lowleveldesignproblems.messagequeue.retry;

/**
 * Exponential backoff: 1s → 2s → 4s → 8s..., capped at maxDelayMs.
 */
public class ExponentialBackoff implements BackoffStrategy {

    private final long initialDelayMs;
    private final double multiplier;
    private final long maxDelayMs;

    public ExponentialBackoff() {
        this(1000, 2.0, 30_000);
    }

    public ExponentialBackoff(long initialDelayMs, double multiplier, long maxDelayMs) {
        this.initialDelayMs = initialDelayMs;
        this.multiplier = multiplier;
        this.maxDelayMs = maxDelayMs;
    }

    @Override
    public long getDelayMs(int attempt) {
        long delay = (long) (initialDelayMs * Math.pow(multiplier, attempt));
        return Math.min(delay, maxDelayMs);
    }
}

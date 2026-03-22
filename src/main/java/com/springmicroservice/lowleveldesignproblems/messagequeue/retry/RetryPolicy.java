package com.springmicroservice.lowleveldesignproblems.messagequeue.retry;

/**
 * Executes an action with retry and backoff on failure.
 */
public class RetryPolicy {

    private final int maxRetries;
    private final BackoffStrategy backoff;

    public RetryPolicy(int maxRetries, BackoffStrategy backoff) {
        this.maxRetries = maxRetries;
        this.backoff = backoff;
    }

    public static RetryPolicy withExponentialBackoff(int maxRetries) {
        return new RetryPolicy(maxRetries, new ExponentialBackoff());
    }

    /**
     * Run the action. Retries on exception with backoff. Rethrows after max retries exhausted.
     */
    public void execute(ThrowingRunnable action) throws Exception {
        Exception lastException = null;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                action.run();
                return;
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxRetries) {
                    long delayMs = backoff.getDelayMs(attempt);
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry backoff", ie);
                    }
                }
            }
        }
        if (lastException != null) {
            throw lastException;
        }
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}

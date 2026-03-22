package com.springmicroservice.lowleveldesignproblems.messagequeue.retry;

/**
 * Strategy for computing delay before next retry attempt.
 */
public interface BackoffStrategy {

    /**
     * @param attempt Zero-based attempt number (0 = first retry)
     * @return Delay in milliseconds
     */
    long getDelayMs(int attempt);
}

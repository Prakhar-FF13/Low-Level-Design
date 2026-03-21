package com.springmicroservice.lowleveldesignproblems.moviecms.services;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Holds cache hit/miss statistics for analytics.
 */
@Data
@AllArgsConstructor
public class CacheAnalytics {
    private long hitCount;
    private long missCount;

    public double getHitRate() {
        long total = hitCount + missCount;
        return total == 0 ? 0.0 : (double) hitCount / total;
    }
}

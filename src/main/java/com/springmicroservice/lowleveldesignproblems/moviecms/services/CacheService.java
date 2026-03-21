package com.springmicroservice.lowleveldesignproblems.moviecms.services;

import java.util.List;
import java.util.function.Supplier;

import com.springmicroservice.lowleveldesignproblems.moviecms.cache.MultiLevelCache;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.Movie;

/**
 * CacheService facade over MultiLevelCache.
 * Provides: cached search via L1→L2→loader, analytics, maintenance (clear).
 */
public class CacheService {
    private final MultiLevelCache multiLevelCache;

    public CacheService(MultiLevelCache multiLevelCache) {
        this.multiLevelCache = multiLevelCache;
    }

    /**
     * Gets search results via L1 → L2 → loader.
     * On miss, loader is invoked and result is backfilled.
     */
    public List<Movie> get(String userId, String cacheKey, Supplier<List<Movie>> loader) {
        return multiLevelCache.get(userId, cacheKey, loader);
    }

    public void clear() {
        multiLevelCache.clear();
    }

    public CacheAnalytics getAnalytics() {
        return multiLevelCache.getAnalytics();
    }

    public void resetAnalytics() {
        multiLevelCache.resetAnalytics();
    }
}

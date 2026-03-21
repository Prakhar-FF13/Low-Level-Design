package com.springmicroservice.lowleveldesignproblems.moviecms.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.springmicroservice.lowleveldesignproblems.moviecms.models.Movie;
import com.springmicroservice.lowleveldesignproblems.moviecms.services.CacheAnalytics;

/**
 * Multi-level cache: L1 (5 per user) → L2 (20 global) → loader (primary store).
 * Implements cache hierarchy from design requirements.
 */
public class MultiLevelCache {
    private static final int L1_CAPACITY = 5;
    private static final int L2_CAPACITY = 20;

    private final Map<String, LRUCache<String, List<Movie>>> l1ByUser;
    private final LRUCache<String, List<Movie>> l2;

    private long hitCount;
    private long missCount;

    public MultiLevelCache() {
        this.l1ByUser = new ConcurrentHashMap<>();
        this.l2 = new LRUCache<>(L2_CAPACITY);
        this.hitCount = 0;
        this.missCount = 0;
    }

    /**
     * Gets from L1 (user-specific) → L2 (global) → loader.
     * On miss, calls loader and backfills L2 then L1.
     */
    public List<Movie> get(String userId, String cacheKey, Supplier<List<Movie>> loader) {
        LRUCache<String, List<Movie>> l1 = l1ByUser.computeIfAbsent(userId, k -> new LRUCache<>(L1_CAPACITY));

        List<Movie> result = l1.get(cacheKey);
        if (result != null) {
            hitCount++;
            return result;
        }

        result = l2.get(cacheKey);
        if (result != null) {
            hitCount++;
            l1.put(cacheKey, result);
            return result;
        }

        missCount++;
        result = loader.get();
        l2.put(cacheKey, result);
        l1.put(cacheKey, result);
        return result;
    }

    public void clear() {
        l1ByUser.clear();
        l2.clear();
    }

    public CacheAnalytics getAnalytics() {
        return new CacheAnalytics(hitCount, missCount);
    }

    public void resetAnalytics() {
        hitCount = 0;
        missCount = 0;
    }
}

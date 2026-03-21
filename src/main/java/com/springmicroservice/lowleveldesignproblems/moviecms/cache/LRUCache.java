package com.springmicroservice.lowleveldesignproblems.moviecms.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU (Least Recently Used) cache with fixed capacity.
 * Evicts oldest accessed entry when full.
 */
public class LRUCache<K, V> {
    private final int maxSize;
    private final Map<K, V> cache;

    public LRUCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }
        this.maxSize = maxSize;
        this.cache = Collections.synchronizedMap(new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCache.this.maxSize;
            }
        });
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}

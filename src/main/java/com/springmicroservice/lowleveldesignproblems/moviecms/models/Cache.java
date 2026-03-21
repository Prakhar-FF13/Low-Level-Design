package com.springmicroservice.lowleveldesignproblems.moviecms.models;

import java.util.HashMap;
import java.util.Map;

public class Cache<V> {
    private Map<String, CacheEntry<V>> cache;

    public Cache() {
        this.cache = new HashMap<String, CacheEntry<V>>();
    }

    public void put(String key, V value, Long expiryTime) {
        cache.put(key, new CacheEntry<>(value, expiryTime));
    }

    public V get(String key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.getValue();
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}

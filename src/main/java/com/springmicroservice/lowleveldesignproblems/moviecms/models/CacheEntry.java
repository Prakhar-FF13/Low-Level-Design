package com.springmicroservice.lowleveldesignproblems.moviecms.models;

import lombok.Data;

@Data
public class CacheEntry<V> {
    private V value;
    private Long expiryTime;

    public CacheEntry(V value, Long expiryTime) {
        this.value = value;
        if (expiryTime != null && expiryTime > 0) {
            this.expiryTime = System.currentTimeMillis() + expiryTime;
        } else {
            this.expiryTime = null;
        }
    }

    public boolean isExpired() {
        if (expiryTime == null) {
            return false;
        }
        return System.currentTimeMillis() > expiryTime;
    }
}

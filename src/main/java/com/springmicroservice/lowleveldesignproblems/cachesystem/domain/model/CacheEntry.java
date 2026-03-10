package com.springmicroservice.lowleveldesignproblems.cachesystem.domain.model;

/**
 * A wrapper to hold in-memory cache values with their explicit expiry
 * mechanisms.
 * Allows decoupling TTL details from the user's plain actual `V` value.
 */
public class CacheEntry<V> {
  private final V value;
  private final Long expiryTime;

  /**
   * @param value       Cache payload.
   * @param ttlInMillis Time to live in milliseconds from when this object is
   *                    instantiated.
   *                    If ttlInMillis is null or <= 0, the entry never expires.
   */
  public CacheEntry(V value, Long ttlInMillis) {
    this.value = value;
    if (ttlInMillis != null && ttlInMillis > 0) {
      this.expiryTime = System.currentTimeMillis() + ttlInMillis;
    } else {
      this.expiryTime = null; // No expiry
    }
  }

  /**
   * Reconstruct from DB.
   * 
   * @param value           Cache payload.
   * @param exactExpiryTime Exact expiry time stamp.
   */
  public CacheEntry(V value, Long exactExpiryTime, boolean isExactTime) {
    this.value = value;
    this.expiryTime = exactExpiryTime;
  }

  public V getValue() {
    return value;
  }

  public Long getExpiryTime() {
    return expiryTime;
  }

  public boolean isExpired() {
    if (expiryTime == null) {
      return false;
    }
    return System.currentTimeMillis() > expiryTime;
  }
}

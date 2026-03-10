package com.springmicroservice.lowleveldesignproblems.cachesystem.api;

import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy.EvictionPolicy;

import java.util.Optional;

public interface Cache<K, V> {

  /**
   * Associates the specified value with the specified key in this cache.
   * 
   * @param key   key with which the specified value is to be associated
   * @param value value to be associated with the specified key
   */
  void put(K key, V value);

  /**
   * Returns the value to which the specified key is mapped, or Optional.empty()
   * if this cache contains no mapping for the key or it has expired.
   * 
   * @param key the key whose associated value is to be returned
   * @return an Optional containing the cached value, or empty if not
   *         found/expired
   */
  Optional<V> get(K key);

  /**
   * Retrieves the metric of size for unit testing / health checking.
   */
  int size();

  /**
   * Dynamically swaps the eviction strategy without dropping the cache items.
   * 
   * @param newPolicy The new Policy Strategy to enforce.
   */
  void changeEvictionPolicy(EvictionPolicy<K> newPolicy);

  /**
   * Completely destroys the cache wiping memory, threads, and persistent states
   * tied to it.
   */
  void clear();
}

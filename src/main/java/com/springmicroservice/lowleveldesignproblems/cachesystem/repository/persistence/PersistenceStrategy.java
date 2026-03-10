package com.springmicroservice.lowleveldesignproblems.cachesystem.repository.persistence;

import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.model.CacheEntity;

public interface PersistenceStrategy {

  /**
   * Called when a key-value is inserted or updated in cache.
   * 
   * @param entity Entity representation matching the DB schema.
   */
  void save(CacheEntity entity);

  /**
   * Called when a key is removed from cache (either via manual removal or
   * eviction).
   * 
   * @param key The key to remove from the persistent store.
   */
  void delete(String key);

  /**
   * Shuts down any background processes tied to the persistence strategy.
   */
  void shutdown();
}

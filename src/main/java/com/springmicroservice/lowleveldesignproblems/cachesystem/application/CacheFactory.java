package com.springmicroservice.lowleveldesignproblems.cachesystem.application;

import com.springmicroservice.lowleveldesignproblems.cachesystem.api.Cache;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy.EvictionPolicy;
import com.springmicroservice.lowleveldesignproblems.cachesystem.repository.persistence.PersistenceStrategy;
import com.springmicroservice.lowleveldesignproblems.cachesystem.repository.storage.InMemoryStorage;

public class CacheFactory<K, V> {

  public Cache<K, V> createCache(int capacity,
      EvictionPolicy<K> evictionPolicy,
      PersistenceStrategy persistenceStrategy,
      Long defaultTtlInMillis,
      boolean startTtlReaper) {

    return new CacheImpl<>(
        new InMemoryStorage<>(capacity),
        evictionPolicy,
        persistenceStrategy,
        defaultTtlInMillis,
        startTtlReaper);
  }

}

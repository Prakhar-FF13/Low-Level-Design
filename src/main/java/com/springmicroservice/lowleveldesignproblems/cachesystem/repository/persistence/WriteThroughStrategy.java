package com.springmicroservice.lowleveldesignproblems.cachesystem.repository.persistence;

import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.model.CacheEntity;
import lombok.RequiredArgsConstructor;

/**
 * Synchronous write execution strategy. Ensures that the data is firmly
 * planted in the H2 Database before continuing. Offers strong consistency.
 */
@RequiredArgsConstructor
public class WriteThroughStrategy implements PersistenceStrategy {

  private final CacheRepository cacheRepository;

  @Override
  public void save(CacheEntity entity) {
    if (cacheRepository != null) {
      cacheRepository.save(entity);
    }
  }

  @Override
  public void delete(String key) {
    if (cacheRepository != null && cacheRepository.existsById(key)) {
      cacheRepository.deleteById(key);
    }
  }

  @Override
  public void shutdown() {
    // No threads to shut down
  }
}

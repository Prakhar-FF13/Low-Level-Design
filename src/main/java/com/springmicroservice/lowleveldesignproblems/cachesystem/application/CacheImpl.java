package com.springmicroservice.lowleveldesignproblems.cachesystem.application;

import com.springmicroservice.lowleveldesignproblems.cachesystem.api.Cache;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.exception.NotFoundException;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.model.CacheEntity;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.model.CacheEntry;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy.EvictionPolicy;
import com.springmicroservice.lowleveldesignproblems.cachesystem.repository.persistence.PersistenceStrategy;
import com.springmicroservice.lowleveldesignproblems.cachesystem.repository.storage.Storage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class CacheImpl<K, V> implements Cache<K, V> {

  private final Storage<K, CacheEntry<V>> storage;
  private EvictionPolicy<K> evictionPolicy;
  private final PersistenceStrategy persistenceStrategy;

  // A single ReentrantReadWriteLock orchestrates safe concurrent access.
  // get() and put() mutate the eviction policy (e.g., LRU movement),
  // so both primarily demand a write-lock to ensure data integrity.
  // However, if we only check expiry, a read-lock is faster.
  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private final Long defaultTtlInMillis;

  public CacheImpl(Storage<K, CacheEntry<V>> storage,
      EvictionPolicy<K> evictionPolicy,
      PersistenceStrategy persistenceStrategy,
      Long defaultTtlInMillis,
      boolean startTtlReaper) {
    this.storage = storage;
    this.evictionPolicy = evictionPolicy;
    this.persistenceStrategy = persistenceStrategy;
    this.defaultTtlInMillis = defaultTtlInMillis;

    if (startTtlReaper) {
      startTtlCleanupTask();
    }
  }

  @Override
  public void put(K key, V value) {
    lock.writeLock().lock();
    try {
      CacheEntry<V> entry = new CacheEntry<>(value, defaultTtlInMillis);

      try {
        // If it exists, we just update it
        storage.get(key);
        storage.add(key, entry);
        evictionPolicy.keyAccessed(key);
      } catch (NotFoundException e) {
        // It's a new entry
        if (storage.isFull()) {
          K victimKey = evictionPolicy.evictKey();
          if (victimKey != null) {
            try {
              storage.remove(victimKey);
              persistenceStrategy.delete(String.valueOf(victimKey));
              log.debug("Evicted key {} due to capacity limit under policy {}", victimKey,
                  evictionPolicy.getPolicyType());
            } catch (NotFoundException ex) {
              log.error("Victim key in policy not found in storage. Data mismatch!");
            }
          }
        }
        storage.add(key, entry);
        evictionPolicy.keyAccessed(key);
      }

      // Sync to persistence
      persistenceStrategy.save(CacheEntity.builder()
          .id(String.valueOf(key))
          .value(String.valueOf(value)) // Mock Serialization
          .expiryTime(entry.getExpiryTime())
          .build());

    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public Optional<V> get(K key) {
    lock.writeLock().lock(); // Using write lock since accessing mutates the eviction policy structure
    try {
      CacheEntry<V> entry = storage.get(key);

      if (entry.isExpired()) {
        log.debug("Key {} is expired. Evicting lazily.", key);
        storage.remove(key);
        evictionPolicy.removeKey(key);
        persistenceStrategy.delete(String.valueOf(key));
        return Optional.empty();
      }

      evictionPolicy.keyAccessed(key);
      return Optional.of(entry.getValue());

    } catch (NotFoundException e) {
      return Optional.empty();
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public int size() {
    lock.readLock().lock();
    try {
      return storage.getAllKeys().size();
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void changeEvictionPolicy(EvictionPolicy<K> newPolicy) {
    lock.writeLock().lock();
    try {
      log.info("Changing Eviction Policy from {} to {}", this.evictionPolicy.getPolicyType(),
          newPolicy.getPolicyType());
      List<K> allKeys = storage.getAllKeys();
      for (K key : allKeys) {
        newPolicy.keyAccessed(key);
      }
      this.evictionPolicy.clear();
      this.evictionPolicy = newPolicy;
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void clear() {
    lock.writeLock().lock();
    try {
      storage.clear();
      evictionPolicy.clear();
      persistenceStrategy.shutdown();
    } finally {
      lock.writeLock().unlock();
    }
  }

  private void startTtlCleanupTask() {
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r);
      t.setDaemon(true);
      t.setName("TTL-Reaper");
      return t;
    });

    // Run every 5 seconds to wipe dead keys proactively
    executor.scheduleAtFixedRate(() -> {
      lock.writeLock().lock();
      try {
        List<K> keys = storage.getAllKeys();
        for (K key : keys) {
          try {
            CacheEntry<V> entry = storage.get(key);
            if (entry.isExpired()) {
              storage.remove(key);
              evictionPolicy.removeKey(key);
              persistenceStrategy.delete(String.valueOf(key));
              log.debug("TTL Reaper dropped abandoned key {}", key);
            }
          } catch (NotFoundException ignored) {
          }
        }
      } finally {
        lock.writeLock().unlock();
      }
    }, 5, 5, TimeUnit.SECONDS);
  }
}

package com.springmicroservice.lowleveldesignproblems.cachesystem;

import com.springmicroservice.lowleveldesignproblems.cachesystem.api.Cache;
import com.springmicroservice.lowleveldesignproblems.cachesystem.application.CacheFactory;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy.LRUEvictionPolicy;
import com.springmicroservice.lowleveldesignproblems.cachesystem.repository.persistence.PersistenceStrategy;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.model.CacheEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheImplTest {

  private Cache<Integer, String> cache;
  private MockPersistence mockPersistence;

  static class MockPersistence implements PersistenceStrategy {
    AtomicInteger saves = new AtomicInteger();
    AtomicInteger deletes = new AtomicInteger();

    @Override
    public void save(CacheEntity entity) {
      saves.incrementAndGet();
    }

    @Override
    public void delete(String key) {
      deletes.incrementAndGet();
    }

    @Override
    public void shutdown() {
    }
  }

  @BeforeEach
  void setup() {
    mockPersistence = new MockPersistence();
    CacheFactory<Integer, String> factory = new CacheFactory<>();
    // Cap of 3, LRU, no TTL
    cache = factory.createCache(3, new LRUEvictionPolicy<>(), mockPersistence, null, false);
  }

  @Test
  void testBasicPutAndGet() {
    cache.put(1, "One");
    cache.put(2, "Two");

    assertEquals(Optional.of("One"), cache.get(1));
    assertEquals(2, cache.size());
    assertEquals(2, mockPersistence.saves.get());
  }

  @Test
  void testEvictionTrigger() {
    cache.put(1, "A");
    cache.put(2, "B");
    cache.put(3, "C");
    cache.put(4, "D"); // Modifies cap, triggers eviction of 1

    assertEquals(Optional.empty(), cache.get(1));
    assertEquals(Optional.of("B"), cache.get(2));
    assertEquals(Optional.of("D"), cache.get(4));
    assertEquals(3, cache.size());

    // 4 saves, 1 delete
    assertEquals(4, mockPersistence.saves.get());
    assertEquals(1, mockPersistence.deletes.get());
  }

  @Test
  void testThreadSafety() throws InterruptedException {
    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      final int key = i % 5; // Contentious keys
      executorService.submit(() -> {
        cache.put(key, "Value " + key);
        cache.get(key);
        latch.countDown();
      });
    }

    latch.await(5, TimeUnit.SECONDS);
    executorService.shutdown();

    assertTrue(cache.size() <= 3); // Max capacity holds
  }
}

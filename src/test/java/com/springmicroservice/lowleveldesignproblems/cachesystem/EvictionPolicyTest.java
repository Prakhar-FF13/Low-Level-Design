package com.springmicroservice.lowleveldesignproblems.cachesystem;

import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy.EvictionPolicy;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy.FIFOEvictionPolicy;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy.LFUEvictionPolicy;
import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy.LRUEvictionPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EvictionPolicyTest {

  @Test
  void testLRUPolicy() {
    EvictionPolicy<Integer> lru = new LRUEvictionPolicy<>();

    lru.keyAccessed(1);
    lru.keyAccessed(2);
    lru.keyAccessed(3);

    // 1 is LRU
    assertEquals(1, lru.evictKey());

    lru.keyAccessed(2); // 2 becomes MRU, 3 is LRU
    assertEquals(3, lru.evictKey());
    assertEquals(2, lru.evictKey());
    assertNull(lru.evictKey());
  }

  @Test
  void testLFUPolicy() {
    EvictionPolicy<Integer> lfu = new LFUEvictionPolicy<>();

    lfu.keyAccessed(1);
    lfu.keyAccessed(1); // Freq 2
    lfu.keyAccessed(2); // Freq 1
    lfu.keyAccessed(3); // Freq 1

    // Either 2 or 3 can be evicted. In our LFU ties are resolved by LRU property
    // due to addNodeToHead and removeTail
    Integer victim = lfu.evictKey();
    assertEquals(2, victim); // 2 was added before 3 at freq 1

    assertEquals(3, lfu.evictKey());
    assertEquals(1, lfu.evictKey());
    assertNull(lfu.evictKey());
  }

  @Test
  void testFIFOPolicy() {
    EvictionPolicy<Integer> fifo = new FIFOEvictionPolicy<>();

    fifo.keyAccessed(1);
    fifo.keyAccessed(2);
    fifo.keyAccessed(1); // FIFO does not care about recency
    fifo.keyAccessed(3);

    assertEquals(1, fifo.evictKey());
    assertEquals(2, fifo.evictKey());
    assertEquals(3, fifo.evictKey());
    assertNull(fifo.evictKey());
  }
}

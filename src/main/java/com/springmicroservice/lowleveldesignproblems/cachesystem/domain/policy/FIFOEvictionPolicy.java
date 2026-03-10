package com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy;

import java.util.LinkedHashSet;

public class FIFOEvictionPolicy<K> implements EvictionPolicy<K> {

  // LinkedHashSet maintains insertion order.
  // Keys added first will be at the front of the iterator.
  private final LinkedHashSet<K> queue;

  public FIFOEvictionPolicy() {
    this.queue = new LinkedHashSet<>();
  }

  @Override
  public void keyAccessed(K key) {
    // For FIFO, access doesn't change eviction position unless it's a new insertion
    if (!queue.contains(key)) {
      queue.add(key);
    }
  }

  @Override
  public K evictKey() {
    if (queue.isEmpty()) {
      return null;
    }
    // Grab the first element inserted
    K first = queue.iterator().next();
    queue.remove(first);
    return first;
  }

  @Override
  public void removeKey(K key) {
    queue.remove(key);
  }

  @Override
  public void clear() {
    queue.clear();
  }

  @Override
  public String getPolicyType() {
    return "FIFO";
  }
}

package com.springmicroservice.lowleveldesignproblems.cachesystem.repository.storage;

import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage<K, V> implements Storage<K, V> {

  private final Map<K, V> storage;
  private final int capacity;

  public InMemoryStorage(int capacity) {
    this.capacity = capacity;
    // Allows iteration while modifications happen, good for cache metrics or TTL
    // Reaper.
    this.storage = new ConcurrentHashMap<>();
  }

  @Override
  public void add(K key, V value) {
    storage.put(key, value);
  }

  @Override
  public void remove(K key) throws NotFoundException {
    if (!storage.containsKey(key)) {
      throw new NotFoundException("Key " + key + " not present in storage to remove.");
    }
    storage.remove(key);
  }

  @Override
  public V get(K key) throws NotFoundException {
    if (!storage.containsKey(key)) {
      throw new NotFoundException("Key " + key + " not present in storage.");
    }
    return storage.get(key);
  }

  @Override
  public boolean isFull() {
    return storage.size() == capacity;
  }

  @Override
  public void clear() {
    storage.clear();
  }

  @Override
  public List<K> getAllKeys() {
    return new ArrayList<>(storage.keySet());
  }
}

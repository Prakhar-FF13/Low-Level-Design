package com.springmicroservice.lowleveldesignproblems.cachesystem.repository.storage;

import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.exception.NotFoundException;

import java.util.List;

/**
 * Handles underlying volatile storage mappings (usually an internal hash map).
 * Separates storage capacity sizing and element retrieval from policy
 * decisions.
 */
public interface Storage<K, V> {

  void add(K key, V value);

  void remove(K key) throws NotFoundException;

  V get(K key) throws NotFoundException;

  boolean isFull();

  void clear();

  List<K> getAllKeys();
}

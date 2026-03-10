package com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy;

import java.util.HashMap;
import java.util.Map;

public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {

  private static class Node<K> {
    K key;
    Node<K> prev;
    Node<K> next;

    public Node(K key) {
      this.key = key;
    }
  }

  private final Node<K> head;
  private final Node<K> tail;
  private final Map<K, Node<K>> nodeMap;

  public LRUEvictionPolicy() {
    this.nodeMap = new HashMap<>();
    this.head = new Node<>(null);
    this.tail = new Node<>(null);
    head.next = tail;
    tail.prev = head;
  }

  @Override
  public void keyAccessed(K key) {
    if (nodeMap.containsKey(key)) {
      Node<K> node = nodeMap.get(key);
      removeNode(node);
      addNodeToHead(node);
    } else {
      Node<K> newNode = new Node<>(key);
      nodeMap.put(key, newNode);
      addNodeToHead(newNode);
    }
  }

  @Override
  public K evictKey() {
    if (nodeMap.isEmpty()) {
      return null;
    }
    // Evict LRU which is at the tail
    Node<K> lruNode = tail.prev;
    removeNode(lruNode);
    nodeMap.remove(lruNode.key);
    return lruNode.key;
  }

  @Override
  public void removeKey(K key) {
    if (nodeMap.containsKey(key)) {
      Node<K> node = nodeMap.get(key);
      removeNode(node);
      nodeMap.remove(key);
    }
  }

  @Override
  public void clear() {
    nodeMap.clear();
    head.next = tail;
    tail.prev = head;
  }

  @Override
  public String getPolicyType() {
    return "LRU";
  }

  // Helper methods
  private void removeNode(Node<K> node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
  }

  private void addNodeToHead(Node<K> node) {
    node.next = head.next;
    node.prev = head;
    head.next.prev = node;
    head.next = node;
  }
}

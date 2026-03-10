package com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy;

import java.util.HashMap;
import java.util.Map;

public class LFUEvictionPolicy<K> implements EvictionPolicy<K> {

  private static class Node<K> {
    K key;
    int frequency;
    Node<K> prev;
    Node<K> next;

    public Node(K key) {
      this.key = key;
      this.frequency = 1;
    }
  }

  private static class DoublyLinkedList<K> {
    Node<K> head;
    Node<K> tail;
    int size;

    public DoublyLinkedList() {
      head = new Node<>(null);
      tail = new Node<>(null);
      head.next = tail;
      tail.prev = head;
      size = 0;
    }

    public void addNodeToHead(Node<K> node) {
      node.next = head.next;
      node.prev = head;
      head.next.prev = node;
      head.next = node;
      size++;
    }

    public void removeNode(Node<K> node) {
      node.prev.next = node.next;
      node.next.prev = node.prev;
      size--;
    }

    public Node<K> removeTail() {
      if (size > 0) {
        Node<K> node = tail.prev;
        removeNode(node);
        return node;
      }
      return null;
    }
  }

  private final Map<K, Node<K>> keyNodeMap;
  private final Map<Integer, DoublyLinkedList<K>> frequencyListMap;
  private int minFrequency;

  public LFUEvictionPolicy() {
    this.keyNodeMap = new HashMap<>();
    this.frequencyListMap = new HashMap<>();
    this.minFrequency = 0;
  }

  @Override
  public void keyAccessed(K key) {
    if (keyNodeMap.containsKey(key)) {
      Node<K> node = keyNodeMap.get(key);
      updateNodeFrequency(node);
    } else {
      Node<K> newNode = new Node<>(key);
      keyNodeMap.put(key, newNode);
      minFrequency = 1;
      frequencyListMap.computeIfAbsent(1, k -> new DoublyLinkedList<>()).addNodeToHead(newNode);
    }
  }

  private void updateNodeFrequency(Node<K> node) {
    int currentFreq = node.frequency;
    DoublyLinkedList<K> currentList = frequencyListMap.get(currentFreq);
    currentList.removeNode(node);

    if (currentFreq == minFrequency && currentList.size == 0) {
      minFrequency++;
    }

    node.frequency++;
    frequencyListMap.computeIfAbsent(node.frequency, k -> new DoublyLinkedList<>()).addNodeToHead(node);
  }

  @Override
  public K evictKey() {
    if (keyNodeMap.isEmpty()) {
      return null;
    }

    DoublyLinkedList<K> minFreqList = frequencyListMap.get(minFrequency);
    while (minFreqList == null || minFreqList.size == 0) {
      minFrequency++;
      minFreqList = frequencyListMap.get(minFrequency);
    }
    Node<K> evictedNode = minFreqList.removeTail();
    keyNodeMap.remove(evictedNode.key);
    return evictedNode.key;
  }

  @Override
  public void removeKey(K key) {
    if (keyNodeMap.containsKey(key)) {
      Node<K> node = keyNodeMap.get(key);
      DoublyLinkedList<K> list = frequencyListMap.get(node.frequency);
      if (list != null) {
        list.removeNode(node);
      }
      keyNodeMap.remove(key);
    }
  }

  @Override
  public void clear() {
    keyNodeMap.clear();
    frequencyListMap.clear();
    minFrequency = 0;
  }

  @Override
  public String getPolicyType() {
    return "LFU";
  }
}

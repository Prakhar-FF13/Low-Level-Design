package com.springmicroservice.lowleveldesignproblems.cachesystem.domain.policy;

/**
 * Describes the eviction strategy that the cache adheres to. Every policy must
 * decide which element is the most appropriate victim when eviction happens.
 * 
 * @param <K> Type of the Cache Key.
 */
public interface EvictionPolicy<K> {

  /**
   * Informs the policy that a key has been accessed (either read or inserted).
   * 
   * @param key Key that has been accessed.
   */
  void keyAccessed(K key);

  /**
   * Determines which key needs to be evicted based on the rules of the specific
   * policy implementation.
   * 
   * @return The victim key. Null if policy is empty.
   */
  K evictKey();

  /**
   * Removes the key from tracking. Used when a key is manually removed or expired
   * (TTL).
   * 
   * @param key Key to forget from eviction consideration.
   */
  void removeKey(K key);

  /**
   * Resets the policy tracking mechanism.
   */
  void clear();

  /**
   * Retrieves the specific type of policy for auditing or dynamic swapping logic.
   * 
   * @return policy identifier
   */
  String getPolicyType();
}

package com.springmicroservice.lowleveldesignproblems.cachesystem.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity object matching an H2 database schema for Cache persistence.
 * String is chosen for key and value to remain generic enough for
 * serialization,
 * though a custom byte[] lob could be used for generic objects.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheEntity {

  @Id
  private String id; // The cache key

  @Lob
  private String value; // Serialized value

  private Long expiryTime; // Exact epoch millisecond timestamp indicating when it expires
}

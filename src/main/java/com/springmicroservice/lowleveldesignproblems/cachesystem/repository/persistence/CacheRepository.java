package com.springmicroservice.lowleveldesignproblems.cachesystem.repository.persistence;

import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.model.CacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheRepository extends JpaRepository<CacheEntity, String> {
}

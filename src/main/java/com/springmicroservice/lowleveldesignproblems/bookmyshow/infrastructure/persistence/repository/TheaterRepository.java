package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.TheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<TheaterEntity, Long> {
}

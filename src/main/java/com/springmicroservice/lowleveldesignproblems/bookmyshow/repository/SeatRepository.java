package com.springmicroservice.lowleveldesignproblems.bookmyshow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.SeatEntity;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
}
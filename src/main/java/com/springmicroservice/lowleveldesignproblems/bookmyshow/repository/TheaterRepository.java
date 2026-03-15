package com.springmicroservice.lowleveldesignproblems.bookmyshow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.TheaterEntity;

@Repository
public interface TheaterRepository extends JpaRepository<TheaterEntity, Long> {
}
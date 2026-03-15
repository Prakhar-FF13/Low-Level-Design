package com.springmicroservice.lowleveldesignproblems.bookmyshow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.ShowEntity;

@Repository
public interface ShowRepository extends JpaRepository<ShowEntity, Long> {
}
package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
}

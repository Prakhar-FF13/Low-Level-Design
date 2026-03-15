package com.springmicroservice.lowleveldesignproblems.bookmyshow.repository;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.ShowSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeatEntity, Long> {

    List<ShowSeatEntity> findByShowShowIdAndStatus(Long showId, SeatStatus status);
}
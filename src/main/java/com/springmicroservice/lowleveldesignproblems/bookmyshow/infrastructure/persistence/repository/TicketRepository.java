package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
}

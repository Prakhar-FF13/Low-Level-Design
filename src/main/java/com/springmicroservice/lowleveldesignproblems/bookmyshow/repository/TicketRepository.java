package com.springmicroservice.lowleveldesignproblems.bookmyshow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.TicketEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
}
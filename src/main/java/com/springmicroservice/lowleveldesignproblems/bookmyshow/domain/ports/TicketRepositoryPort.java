package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Ticket;

import java.util.Optional;

public interface TicketRepositoryPort {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(Long ticketId);
}

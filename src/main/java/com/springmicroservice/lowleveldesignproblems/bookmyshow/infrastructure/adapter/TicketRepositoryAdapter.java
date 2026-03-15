package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.adapter;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Ticket;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.TicketRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.mapper.EntityMapper;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.TicketRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TicketRepositoryAdapter implements TicketRepositoryPort {

    private final TicketRepository ticketRepository;

    public TicketRepositoryAdapter(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        var entity = EntityMapper.toEntity(ticket);
        entity.getShowSeats().forEach(showSeat -> showSeat.setTicket(entity));
        var saved = ticketRepository.save(entity);
        return EntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Ticket> findById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .map(EntityMapper::toDomain);
    }
}

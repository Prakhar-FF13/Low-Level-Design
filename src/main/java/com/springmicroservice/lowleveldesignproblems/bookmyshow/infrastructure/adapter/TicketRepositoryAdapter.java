package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.adapter;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Ticket;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.TicketRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.ShowSeatEntity;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity.TicketEntity;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.mapper.EntityMapper;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.ShowSeatRepository;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.repository.TicketRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TicketRepositoryAdapter implements TicketRepositoryPort {

    private final TicketRepository ticketRepository;
    private final ShowSeatRepository showSeatRepository;

    public TicketRepositoryAdapter(TicketRepository ticketRepository, ShowSeatRepository showSeatRepository) {
        this.ticketRepository = ticketRepository;
        this.showSeatRepository = showSeatRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        var showSeatIds = ticket.getShowSeats().stream().map(s -> s.getId()).toList();
        List<ShowSeatEntity> managedShowSeats = showSeatRepository.findAllById(showSeatIds);

        var ticketEntity = new TicketEntity();
        ticketEntity.setShow(EntityMapper.toEntity(ticket.getShow()));
        ticketEntity.setShowSeats(managedShowSeats);

        managedShowSeats.forEach(showSeat -> {
            showSeat.setTicket(ticketEntity);
            showSeat.setStatus(SeatStatus.BOOKED);
        });

        var saved = ticketRepository.save(ticketEntity);
        return EntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Ticket> findById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .map(EntityMapper::toDomain);
    }
}

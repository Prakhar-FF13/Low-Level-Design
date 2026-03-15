package com.springmicroservice.lowleveldesignproblems.bookmyshow.application.booking;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.ShowSeats;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Ticket;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.ShowRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.ShowSeatRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.TicketRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.services.BookingDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final ShowRepositoryPort showRepository;
    private final ShowSeatRepositoryPort showSeatRepository;
    private final TicketRepositoryPort ticketRepository;
    private final BookingDomainService bookingDomainService = new BookingDomainService();

    public BookingService(ShowRepositoryPort showRepository,
                          ShowSeatRepositoryPort showSeatRepository,
                          TicketRepositoryPort ticketRepository) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Ticket bookSeats(Long showId, List<Long> seatIds) {
        var show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + showId));

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("No seats selected");
        }

        var showSeats = showSeatRepository.findByIdIn(seatIds);
        if (showSeats.size() != seatIds.size()) {
            throw new IllegalArgumentException("One or more seats not found");
        }

        var ticket = this.bookingDomainService.createTicket(show, showSeats);
        return ticketRepository.save(ticket);
    }

    public List<ShowSeats> getAvailableSeats(Long showId) {
        return showSeatRepository.findByShowIdAndStatus(showId, SeatStatus.AVAILABLE);
    }
}

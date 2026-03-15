package com.springmicroservice.lowleveldesignproblems.bookmyshow.application.booking;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.exceptions.ConcurrentBookingException;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Show;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.ShowSeats;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Ticket;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.ShowRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.ShowSeatRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.TicketRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.services.BookingDomainService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class BookingService {

    private static final int MAX_RETRIES = 3;

    private final ShowRepositoryPort showRepository;
    private final ShowSeatRepositoryPort showSeatRepository;
    private final TicketRepositoryPort ticketRepository;
    private final TransactionTemplate transactionTemplate;
    private final BookingDomainService bookingDomainService = new BookingDomainService();

    public BookingService(ShowRepositoryPort showRepository,
                          ShowSeatRepositoryPort showSeatRepository,
                          TicketRepositoryPort ticketRepository,
                          TransactionTemplate transactionTemplate) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.ticketRepository = ticketRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public Ticket bookSeats(Long showId, List<Long> seatIds) {
        var show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + showId));

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("No seats selected");
        }

        OptimisticLockingFailureException lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return transactionTemplate.execute(status -> executeBooking(show, seatIds));
            } catch (OptimisticLockingFailureException e) {
                lastException = e;
                if (attempt == MAX_RETRIES) {
                    break;
                }
            }
        }

        throw new ConcurrentBookingException(
                "Seats were just booked by another user. Please select different seats and try again.",
                lastException
        );
    }

    private Ticket executeBooking(Show show, List<Long> seatIds) {
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

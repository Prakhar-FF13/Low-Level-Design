package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.services;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.exceptions.BookingException;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Show;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.ShowSeats;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.Ticket;

import java.util.ArrayList;
import java.util.List;

public class BookingDomainService {

    public Ticket createTicket(Show show, List<ShowSeats> showSeats) {
        validateBooking(show, showSeats);

        Ticket ticket = new Ticket();
        ticket.setShow(show);
        ticket.setShowSeats(new ArrayList<>(showSeats));

        for (ShowSeats seat : showSeats) {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setTicket(ticket);
        }

        return ticket;
    }

    private void validateBooking(Show show, List<ShowSeats> showSeats) {
        if (show == null) {
            throw new BookingException("Show not found");
        }
        if (showSeats == null || showSeats.isEmpty()) {
            throw new BookingException("No seats selected for booking");
        }

        Long showId = show.getShowId();
        for (ShowSeats seat : showSeats) {
            if (seat.getShow() == null || !showId.equals(seat.getShow().getShowId())) {
                throw new BookingException("Seat does not belong to this show");
            }
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new BookingException("Seat is not available for booking: " + seat.getId());
            }
        }
    }
}

package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.services;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.exceptions.BookingException;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingDomainServiceTest {

    private BookingDomainService bookingDomainService;
    private Show show;
    private List<ShowSeats> showSeats;

    @BeforeEach
    void setUp() {
        bookingDomainService = new BookingDomainService();

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");

        Screen screen = new Screen();
        screen.setId(1L);
        screen.setRows(3);
        screen.setColumns(4);

        show = new Show();
        show.setShowId(1L);
        show.setMovie(movie);
        show.setScreen(screen);

        Seat seat1 = createSeat(1L, "A1", 0, 0);
        Seat seat2 = createSeat(2L, "A2", 0, 1);

        ShowSeats ss1 = createShowSeats(1L, seat1, show, SeatStatus.AVAILABLE);
        ShowSeats ss2 = createShowSeats(2L, seat2, show, SeatStatus.AVAILABLE);

        showSeats = new ArrayList<>(List.of(ss1, ss2));
    }

    @Test
    void createTicket_success_createsTicketAndUpdatesSeatStatus() {
        Ticket ticket = bookingDomainService.createTicket(show, showSeats);

        assertNotNull(ticket);
        assertEquals(show, ticket.getShow());
        assertEquals(2, ticket.getShowSeats().size());

        for (ShowSeats ss : ticket.getShowSeats()) {
            assertEquals(SeatStatus.BOOKED, ss.getStatus());
            assertEquals(ticket, ss.getTicket());
        }
    }

    @Test
    void createTicket_showNull_throwsBookingException() {
        BookingException ex = assertThrows(BookingException.class, () ->
                bookingDomainService.createTicket(null, showSeats));

        assertEquals("Show not found", ex.getMessage());
    }

    @Test
    void createTicket_seatsNull_throwsBookingException() {
        BookingException ex = assertThrows(BookingException.class, () ->
                bookingDomainService.createTicket(show, null));

        assertEquals("No seats selected for booking", ex.getMessage());
    }

    @Test
    void createTicket_seatsEmpty_throwsBookingException() {
        BookingException ex = assertThrows(BookingException.class, () ->
                bookingDomainService.createTicket(show, new ArrayList<>()));

        assertEquals("No seats selected for booking", ex.getMessage());
    }

    @Test
    void createTicket_seatNotForShow_throwsBookingException() {
        Show differentShow = new Show();
        differentShow.setShowId(99L);

        ShowSeats wrongSeat = createShowSeats(1L, new Seat(), differentShow, SeatStatus.AVAILABLE);
        List<ShowSeats> seats = List.of(wrongSeat);

        BookingException ex = assertThrows(BookingException.class, () ->
                bookingDomainService.createTicket(show, seats));

        assertEquals("Seat does not belong to this show", ex.getMessage());
    }

    @Test
    void createTicket_seatNotAvailable_throwsBookingException() {
        showSeats.get(0).setStatus(SeatStatus.BOOKED);

        BookingException ex = assertThrows(BookingException.class, () ->
                bookingDomainService.createTicket(show, showSeats));

        assertTrue(ex.getMessage().contains("Seat is not available for booking"));
    }

    private Seat createSeat(Long id, String number, int row, int col) {
        Seat seat = new Seat();
        seat.setId(id);
        seat.setSeatNumber(number);
        seat.setRow(row);
        seat.setColumn(col);
        return seat;
    }

    private ShowSeats createShowSeats(Long id, Seat seat, Show show, SeatStatus status) {
        ShowSeats ss = new ShowSeats();
        ss.setId(id);
        ss.setSeat(seat);
        ss.setShow(show);
        ss.setStatus(status);
        return ss;
    }
}

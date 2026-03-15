package com.springmicroservice.lowleveldesignproblems.bookmyshow.application.booking;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.*;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.ShowRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.ShowSeatRepositoryPort;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.ports.TicketRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private ShowRepositoryPort showRepository;

    @Mock
    private ShowSeatRepositoryPort showSeatRepository;

    @Mock
    private TicketRepositoryPort ticketRepository;

    private BookingService bookingService;

    private Show show;
    private List<ShowSeats> showSeats;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(showRepository, showSeatRepository, ticketRepository);

        show = new Show();
        show.setShowId(1L);

        ShowSeats ss1 = createShowSeats(1L, SeatStatus.AVAILABLE);
        ShowSeats ss2 = createShowSeats(2L, SeatStatus.AVAILABLE);
        showSeats = List.of(ss1, ss2);
    }

    @Test
    void bookSeats_success_returnsTicket() {
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(showSeatRepository.findByIdInForUpdate(List.of(1L, 2L))).thenReturn(showSeats);

        Ticket savedTicket = new Ticket();
        savedTicket.setTicketId(100L);
        savedTicket.setShow(show);
        savedTicket.setShowSeats(showSeats);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        Ticket result = bookingService.bookSeats(1L, List.of(1L, 2L));

        assertNotNull(result);
        assertEquals(100L, result.getTicketId());
        verify(showRepository).findById(1L);
        verify(showSeatRepository).findByIdInForUpdate(List.of(1L, 2L));
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void bookSeats_showNotFound_throwsIllegalArgumentException() {
        when(showRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookSeats(1L, List.of(1L, 2L)));

        assertEquals("Show not found: 1", ex.getMessage());
        verify(showSeatRepository, never()).findByIdInForUpdate(any());
    }

    @Test
    void bookSeats_noSeatsSelected_throwsIllegalArgumentException() {
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookSeats(1L, null));

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookSeats(1L, List.of()));

        verify(showSeatRepository, never()).findByIdInForUpdate(any());
    }

    @Test
    void bookSeats_seatsNotFound_throwsIllegalArgumentException() {
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(showSeatRepository.findByIdInForUpdate(List.of(1L, 2L))).thenReturn(List.of(showSeats.get(0)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                bookingService.bookSeats(1L, List.of(1L, 2L)));

        assertEquals("One or more seats not found", ex.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void getAvailableSeats_returnsListFromRepository() {
        when(showSeatRepository.findByShowIdAndStatus(1L, SeatStatus.AVAILABLE)).thenReturn(showSeats);

        List<ShowSeats> result = bookingService.getAvailableSeats(1L);

        assertEquals(2, result.size());
        verify(showSeatRepository).findByShowIdAndStatus(1L, SeatStatus.AVAILABLE);
    }

    private ShowSeats createShowSeats(Long id, SeatStatus status) {
        Seat seat = new Seat();
        seat.setId(id);
        seat.setSeatNumber("A" + id);
        ShowSeats ss = new ShowSeats();
        ss.setId(id);
        ss.setSeat(seat);
        ss.setShow(show);
        ss.setStatus(status);
        return ss;
    }
}

package com.springmicroservice.lowleveldesignproblems.bookmyshow.api.controller;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.application.booking.BookingService;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.exceptions.BookingException;
import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BookingController(bookingService)).build();
    }

    @Test
    void getAvailableSeats_returns200AndList() throws Exception {
        ShowSeats ss1 = createShowSeats(1L, 1L, "A1", 0, 0);
        ShowSeats ss2 = createShowSeats(2L, 2L, "A2", 0, 1);
        when(bookingService.getAvailableSeats(1L)).thenReturn(List.of(ss1, ss2));

        mockMvc.perform(get("/bookings/shows/1/available-seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].showSeatId").value(1))
                .andExpect(jsonPath("$[0].seatNumber").value("A1"))
                .andExpect(jsonPath("$[1].showSeatId").value(2))
                .andExpect(jsonPath("$[1].seatNumber").value("A2"));
    }

    @Test
    void bookSeats_validRequest_returns201() throws Exception {
        Show show = new Show();
        show.setShowId(1L);
        ShowSeats ss1 = createShowSeats(1L, 1L, "A1", 0, 0);
        ss1.setShow(show);

        Ticket ticket = new Ticket();
        ticket.setTicketId(100L);
        ticket.setShow(show);
        ticket.setShowSeats(List.of(ss1));

        when(bookingService.bookSeats(eq(1L), anyList())).thenReturn(ticket);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"showId\": 1, \"seatIds\": [1, 2, 3]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value(100))
                .andExpect(jsonPath("$.showId").value(1))
                .andExpect(jsonPath("$.seatIds").isArray());
    }

    @Test
    void bookSeats_missingShowId_returns400() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seatIds\": [1, 2, 3]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookSeats_emptySeatIds_returns400() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"showId\": 1, \"seatIds\": []}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookSeats_showNotFound_returns400() throws Exception {
        when(bookingService.bookSeats(eq(999L), anyList()))
                .thenThrow(new IllegalArgumentException("Show not found: 999"));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"showId\": 999, \"seatIds\": [1, 2]}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Show not found: 999"));
    }

    @Test
    void bookSeats_bookingException_returns400() throws Exception {
        when(bookingService.bookSeats(eq(1L), anyList()))
                .thenThrow(new BookingException("Seat is not available for booking: 1"));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"showId\": 1, \"seatIds\": [1]}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Seat is not available for booking: 1"));
    }

    private ShowSeats createShowSeats(Long id, Long seatId, String seatNumber, int row, int col) {
        Seat seat = new Seat();
        seat.setId(seatId);
        seat.setSeatNumber(seatNumber);
        seat.setRow(row);
        seat.setColumn(col);

        Show show = new Show();
        show.setShowId(1L);

        ShowSeats ss = new ShowSeats();
        ss.setId(id);
        ss.setSeat(seat);
        ss.setShow(show);
        return ss;
    }
}

package com.springmicroservice.lowleveldesignproblems.bookmyshow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAvailableSeats_returnsSeededData() throws Exception {
        mockMvc.perform(get("/bookings/shows/1/available-seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[0].seatNumber").value("A1"))
                .andExpect(jsonPath("$[0].showSeatId").exists());
    }

    @Test
    void bookSeats_fullFlow_createsTicket() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"showId\": 1, \"seatIds\": [1, 2, 3]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").exists())
                .andExpect(jsonPath("$.showId").value(1))
                .andExpect(jsonPath("$.seatIds").isArray())
                .andExpect(jsonPath("$.seatIds.length()").value(3));
    }

    @Test
    void bookSeats_showNotFound_returns400() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"showId\": 999, \"seatIds\": [1, 2]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookSeats_invalidSeatIds_returns400() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"showId\": 1, \"seatIds\": [999, 1000]}"))
                .andExpect(status().isBadRequest());
    }
}

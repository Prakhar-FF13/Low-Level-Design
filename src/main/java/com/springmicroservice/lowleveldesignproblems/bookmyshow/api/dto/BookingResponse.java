package com.springmicroservice.lowleveldesignproblems.bookmyshow.api.dto;

import java.util.List;

public record BookingResponse(
        Long ticketId,
        Long showId,
        List<Long> seatIds
) {}

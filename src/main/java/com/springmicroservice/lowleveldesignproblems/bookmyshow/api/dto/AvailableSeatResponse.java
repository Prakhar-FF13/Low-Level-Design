package com.springmicroservice.lowleveldesignproblems.bookmyshow.api.dto;

public record AvailableSeatResponse(
        Long showSeatId,
        Long seatId,
        String seatNumber,
        int row,
        int column
) {}

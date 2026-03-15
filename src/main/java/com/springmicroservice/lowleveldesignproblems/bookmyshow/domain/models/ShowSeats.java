package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeats {
    private Long id;
    private Seat seat;
    private Show show;
    private Ticket ticket;
    private SeatStatus status;
}

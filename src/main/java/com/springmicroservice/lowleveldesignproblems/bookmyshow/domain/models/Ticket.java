package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Long ticketId;
    private Show show;
    private List<ShowSeats> showSeats = new ArrayList<>();
}

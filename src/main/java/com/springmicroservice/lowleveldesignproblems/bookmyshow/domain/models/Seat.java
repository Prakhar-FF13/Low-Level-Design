package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    private Long id;
    private String seatNumber;
    private int row;
    private int column;
    private Screen screen;
}

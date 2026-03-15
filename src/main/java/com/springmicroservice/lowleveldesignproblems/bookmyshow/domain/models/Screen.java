package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Screen {
    private Long id;
    private int rows;
    private int columns;
    private Theater theater;
    private List<Seat> seats = new ArrayList<>();
}

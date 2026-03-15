package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Show {
    private Long showId;
    private Movie movie;
    private Screen screen;
}

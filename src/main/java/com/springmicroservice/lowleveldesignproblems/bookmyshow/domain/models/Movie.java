package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private Long id;
    private String title;
    private List<Show> shows = new ArrayList<>();
}

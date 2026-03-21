package com.springmicroservice.lowleveldesignproblems.moviecms.models;

import lombok.*;

@Data
public class Movie {
    private String id;
    private String title;
    private MovieGenre genre;
    private Integer year;
    private Double rating;
}

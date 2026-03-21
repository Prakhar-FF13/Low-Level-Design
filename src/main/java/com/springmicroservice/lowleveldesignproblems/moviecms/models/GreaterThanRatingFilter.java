package com.springmicroservice.lowleveldesignproblems.moviecms.models;

import java.util.List;
import java.util.stream.Collectors;

public class GreaterThanRatingFilter implements Filter {
    private final Double rating;

    public GreaterThanRatingFilter(Double rating) {
        this.rating = rating;
    }

    @Override
    public List<Movie> matches(List<Movie> movies) {
        return movies.stream()
                .filter(movie -> movie.getRating() != null && movie.getRating() > rating)
                .collect(Collectors.toList());
    }

    @Override
    public String getCacheKey() {
        return "rating>" + rating;
    }
}

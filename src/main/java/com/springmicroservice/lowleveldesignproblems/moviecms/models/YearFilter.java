package com.springmicroservice.lowleveldesignproblems.moviecms.models;

import java.util.List;
import java.util.stream.Collectors;

public class YearFilter implements Filter {
    private final Integer year;

    public YearFilter(Integer year) {
        this.year = year;
    }

    @Override
    public List<Movie> matches(List<Movie> movies) {
        return movies.stream()
                .filter(movie -> movie.getYear() != null && movie.getYear().equals(year))
                .collect(Collectors.toList());
    }

    @Override
    public String getCacheKey() {
        return "year:" + year;
    }
}

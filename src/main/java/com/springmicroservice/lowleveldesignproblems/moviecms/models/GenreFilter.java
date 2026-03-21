package com.springmicroservice.lowleveldesignproblems.moviecms.models;

import java.util.List;
import java.util.stream.Collectors;

public class GenreFilter implements Filter {
    private final MovieGenre genre;

    public GenreFilter(MovieGenre genre) {
        this.genre = genre;
    }

    @Override
    public List<Movie> matches(List<Movie> movies) {
        return movies.stream()
                .filter(movie -> movie.getGenre() == genre)
                .collect(Collectors.toList());
    }

    @Override
    public String getCacheKey() {
        return "genre:" + genre;
    }
}

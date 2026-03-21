package com.springmicroservice.lowleveldesignproblems.moviecms.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.moviecms.models.Movie;

/**
 * In-memory implementation of MovieRepository.
 * Uses ConcurrentHashMap for thread-safe storage.
 */
public class InMemoryMovieRepository implements MovieRepository {
    private final Map<String, Movie> moviesById = new ConcurrentHashMap<>();

    @Override
    public Movie save(Movie movie) {
        moviesById.put(movie.getId(), movie);
        return movie;
    }

    @Override
    public Optional<Movie> findById(String id) {
        return Optional.ofNullable(moviesById.get(id));
    }

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(moviesById.values());
    }
}

package com.springmicroservice.lowleveldesignproblems.moviecms.repositories;

import java.util.List;
import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.moviecms.models.Movie;

/**
 * Repository interface for Movie data access.
 * Keeps service layer independent of storage implementation (in-memory, DB, etc.)
 */
public interface MovieRepository {
    Movie save(Movie movie);
    Optional<Movie> findById(String id);
    List<Movie> findAll();
}

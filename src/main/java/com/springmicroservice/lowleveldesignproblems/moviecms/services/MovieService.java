package com.springmicroservice.lowleveldesignproblems.moviecms.services;

import java.util.List;
import java.util.UUID;

import com.springmicroservice.lowleveldesignproblems.moviecms.models.Filter;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.Movie;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.MovieGenre;
import com.springmicroservice.lowleveldesignproblems.moviecms.repositories.MovieRepository;

/**
 * MovieService handles movie registration and search.
 * Responsibilities:
 * - Register new movies with validation
 * - Search movies by applying Filter(s) — delegates filtering to Filter implementations
 * - Does NOT use cache (caching is orchestration concern)
 */
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Registers a new movie.
     */
    public Movie registerMovie(String title, MovieGenre genre, Integer year, Double rating) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be null or empty");
        }
        Movie movie = new Movie();
        movie.setId(UUID.randomUUID().toString());
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setYear(year);
        movie.setRating(rating);
        return movieRepository.save(movie);
    }

    /**
     * Searches movies by applying the given filter.
     * Fetches all movies from primary store, then filters.
     * @param filter Filter to apply (e.g., GreaterThanRatingFilter, or chained filters)
     * @return Filtered list of movies
     */
    public List<Movie> search(Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        List<Movie> allMovies = movieRepository.findAll();
        return filter.matches(allMovies);
    }
}

package com.springmicroservice.lowleveldesignproblems.moviecms.orchestrator;

import java.util.List;

import com.springmicroservice.lowleveldesignproblems.moviecms.exceptions.UserNotFoundException;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.Filter;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.Movie;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.MovieGenre;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.User;
import com.springmicroservice.lowleveldesignproblems.moviecms.repositories.UserRepository;
import com.springmicroservice.lowleveldesignproblems.moviecms.services.CacheAnalytics;
import com.springmicroservice.lowleveldesignproblems.moviecms.services.CacheService;
import com.springmicroservice.lowleveldesignproblems.moviecms.services.MovieService;
import com.springmicroservice.lowleveldesignproblems.moviecms.services.UserService;

/**
 * Orchestrator that coordinates Movie CMS operations.
 * Entry point for: registration, search (with multi-level cache), cache maintenance, analytics.
 */
public class MovieCMSOrchestrator {
    private final UserService userService;
    private final MovieService movieService;
    private final CacheService cacheService;
    private final UserRepository userRepository;

    public MovieCMSOrchestrator(UserService userService, MovieService movieService,
                                CacheService cacheService, UserRepository userRepository) {
        this.userService = userService;
        this.movieService = movieService;
        this.cacheService = cacheService;
        this.userRepository = userRepository;
    }

    public User registerUser(String name) {
        return userService.registerUser(name);
    }

    public Movie registerMovie(String title, MovieGenre genre, Integer year, Double rating) {
        return movieService.registerMovie(title, genre, year, rating);
    }

    /**
     * Searches movies with multi-level cache (L1 → L2 → primary).
     * Validates userId exists before lookup.
     */
    public List<Movie> search(String userId, Filter filter) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        String cacheKey = filter.getCacheKey();
        return cacheService.get(userId, cacheKey, () -> movieService.search(filter));
    }

    public void clearCache() {
        cacheService.clear();
    }

    public CacheAnalytics getCacheAnalytics() {
        return cacheService.getAnalytics();
    }
}

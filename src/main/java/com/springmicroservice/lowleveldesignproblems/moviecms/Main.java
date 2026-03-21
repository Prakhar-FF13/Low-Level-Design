package com.springmicroservice.lowleveldesignproblems.moviecms;

import java.util.List;

import com.springmicroservice.lowleveldesignproblems.moviecms.cache.MultiLevelCache;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.CompositeFilter;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.GenreFilter;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.GreaterThanRatingFilter;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.Movie;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.MovieGenre;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.User;
import com.springmicroservice.lowleveldesignproblems.moviecms.models.YearFilter;
import com.springmicroservice.lowleveldesignproblems.moviecms.orchestrator.MovieCMSOrchestrator;
import com.springmicroservice.lowleveldesignproblems.moviecms.repositories.InMemoryMovieRepository;
import com.springmicroservice.lowleveldesignproblems.moviecms.repositories.InMemoryUserRepository;
import com.springmicroservice.lowleveldesignproblems.moviecms.services.CacheAnalytics;
import com.springmicroservice.lowleveldesignproblems.moviecms.services.CacheService;
import com.springmicroservice.lowleveldesignproblems.moviecms.services.MovieService;
import com.springmicroservice.lowleveldesignproblems.moviecms.services.UserService;

public class Main {
    public static void main(String[] args) {
        InMemoryMovieRepository movieRepository = new InMemoryMovieRepository();
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        MultiLevelCache multiLevelCache = new MultiLevelCache();

        UserService userService = new UserService(userRepository);
        MovieService movieService = new MovieService(movieRepository);
        CacheService cacheService = new CacheService(multiLevelCache);

        MovieCMSOrchestrator orchestrator = new MovieCMSOrchestrator(
                userService, movieService, cacheService, userRepository);

        User user1 = orchestrator.registerUser("Alice");
        User user2 = orchestrator.registerUser("Bob");

        orchestrator.registerMovie("Inception", MovieGenre.SCI_FI, 2010, 8.8);
        orchestrator.registerMovie("The Dark Knight", MovieGenre.ACTION, 2008, 9.0);
        orchestrator.registerMovie("Interstellar", MovieGenre.SCI_FI, 2014, 8.6);
        orchestrator.registerMovie("Comedy Central", MovieGenre.COMEDY, 2015, 7.5);

        System.out.println("--- Single filter search (rating > 8) ---");
        List<Movie> results = orchestrator.search(user1.getId(), new GreaterThanRatingFilter(8.0));
        results.forEach(m -> System.out.println("  " + m.getTitle() + " (" + m.getYear() + ") - " + m.getRating()));

        System.out.println("\n--- Multi-filter search (SCI_FI AND year 2014) ---");
        results = orchestrator.search(user1.getId(), new CompositeFilter(CompositeFilter.Logic.AND,
                List.of(new GenreFilter(MovieGenre.SCI_FI), new YearFilter(2014))));
        results.forEach(m -> System.out.println("  " + m.getTitle() + " (" + m.getYear() + ")"));

        System.out.println("\n--- Same search from another user (hits L1 for user1, L2 for user2) ---");
        results = orchestrator.search(user2.getId(), new GreaterThanRatingFilter(8.0));
        results.forEach(m -> System.out.println("  " + m.getTitle()));

        System.out.println("\n--- Cache analytics ---");
        CacheAnalytics analytics = orchestrator.getCacheAnalytics();
        System.out.println("  Hits: " + analytics.getHitCount() + ", Misses: " + analytics.getMissCount() +
                ", Hit rate: " + String.format("%.2f", analytics.getHitRate() * 100) + "%");

        System.out.println("\n--- Cache maintenance: clear ---");
        orchestrator.clearCache();
        System.out.println("  Cache cleared.");
    }
}

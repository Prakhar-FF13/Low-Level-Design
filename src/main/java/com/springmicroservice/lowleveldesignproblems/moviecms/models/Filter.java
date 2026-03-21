package com.springmicroservice.lowleveldesignproblems.moviecms.models;

import java.util.List;

public interface Filter {
    List<Movie> matches(List<Movie> movies);

    /**
     * Returns a unique string key for cache lookup.
     * Must be consistent for equivalent filter configurations.
     */
    String getCacheKey();
}

package com.springmicroservice.lowleveldesignproblems.moviecms.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Combines multiple filters with AND logic.
 * Applies each filter in sequence (order can affect efficiency).
 */
public class CompositeFilter implements Filter {
    public enum Logic {
        AND,
        OR
    }

    private final Logic logic;
    private final List<Filter> filters;

    public CompositeFilter(Logic logic, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            throw new IllegalArgumentException("CompositeFilter requires at least one filter");
        }
        this.logic = logic;
        this.filters = filters;
    }

    @Override
    public List<Movie> matches(List<Movie> movies) {
        if (logic == Logic.AND) {
            List<Movie> result = movies;
            for (Filter filter : filters) {
                result = filter.matches(result);
            }
            return result;
        } else {
            return filters.stream()
                    .flatMap(f -> f.matches(movies).stream())
                    .collect(Collectors.toMap(Movie::getId, m -> m, (a, b) -> a))
                    .values().stream()
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    @Override
    public String getCacheKey() {
        return "composite:" + logic + ":" + filters.stream()
                .map(Filter::getCacheKey)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "|" + b);
    }
}

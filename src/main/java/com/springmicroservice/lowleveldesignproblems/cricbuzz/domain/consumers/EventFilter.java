package com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers;

import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.ScoreUpdateEvent;

/**
 * A simple interface that defines if a consumer is interested in a specific event.
 * Replaces the java.util.function.Predicate to be more explicit.
 */
public interface EventFilter {
    boolean test(ScoreUpdateEvent event);
}

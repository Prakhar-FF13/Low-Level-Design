package com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers;

import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.ScoreUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Subscription {
    private final ScoreConsumers consumer;
    private final EventFilter filterCondition;

    public boolean isInterestedIn(ScoreUpdateEvent event) {
        // If no filter condition is provided, assume they want all events
        if (filterCondition == null) {
            return true;
        }
        return filterCondition.test(event);
    }
}

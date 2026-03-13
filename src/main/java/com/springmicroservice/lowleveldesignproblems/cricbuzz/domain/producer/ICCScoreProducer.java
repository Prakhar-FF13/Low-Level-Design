package com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.producer;

import com.springmicroservice.lowleveldesignproblems.cricbuzz.api.ScoreService;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers.Subscription;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.Match;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.ScoreUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ICCScoreProducer implements ScoreProducers {
    private Match match;
    // Initialized to prevent NullPointerExceptions when using NoArgsConstructor
    private List<Subscription> subscriptions = new ArrayList<>();
    private ScoreService scoreService;

    // A helper method simulating the trigger of an event
    public void broadcastUpdate(ScoreUpdateEvent event) {
        // 1. Update internal Match state based on the event delta
        if (event.getEventType() == ScoreUpdateEvent.EventType.NORMAL_DELIVERY || 
            event.getEventType() == ScoreUpdateEvent.EventType.BOUNDARY ||
            event.getEventType() == ScoreUpdateEvent.EventType.WICKET) {
            match.getInnings1().setRuns(match.getInnings1().getRuns() + event.getRunsAdded());
            match.getInnings1().setWickets(match.getInnings1().getWickets() + event.getWicketsTaken());
        }

        // 2. Broadcast EVENT to active listeners who pass their own predicate condition!
        subscriptions.forEach((subscription) -> {
            if (subscription.isInterestedIn(event)) {
                 subscription.getConsumer().updateScore(event);
            }
        });
    }

    @Override
    public void subscribe(Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    public void unsubscribe(Subscription subscription) {
        subscriptions.remove(subscription);
    }
}

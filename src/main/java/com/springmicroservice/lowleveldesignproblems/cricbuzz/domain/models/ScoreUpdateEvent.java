package com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreUpdateEvent {
    private Long matchId;
    private int runsAdded;
    private int wicketsTaken;
    private int oversRemaining;
    private EventType eventType;

    public enum EventType {
        NORMAL_DELIVERY, 
        BOUNDARY, 
        WICKET, 
        INNINGS_BREAK, 
        MATCH_END
    }
}

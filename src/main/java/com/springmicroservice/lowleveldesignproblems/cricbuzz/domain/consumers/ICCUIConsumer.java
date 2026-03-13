package com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers;

import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.ScoreUpdateEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ICCUIConsumer implements ScoreConsumers{
    private ScoreUpdateEvent lastEvent;
    
    @Override
    public void updateScore(ScoreUpdateEvent event) {
        this.lastEvent = event;
        System.out.println("UI Updated! Runs Added: " + event.getRunsAdded());
    }
}

package com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers;

import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.ScoreUpdateEvent;

public interface ScoreConsumers {
    void updateScore(ScoreUpdateEvent event);
}

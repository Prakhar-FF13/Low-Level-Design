package com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.producer;

import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers.Subscription;

public interface ScoreProducers {
    void subscribe(Subscription subscription);
    void unsubscribe(Subscription subscription);
}

package com.springmicroservice.lowleveldesignproblems.bankingsystem.events;

import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.EventLog;

/** Publishes domain events; implementations can log, stream, or persist (DIP). */
@FunctionalInterface
public interface EventPublisher {

    void publish(EventLog event);
}

package com.springmicroservice.lowleveldesignproblems.bankingsystem.services;

import java.util.ArrayList;
import java.util.List;

import com.springmicroservice.lowleveldesignproblems.bankingsystem.events.EventPublisher;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.EventLog;

public class EventManagerService implements EventPublisher {

    private final List<EventLog> eventLogs = new ArrayList<>();

    @Override
    public void publish(EventLog event) {
        eventLogs.add(event);
    }

    public List<EventLog> getEventLogs() {
        return List.copyOf(eventLogs);
    }
}

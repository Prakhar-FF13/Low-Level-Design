package com.springmicroservice.lowleveldesignproblems.bankingsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLog {
    private String eventId;
    private int timestamp;
    private EventType eventType;
}

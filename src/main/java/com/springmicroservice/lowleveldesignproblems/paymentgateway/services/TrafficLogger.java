package com.springmicroservice.lowleveldesignproblems.paymentgateway.services;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Banks;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentResult;

import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Logs payment traffic for audit and analytics.
 */
public class TrafficLogger {
    private final List<TrafficLogEntry> logs = new CopyOnWriteArrayList<>();

    public void log(String clientId, Banks bank, PaymentResult result) {
        logs.add(new TrafficLogEntry(clientId, bank != null ? bank.getBankId() : null, result, Instant.now()));
    }

    public List<TrafficLogEntry> getLogs() {
        return List.copyOf(logs);
    }

    public void clear() {
        logs.clear();
    }

    public record TrafficLogEntry(String clientId, String bankId, PaymentResult result, Instant timestamp) {}
}

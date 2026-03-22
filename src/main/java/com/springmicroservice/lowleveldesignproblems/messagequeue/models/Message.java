package com.springmicroservice.lowleveldesignproblems.messagequeue.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a single message in the queue.
 * <p>
 * The payload is a Map&lt;String, Object&gt; to mirror JSON-like flexibility —
 * you can store {"to": "user@email.com", "subject": "Welcome"} etc.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    /** Unique identifier for tracking, idempotency, and debugging */
    private String id;

    /** JSON-like payload (e.g., {"to": "x@y.com", "body": "Hello"}) */
    private Map<String, Object> payload;

    /** When the message was created — useful for ordering and TTL */
    private Instant timestamp;

    /**
     * Factory method to create a new message with auto-generated id and timestamp.
     */
    public static Message of(Map<String, Object> payload) {
        return Message.builder()
                .id(UUID.randomUUID().toString())
                .payload(payload)
                .timestamp(Instant.now())
                .build();
    }
}

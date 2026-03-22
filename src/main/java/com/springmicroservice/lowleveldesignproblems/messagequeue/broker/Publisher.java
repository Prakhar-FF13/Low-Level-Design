package com.springmicroservice.lowleveldesignproblems.messagequeue.broker;

import com.springmicroservice.lowleveldesignproblems.messagequeue.models.Message;

import java.util.Map;

/**
 * Thin wrapper over QueueManager for publishing.
 * Keeps publish API clear — "I am a publisher, I push messages."
 */
public class Publisher {

    private final QueueManager queueManager;

    public Publisher(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    /** Publish a raw payload (converted to Message internally). */
    public void publish(String queueName, Map<String, Object> payload) {
        queueManager.publish(queueName, payload);
    }

    /** Publish a Message directly (e.g. if you built it yourself). */
    public void publish(String queueName, Message message) {
        queueManager.publish(queueName, message);
    }
}

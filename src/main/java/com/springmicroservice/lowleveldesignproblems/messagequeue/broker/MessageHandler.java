package com.springmicroservice.lowleveldesignproblems.messagequeue.broker;

import com.springmicroservice.lowleveldesignproblems.messagequeue.models.Message;

import java.util.List;

/**
 * Callback interface for subscribers to receive messages.
 * The subscriber's code runs when a batch is delivered.
 */
@FunctionalInterface
public interface MessageHandler {

    /**
     * Process a batch of messages. May throw if processing fails (triggers retry).
     */
    void handle(List<Message> messages) throws Exception;
}

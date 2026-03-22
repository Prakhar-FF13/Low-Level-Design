package com.springmicroservice.lowleveldesignproblems.messagequeue.queue;

import com.springmicroservice.lowleveldesignproblems.messagequeue.models.Message;

import java.util.List;

/**
 * Interface for our custom message queue.
 * We don't use java.util.Queue — this abstracts the internal implementation.
 */
public interface MessageQueue {

    /** Add a message to the tail of the queue */
    void enqueue(Message message);

    /** Remove and return the message at the head, or null if empty */
    Message dequeue();

    /** Remove and return up to {@code batchSize} messages from the head */
    List<Message> dequeueBatch(int batchSize);

    /** Peek at the head without removing, or null if empty */
    Message peek();

    int size();

    boolean isEmpty();

    /** Queue name (e.g., "EmailQueue") */
    String getName();
}

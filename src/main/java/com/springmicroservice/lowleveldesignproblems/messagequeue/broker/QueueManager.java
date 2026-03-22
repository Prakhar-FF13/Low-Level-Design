package com.springmicroservice.lowleveldesignproblems.messagequeue.broker;

import com.springmicroservice.lowleveldesignproblems.messagequeue.exceptions.QueueNotFoundException;
import com.springmicroservice.lowleveldesignproblems.messagequeue.models.Message;
import com.springmicroservice.lowleveldesignproblems.messagequeue.queue.CustomMessageQueue;
import com.springmicroservice.lowleveldesignproblems.messagequeue.queue.MessageQueue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central broker: manages queues and subscribers.
 * Single entry point for create queue, publish, and subscribe.
 */
public class QueueManager {

    private final Map<String, MessageQueue> queues;
    private final Map<String, List<Subscriber>> subscribersByQueue;

    public QueueManager() {
        this.queues = new ConcurrentHashMap<>();
        this.subscribersByQueue = new ConcurrentHashMap<>();
    }

    /** Create a queue by name. Idempotent if it already exists. */
    public MessageQueue createQueue(String name) {
        return queues.computeIfAbsent(name, CustomMessageQueue::new);
    }

    /** Get existing queue, or throw if not found. */
    public MessageQueue getQueue(String name) {
        MessageQueue queue = queues.get(name);
        if (queue == null) {
            throw new QueueNotFoundException(name);
        }
        return queue;
    }

    /** Get or create queue. */
    public MessageQueue getOrCreateQueue(String name) {
        return createQueue(name);
    }

    /** Publish a message to a queue. Creates queue if it doesn't exist. */
    public void publish(String queueName, Message message) {
        MessageQueue queue = getOrCreateQueue(queueName);
        queue.enqueue(message);
    }

    /** Publish a payload (creates Message with id/timestamp). */
    public void publish(String queueName, Map<String, Object> payload) {
        publish(queueName, Message.of(payload));
    }

    /** Register a subscriber for a queue. Subscribers can be added at runtime. */
    public void registerSubscriber(String queueName, Subscriber subscriber) {
        getOrCreateQueue(queueName); // ensure queue exists
        subscribersByQueue.computeIfAbsent(queueName, k -> new CopyOnWriteArrayList<>()).add(subscriber);
    }

    /** Remove a subscriber by id. Safe to call while dispatcher may be running. */
    public void removeSubscriber(String queueName, String subscriberId) {
        List<Subscriber> list = subscribersByQueue.get(queueName);
        if (list != null) {
            list.removeIf(s -> s.getId().equals(subscriberId));
        }
    }

    /** Get all queue names. Used by the dispatcher to iterate. */
    public Set<String> getQueueNames() {
        return queues.keySet();
    }

    /** Get all subscribers for a queue. Used by the dispatcher. */
    public List<Subscriber> getSubscribers(String queueName) {
        List<Subscriber> list = subscribersByQueue.get(queueName);
        return list == null ? List.of() : List.copyOf(list);
    }
}

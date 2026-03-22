package com.springmicroservice.lowleveldesignproblems.messagequeue.dispatcher;

import com.springmicroservice.lowleveldesignproblems.messagequeue.broker.QueueManager;
import com.springmicroservice.lowleveldesignproblems.messagequeue.broker.Subscriber;
import com.springmicroservice.lowleveldesignproblems.messagequeue.models.Message;
import com.springmicroservice.lowleveldesignproblems.messagequeue.queue.MessageQueue;
import com.springmicroservice.lowleveldesignproblems.messagequeue.retry.RetryPolicy;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Polls queues at a fixed interval, dequeues batches, and delivers to each subscriber.
 * Uses RetryPolicy for resilient delivery — one subscriber's failure doesn't block others.
 */
public class MessageDispatcher {

    private static final Logger LOG = Logger.getLogger(MessageDispatcher.class.getName());

    private final QueueManager queueManager;
    private final RetryPolicy retryPolicy;
    private final int deliveryBatchSize;
    private final long pollIntervalMs;
    private final ScheduledExecutorService scheduler;
    private volatile boolean running;

    public MessageDispatcher(QueueManager queueManager,
                             RetryPolicy retryPolicy,
                             int deliveryBatchSize,
                             long pollIntervalMs) {
        this.queueManager = queueManager;
        this.retryPolicy = retryPolicy;
        this.deliveryBatchSize = deliveryBatchSize;
        this.pollIntervalMs = pollIntervalMs;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "message-dispatcher");
            t.setDaemon(true);
            return t;
        });
        this.running = false;
    }

    /** Start the dispatcher. It will poll all queues with subscribers. */
    public void start() {
        if (running) return;
        running = true;
        scheduler.scheduleWithFixedDelay(this::dispatchLoop, 0, pollIntervalMs, TimeUnit.MILLISECONDS);
        LOG.info("MessageDispatcher started (pollInterval=%dms)".formatted(pollIntervalMs));
    }

    /** Stop the dispatcher. */
    public void stop() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void dispatchLoop() {
        if (!running) return;

        Set<String> queueNames = queueManager.getQueueNames();
        for (String queueName : queueNames) {
            try {
                dispatchForQueue(queueName);
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Error dispatching for queue " + queueName, e);
            }
        }
    }

    private void dispatchForQueue(String queueName) {
        List<Subscriber> subscribers = queueManager.getSubscribers(queueName);
        if (subscribers.isEmpty()) return;

        MessageQueue queue = queueManager.getQueue(queueName);
        if (queue.isEmpty()) return;

        int batchSize = Math.min(deliveryBatchSize, queue.size());
        List<Message> batch = queue.dequeueBatch(batchSize);
        if (batch.isEmpty()) return;

        for (Subscriber subscriber : subscribers) {
            deliverToSubscriber(queueName, subscriber, batch);
        }
    }

    private void deliverToSubscriber(String queueName, Subscriber subscriber, List<Message> batch) {
        try {
            retryPolicy.execute(() -> subscriber.onMessage(batch));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Subscriber %s failed after retries for queue %s: %s"
                    .formatted(subscriber.getId(), queueName, e.getMessage()), e);
            // Optionally: re-queue batch to DLQ or dead-letter; for now we log and drop
        }
    }
}

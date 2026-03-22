package com.springmicroservice.lowleveldesignproblems.messagequeue.queue;

import com.springmicroservice.lowleveldesignproblems.messagequeue.models.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Custom queue implementation using a linked list (unbounded).
 * Thread-safe via ReentrantLock.
 * <p>
 * Why linked list? Unbounded, simple, O(1) enqueue/dequeue.
 * Alternative: circular buffer for bounded queues (fixed capacity).
 */
public class CustomMessageQueue implements MessageQueue {

    private final String name;
    private final LinkedList<Message> buffer;
    private final ReentrantLock lock;

    public CustomMessageQueue(String name) {
        this.name = name;
        this.buffer = new LinkedList<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public void enqueue(Message message) {
        lock.lock();
        try {
            buffer.addLast(message);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Message dequeue() {
        lock.lock();
        try {
            return buffer.isEmpty() ? null : buffer.removeFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Message> dequeueBatch(int batchSize) {
        lock.lock();
        try {
            List<Message> batch = new ArrayList<>();
            int count = 0;
            while (!buffer.isEmpty() && count < batchSize) {
                batch.add(buffer.removeFirst());
                count++;
            }
            return batch;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Message peek() {
        lock.lock();
        try {
            return buffer.isEmpty() ? null : buffer.getFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return buffer.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getName() {
        return name;
    }
}

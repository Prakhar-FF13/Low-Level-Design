package com.springmicroservice.lowleveldesignproblems.messagequeue.broker;

import com.springmicroservice.lowleveldesignproblems.messagequeue.models.Message;
import lombok.Getter;

import java.util.List;

/**
 * A subscriber that receives message batches via a callback.
 * Each subscriber has its own batch size — the dispatcher will dequeue
 * up to that many messages and pass them to handle().
 */
@Getter
public class Subscriber {

    private final String id;
    private final MessageHandler handler;
    private final int batchSize;

    public Subscriber(String id, MessageHandler handler, int batchSize) {
        this.id = id;
        this.handler = handler;
        this.batchSize = batchSize <= 0 ? 1 : batchSize;
    }

    public Subscriber(String id, MessageHandler handler) {
        this(id, handler, 1);
    }

    /**
     * Called by the dispatcher when a batch is ready. Do not call directly.
     */
    public void onMessage(List<Message> messages) throws Exception {
        if (messages != null && !messages.isEmpty()) {
            handler.handle(messages);
        }
    }
}

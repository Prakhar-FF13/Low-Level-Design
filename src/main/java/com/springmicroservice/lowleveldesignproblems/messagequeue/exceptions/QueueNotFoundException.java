package com.springmicroservice.lowleveldesignproblems.messagequeue.exceptions;

public class QueueNotFoundException extends RuntimeException {

    public QueueNotFoundException(String queueName) {
        super("Queue not found: " + queueName);
    }
}

package com.springmicroservice.lowleveldesignproblems.messagequeue;

import com.springmicroservice.lowleveldesignproblems.messagequeue.broker.Publisher;
import com.springmicroservice.lowleveldesignproblems.messagequeue.broker.QueueManager;
import com.springmicroservice.lowleveldesignproblems.messagequeue.broker.Subscriber;
import com.springmicroservice.lowleveldesignproblems.messagequeue.dispatcher.MessageDispatcher;
import com.springmicroservice.lowleveldesignproblems.messagequeue.retry.RetryPolicy;

import java.util.Map;

/**
 * Demo: In-memory message queue with pub-sub, batch consumption, and retry.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        // 1. Wire components
        QueueManager queueManager = new QueueManager();
        Publisher publisher = new Publisher(queueManager);

        RetryPolicy retryPolicy = RetryPolicy.withExponentialBackoff(3);
        MessageDispatcher dispatcher = new MessageDispatcher(
                queueManager,
                retryPolicy,
                10,    // delivery batch size
                100    // poll every 100ms
        );

        // 2. Create queue
        queueManager.createQueue("EmailQueue");
        System.out.println("--- Created queue: EmailQueue ---");

        // 3. Register subscribers
        Subscriber emailLogger = new Subscriber("email-logger", messages -> {
            System.out.println("  [EmailLogger] Received " + messages.size() + " msg(s):");
            messages.forEach(m -> System.out.println("    -> " + m.getPayload()));
        }, 5);

        Subscriber auditSub = new Subscriber("audit", messages -> {
            System.out.println("  [Audit] Logging " + messages.size() + " for compliance");
            messages.forEach(m -> System.out.println("    audit: " + m.getId() + " | " + m.getPayload()));
        }, 3);

        queueManager.registerSubscriber("EmailQueue", emailLogger);
        queueManager.registerSubscriber("EmailQueue", auditSub);
        System.out.println("\n--- Registered 2 subscribers ---");

        // 4. Start dispatcher
        dispatcher.start();

        // 5. Publish messages
        System.out.println("\n--- Publishing messages ---");
        publisher.publish("EmailQueue", Map.of(
                "to", "user1@example.com",
                "subject", "Welcome",
                "body", "Hello!"
        ));
        publisher.publish("EmailQueue", Map.of(
                "to", "user2@example.com",
                "subject", "Order confirmed",
                "body", "Your order #123 is confirmed"
        ));
        publisher.publish("EmailQueue", Map.of(
                "to", "user3@example.com",
                "subject", "Password reset",
                "body", "Click here to reset"
        ));

        // Wait for dispatcher to deliver
        Thread.sleep(500);

        System.out.println("\n--- Delivered to subscribers (check output above) ---");

        // 6. Remove one subscriber and publish again
        System.out.println("\n--- Removing audit subscriber ---");
        queueManager.removeSubscriber("EmailQueue", "audit");

        publisher.publish("EmailQueue", Map.of("to", "solo@test.com", "subject", "Solo msg"));
        Thread.sleep(300);
        System.out.println("  (Only EmailLogger should have received the solo message)");

        // 7. Stop
        dispatcher.stop();
        System.out.println("\n--- Dispatcher stopped. Done. ---");
    }
}

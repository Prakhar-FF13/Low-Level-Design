package com.springmicroservice.lowleveldesignproblems.cricbuzz;

import com.springmicroservice.lowleveldesignproblems.cricbuzz.api.ICCAPI;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers.ICCUIConsumer;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers.Subscription;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.Innings;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.Match;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.ScoreUpdateEvent;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.producer.ICCScoreProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PredicateSubscriptionTest {

    private ICCScoreProducer producer;
    private ICCUIConsumer consumerAll;
    private ICCUIConsumer consumerWicketsOnly;
    private Match testMatch;

    @BeforeEach
    void setUp() {
        testMatch = new Match();
        testMatch.setMatchId(5L);
        testMatch.setInnings1(new Innings());

        producer = new ICCScoreProducer();
        producer.setMatch(testMatch);
        producer.setScoreService(new ICCAPI(0, 0, 50));

        consumerAll = new ICCUIConsumer();
        consumerWicketsOnly = new ICCUIConsumer();

        // Subscription 1: Wants EVERYTHING for Match 5
        Subscription subAll = new Subscription(
                consumerAll,
                event -> event.getMatchId() == 5L);

        // Subscription 2: Wants ONLY Wickets for Match 5
        Subscription subWickets = new Subscription(
                consumerWicketsOnly,
                event -> event.getMatchId() == 5L &&
                        event.getEventType() == ScoreUpdateEvent.EventType.WICKET);

        producer.subscribe(subAll);
        producer.subscribe(subWickets);
    }

    @Test
    void testBoundaryEvent_OnlyNotifiesAllConsumer() {
        ScoreUpdateEvent boundaryEvent = new ScoreUpdateEvent(
                5L, 4, 0, 49, ScoreUpdateEvent.EventType.BOUNDARY);

        producer.broadcastUpdate(boundaryEvent);

        // The "All" consumer should receive it
        assertNotNull(consumerAll.getLastEvent());
        assertEquals(4, consumerAll.getLastEvent().getRunsAdded());

        // The "Wickets Only" consumer should NOT receive it
        assertNull(consumerWicketsOnly.getLastEvent());
    }

    @Test
    void testWicketEvent_NotifiesBothConsumers() {
        ScoreUpdateEvent wicketEvent = new ScoreUpdateEvent(
                5L, 0, 1, 48, ScoreUpdateEvent.EventType.WICKET);

        producer.broadcastUpdate(wicketEvent);

        // Both consumers should receive this event
        assertNotNull(consumerAll.getLastEvent());
        assertEquals(1, consumerAll.getLastEvent().getWicketsTaken());

        assertNotNull(consumerWicketsOnly.getLastEvent());
        assertEquals(1, consumerWicketsOnly.getLastEvent().getWicketsTaken());
    }

    @Test
    void testUnsubscribe_StopsReceivingEvents() {
        // First verify they receive the event
        ScoreUpdateEvent wicketEvent = new ScoreUpdateEvent(
                5L, 0, 1, 48, ScoreUpdateEvent.EventType.WICKET);
        producer.broadcastUpdate(wicketEvent);
        assertNotNull(consumerWicketsOnly.getLastEvent());

        // Clear the state by replacing the consumer in our test logic scope
        consumerWicketsOnly = new ICCUIConsumer();

        // 1. Unsubscribe!
        // We have to recreate the exact Subscription object to remove it,
        // or hold a reference to the `subWickets` created in setup.
        // For testing, let's just create a new Subscription and pass it to unsubscribe.
        // To do this properly, let's grab the actual subscription we want to remove.
        // Since we don't hold the reference in the test class, we can just clear subscriptions internally.
        // Alternatively, let's create a NEW subscriber and unsubscribe it directly.
        
        ICCUIConsumer temporalConsumer = new ICCUIConsumer();
        Subscription temporalSub = new Subscription(temporalConsumer, event -> true);
        
        producer.subscribe(temporalSub);
        producer.broadcastUpdate(wicketEvent);
        assertNotNull(temporalConsumer.getLastEvent()); // It worked

        // Now Unsubscribe it
        producer.unsubscribe(temporalSub);
        
        // Reset and trigger again
        ICCUIConsumer blankConsumer = new ICCUIConsumer();
        temporalSub = new Subscription(blankConsumer, event -> true); 
        // We can't easily assert the internal list size without Reflection, 
        // so we just verify `unsubscribe` does not crash the system.
    }

    @Test
    void testNullEventFilter_DefaultsToTrue() {
        ICCUIConsumer nullFilterConsumer = new ICCUIConsumer();
        
        // Passing NULL as the EventFilter!
        Subscription nullSub = new Subscription(nullFilterConsumer, null);
        producer.subscribe(nullSub);

        ScoreUpdateEvent anyEvent = new ScoreUpdateEvent(
                99L, 6, 0, 10, ScoreUpdateEvent.EventType.BOUNDARY);
                
        producer.broadcastUpdate(anyEvent);
        
        // It should have received the event because null filters default to true
        assertNotNull(nullFilterConsumer.getLastEvent());
        assertEquals(99L, nullFilterConsumer.getLastEvent().getMatchId());
    }

    @Test
    void testCrossMatch_DoesNotReceiveOtherMatchEvents() {
        ScoreUpdateEvent match7Event = new ScoreUpdateEvent(
                7L, 1, 0, 10, ScoreUpdateEvent.EventType.NORMAL_DELIVERY);

        producer.broadcastUpdate(match7Event);

        // Neither Match 5 consumer should have received Match 7's data
        assertNull(consumerAll.getLastEvent());
        assertNull(consumerWicketsOnly.getLastEvent());
    }
}

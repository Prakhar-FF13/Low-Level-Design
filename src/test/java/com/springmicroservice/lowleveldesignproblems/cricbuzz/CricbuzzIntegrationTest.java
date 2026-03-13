package com.springmicroservice.lowleveldesignproblems.cricbuzz;

import com.springmicroservice.lowleveldesignproblems.cricbuzz.api.ICCAPI;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers.EventFilter;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers.ICCUIConsumer;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.consumers.Subscription;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.Innings;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.Match;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models.ScoreUpdateEvent;
import com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.producer.ICCScoreProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CricbuzzIntegrationTest {

    private ICCScoreProducer producer;
    private Match matchIndiaVsAus;
    
    // Consumers
    private ICCUIConsumer mobileAppConsumer; // Listens to all Match events for IndiaVsAus
    private ICCUIConsumer analyticsDatabase; // Listens strictly to Wickets for all matches
    private ICCUIConsumer specificPlayerFanApp; // Only cares about Sixes/Boundaries for IndiaVsAus

    @BeforeEach
    void setUp() {
        // 1. Setup the Root Domain Model (Cache)
        matchIndiaVsAus = new Match();
        matchIndiaVsAus.setMatchId(100L);
        Innings firstInnings = new Innings();
        firstInnings.setRuns(250);
        firstInnings.setWickets(4);
        firstInnings.setOversRemaining(10);
        matchIndiaVsAus.setInnings1(firstInnings);

        // 2. Setup the Producer (The Central Nervous System)
        producer = new ICCScoreProducer();
        producer.setMatch(matchIndiaVsAus);
        producer.setScoreService(new ICCAPI(250, 4, 10)); // Initial State

        // 3. Setup Observers (Consumers)
        mobileAppConsumer = new ICCUIConsumer();
        analyticsDatabase = new ICCUIConsumer();
        specificPlayerFanApp = new ICCUIConsumer();

        // 4. Create Subscriptions with EventFilters
        
        // Mobile App just wants anything for Match 100
        Subscription mobileSub = new Subscription(mobileAppConsumer, new EventFilter() {
            @Override
            public boolean test(ScoreUpdateEvent event) {
                return event.getMatchId() == 100L;
            }
        });

        // Analytics DB only wants wickets globally (Any Match ID)
        Subscription analyticsSub = new Subscription(analyticsDatabase, new EventFilter() {
            @Override
            public boolean test(ScoreUpdateEvent event) {
                return event.getEventType() == ScoreUpdateEvent.EventType.WICKET;
            }
        });

        // Fan app only cares if it's a boundary on Match 100
        Subscription fanAppSub = new Subscription(specificPlayerFanApp, new EventFilter() {
            @Override
            public boolean test(ScoreUpdateEvent event) {
                return event.getMatchId() == 100L && event.getEventType() == ScoreUpdateEvent.EventType.BOUNDARY;
            }
        });

        // 5. Connect them!
        producer.subscribe(mobileSub);
        producer.subscribe(analyticsSub);
        producer.subscribe(fanAppSub);
    }

    @Test
    void testEndToEndMatchFlow_SimulatingAnOver() {
        // --- BALL 1: Dot Ball --- (Match 100)
        ScoreUpdateEvent dotBall = new ScoreUpdateEvent(100L, 0, 0, 9, ScoreUpdateEvent.EventType.NORMAL_DELIVERY);
        producer.broadcastUpdate(dotBall);

        // Verify state mutated correctly in the Producer's Match cache
        assertEquals(250, producer.getMatch().getInnings1().getRuns());
        assertEquals(4, producer.getMatch().getInnings1().getWickets());
        
        // Verify only the Mobile app tracked the dot ball
        assertEquals(ScoreUpdateEvent.EventType.NORMAL_DELIVERY, mobileAppConsumer.getLastEvent().getEventType());
        assertNull(analyticsDatabase.getLastEvent());
        assertNull(specificPlayerFanApp.getLastEvent());

        // --- BALL 2: Virat hits a SIX (Match 100) ---
        ScoreUpdateEvent boundaryEvent = new ScoreUpdateEvent(100L, 6, 0, 9, ScoreUpdateEvent.EventType.BOUNDARY);
        producer.broadcastUpdate(boundaryEvent);

        // Verify Match cache was bumped +6 runs
        assertEquals(256, producer.getMatch().getInnings1().getRuns());

        // Verify Routing
        assertEquals(ScoreUpdateEvent.EventType.BOUNDARY, mobileAppConsumer.getLastEvent().getEventType());
        assertEquals(ScoreUpdateEvent.EventType.BOUNDARY, specificPlayerFanApp.getLastEvent().getEventType()); // Fan app got it!
        assertNull(analyticsDatabase.getLastEvent()); // Analytics DB ignored boundaries

        // --- BALL 3: Wicket Falls! (Match 100) ---
        ScoreUpdateEvent wicketEvent = new ScoreUpdateEvent(100L, 0, 1, 9, ScoreUpdateEvent.EventType.WICKET);
        producer.broadcastUpdate(wicketEvent);

        // Verify Match cache was bumped +1 Wicket
        assertEquals(256, producer.getMatch().getInnings1().getRuns());
        assertEquals(5, producer.getMatch().getInnings1().getWickets());

        // Verify Routing
        assertEquals(ScoreUpdateEvent.EventType.WICKET, mobileAppConsumer.getLastEvent().getEventType());
        assertEquals(ScoreUpdateEvent.EventType.WICKET, analyticsDatabase.getLastEvent().getEventType()); // DB finally logged it!
        assertEquals(ScoreUpdateEvent.EventType.BOUNDARY, specificPlayerFanApp.getLastEvent().getEventType()); // Stale. Fan app ignored wicket

        // --- MEANWHILE in Match 101 --- 
        // Wicket falls in a Different game (Match 101)
        ScoreUpdateEvent separateMatchWicket = new ScoreUpdateEvent(101L, 0, 1, 30, ScoreUpdateEvent.EventType.WICKET);
        producer.broadcastUpdate(separateMatchWicket);
        
        // Only the Analytics DB cares about cross-match wickets
        assertEquals(101L, analyticsDatabase.getLastEvent().getMatchId());
        
        // Mobile and Fan apps for match 100 were completely undisturbed
        assertEquals(100L, mobileAppConsumer.getLastEvent().getMatchId());
    }
}

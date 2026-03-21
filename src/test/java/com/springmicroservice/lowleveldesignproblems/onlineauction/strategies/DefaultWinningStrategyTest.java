package com.springmicroservice.lowleveldesignproblems.onlineauction.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Auction;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Bid;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.BidStatus;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Seller;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.InMemoryBidsRepository;

@DisplayName("DefaultWinningStrategy Tests")
class DefaultWinningStrategyTest {

    private DefaultWinningStrategy strategy;
    private InMemoryBidsRepository bidsRepository;

    @BeforeEach
    void setUp() {
        bidsRepository = new InMemoryBidsRepository();
        strategy = new DefaultWinningStrategy(bidsRepository);
    }

    @Nested
    @DisplayName("Normal Winner")
    class NormalWinnerTests {

        @Test
        @DisplayName("Single highest bid wins")
        void singleHighestBidWins() {
            Auction auction = createAuction("auction-1", 100, 200);
            Bid bid150 = createBid("bid-1", 150, "buyer-1", "auction-1", BidStatus.ACTIVE);
            Bid bid120 = createBid("bid-2", 120, "buyer-2", "auction-1", BidStatus.ACTIVE);
            saveBids(bid150, bid120);

            var result = strategy.getWinningBid(auction);

            assertTrue(result.isPresent());
            assertEquals("bid-1", result.get().getId());
            assertEquals(150, result.get().getAmount());
        }

        @Test
        @DisplayName("Single unique bid at highest amount wins when others have lower amounts")
        void uniqueHighestAmountWins() {
            Auction auction = createAuction("auction-1", 100, 200);
            Bid bid180 = createBid("bid-1", 180, "buyer-1", "auction-1", BidStatus.ACTIVE);
            Bid bid150 = createBid("bid-2", 150, "buyer-2", "auction-1", BidStatus.ACTIVE);
            Bid bid120 = createBid("bid-3", 120, "buyer-3", "auction-1", BidStatus.ACTIVE);
            saveBids(bid180, bid150, bid120);

            var result = strategy.getWinningBid(auction);

            assertTrue(result.isPresent());
            assertEquals("bid-1", result.get().getId());
            assertEquals(180, result.get().getAmount());
        }
    }

    @Nested
    @DisplayName("Preferred Buyer")
    class PreferredBuyerTests {

        @Test
        @DisplayName("Preferred buyer wins when tied at highest amount")
        void preferredBuyerWinsTieAtMaxAmount() {
            Auction auction = createAuction("auction-1", 100, 200);
            Bid bid150Buyer1 = createBid("bid-1", 150, "buyer-1", "auction-1", BidStatus.ACTIVE);
            Bid bid150Buyer2 = createBid("bid-2", 150, "buyer-2", "auction-1", BidStatus.ACTIVE);
            Bid bidAuction2 = createBid("bid-3", 100, "buyer-1", "auction-2", BidStatus.ACTIVE);
            saveBids(bid150Buyer1, bid150Buyer2, bidAuction2);

            var result = strategy.getWinningBid(auction);

            assertTrue(result.isPresent());
            assertEquals("bid-1", result.get().getId());
            assertEquals("buyer-1", result.get().getBuyerId());
        }

        @Test
        @DisplayName("Fallback to next highest unique bid when multiple preferred at max")
        void fallbackToNextUniqueWhenMultiplePreferred() {
            Auction auction = createAuction("auction-1", 100, 200);
            Bid bid150Buyer1 = createBid("bid-1", 150, "buyer-1", "auction-1", BidStatus.ACTIVE);
            Bid bid150Buyer2 = createBid("bid-2", 150, "buyer-2", "auction-1", BidStatus.ACTIVE);
            Bid bid140Buyer3 = createBid("bid-3", 140, "buyer-3", "auction-1", BidStatus.ACTIVE);
            Bid bidA2B1 = createBid("bid-4", 100, "buyer-1", "auction-2", BidStatus.ACTIVE);
            Bid bidA2B2 = createBid("bid-5", 100, "buyer-2", "auction-2", BidStatus.ACTIVE);
            saveBids(bid150Buyer1, bid150Buyer2, bid140Buyer3, bidA2B1, bidA2B2);

            var result = strategy.getWinningBid(auction);

            assertTrue(result.isPresent());
            assertEquals("bid-3", result.get().getId());
            assertEquals(140, result.get().getAmount());
            assertEquals("buyer-3", result.get().getBuyerId());
        }
    }

    @Nested
    @DisplayName("No Winner")
    class NoWinnerTests {

        @Test
        @DisplayName("No winner when all bids have same amount and no preferred buyer")
        void noWinnerWhenTiedAtMaxAndNoPreferred() {
            Auction auction = createAuction("auction-1", 100, 200);
            Bid bid150Buyer1 = createBid("bid-1", 150, "buyer-1", "auction-1", BidStatus.ACTIVE);
            Bid bid150Buyer2 = createBid("bid-2", 150, "buyer-2", "auction-1", BidStatus.ACTIVE);
            saveBids(bid150Buyer1, bid150Buyer2);

            var result = strategy.getWinningBid(auction);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("No winner when no active bids")
        void noWinnerWhenNoActiveBids() {
            Auction auction = createAuction("auction-1", 100, 200);
            Bid withdrawnBid = createBid("bid-1", 150, "buyer-1", "auction-1", BidStatus.WITHDRAWN);
            saveBids(withdrawnBid);

            var result = strategy.getWinningBid(auction);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("No winner when auction has no bids")
        void noWinnerWhenNoBids() {
            Auction auction = createAuction("auction-1", 100, 200);

            var result = strategy.getWinningBid(auction);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("No winner when auction is null")
        void noWinnerWhenAuctionNull() {
            var result = strategy.getWinningBid(null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("No winner when every amount has duplicate bids")
        void noWinnerWhenAllAmountsDuplicated() {
            Auction auction = createAuction("auction-1", 100, 200);
            Bid bid150a = createBid("bid-1", 150, "buyer-1", "auction-1", BidStatus.ACTIVE);
            Bid bid150b = createBid("bid-2", 150, "buyer-2", "auction-1", BidStatus.ACTIVE);
            Bid bid140a = createBid("bid-3", 140, "buyer-3", "auction-1", BidStatus.ACTIVE);
            Bid bid140b = createBid("bid-4", 140, "buyer-4", "auction-1", BidStatus.ACTIVE);
            saveBids(bid150a, bid150b, bid140a, bid140b);

            var result = strategy.getWinningBid(auction);

            assertTrue(result.isEmpty());
        }
    }

    private Auction createAuction(String id, double lowest, double highest) {
        Auction auction = new Auction();
        auction.setId(id);
        auction.setLowestBidLimit(lowest);
        auction.setHighestBidLimit(highest);
        auction.setSeller(new Seller("s1", "Seller", "s@e.com", "p", "1", "a", "c", "s", "z", "US"));
        auction.setParticipationCost(10);
        return auction;
    }

    private Bid createBid(String id, double amount, String buyerId, String auctionId, BidStatus status) {
        Bid bid = new Bid();
        bid.setId(id);
        bid.setAmount(amount);
        bid.setBuyerId(buyerId);
        bid.setAuctionId(auctionId);
        bid.setStatus(status);
        return bid;
    }

    private void saveBids(Bid... bids) {
        for (Bid bid : bids) {
            bidsRepository.save(bid);
        }
    }
}

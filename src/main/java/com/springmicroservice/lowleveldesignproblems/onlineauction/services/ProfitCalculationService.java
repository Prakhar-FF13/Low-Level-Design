package com.springmicroservice.lowleveldesignproblems.onlineauction.services;

import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Auction;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.AuctionStatus;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Bid;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.BidsRepository;
import com.springmicroservice.lowleveldesignproblems.onlineauction.strategies.WinningStrategy;

public class ProfitCalculationService {
    private final WinningStrategy winningStrategy;
    private final BidsRepository bidsRepository;

    public ProfitCalculationService(WinningStrategy winningStrategy, BidsRepository bidsRepository) {
        this.winningStrategy = winningStrategy;
        this.bidsRepository = bidsRepository;
    }

    public double calculateProfit(Auction auction) {
        if (auction == null || auction.getId() == null || auction.getStatus() != AuctionStatus.CLOSED) {
            throw new IllegalArgumentException("Invalid auction");
        }
        long numBidders = bidsRepository.findByAuctionId(auction.getId()).stream()
                .map(Bid::getBuyerId)
                .distinct()
                .count();
        Optional<Bid> winningBid = winningStrategy.getWinningBid(auction);
        if (winningBid.isEmpty()) {
            return numBidders * 0.2 * auction.getParticipationCost();
        }
        double avg = (auction.getLowestBidLimit() + auction.getHighestBidLimit()) / 2;
        return winningBid.get().getAmount() + (numBidders * 0.2 * auction.getParticipationCost()) - avg;
    }
}

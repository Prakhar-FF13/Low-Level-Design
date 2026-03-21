package com.springmicroservice.lowleveldesignproblems.onlineauction.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Auction;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.AuctionStatus;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Bid;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.BidStatus;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.User;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.UserType;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.AuctionRepository;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.BidsRepository;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.UserRepository;

public class BidsService {
    private final BidsRepository bidsRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    public BidsService(BidsRepository bidsRepository, AuctionRepository auctionRepository, UserRepository userRepository) {
        this.bidsRepository = bidsRepository;
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
    }

    public Bid createBid(double amount, String auctionId, String buyerId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new IllegalArgumentException("Auction is not open");
        }
        if (amount < auction.getLowestBidLimit() || amount > auction.getHighestBidLimit()) {
            throw new IllegalArgumentException("Bid amount is not within the auction limits");
        }

        User user = userRepository.findById(buyerId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getUserType() != UserType.BUYER) {
            throw new IllegalArgumentException("User is not a buyer");
        }
        if (auction.getBuyerParticipation() == null) {
            auction.setBuyerParticipation(new java.util.HashMap<>());
        }
        auction.getBuyerParticipation().put(buyerId, true);
        Bid bid = new Bid(UUID.randomUUID().toString(), amount, buyerId, auctionId, BidStatus.ACTIVE);
        bidsRepository.save(bid);
        List<Bid> bids = auction.getBids();
        if (bids == null) {
            bids = new ArrayList<>();
            auction.setBids(bids);
        }
        bids.add(bid);
        auctionRepository.save(auction);
        return bid;
    }

    public Bid updateBid(String bidId, double amount) {
        Bid bid = bidsRepository.findById(bidId).orElseThrow(() -> new IllegalArgumentException("Bid not found"));
        if (bid.getStatus() != BidStatus.ACTIVE) {
            throw new IllegalArgumentException("Bid is not active");
        }
        Auction auction = auctionRepository.findById(bid.getAuctionId()).orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new IllegalArgumentException("Auction is not open");
        }
        if (amount < auction.getLowestBidLimit() || amount > auction.getHighestBidLimit()) {
            throw new IllegalArgumentException("Bid amount is not within the auction limits");
        }
        bid.setAmount(amount);
        bidsRepository.save(bid);
        return bid;
    }

    public Bid withdrawBid(String bidId) {
        Bid bid = bidsRepository.findById(bidId).orElseThrow(() -> new IllegalArgumentException("Bid not found"));
        if (bid.getStatus() != BidStatus.ACTIVE) {
            throw new IllegalArgumentException("Bid is not active");
        }
        Auction auction = auctionRepository.findById(bid.getAuctionId()).orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new IllegalArgumentException("Auction is not open");
        }
        if (auction.getBuyerParticipation() == null || !Boolean.TRUE.equals(auction.getBuyerParticipation().get(bid.getBuyerId()))) {
            throw new IllegalArgumentException("Buyer has not participated in the auction");
        }
        bid.setStatus(BidStatus.WITHDRAWN);
        bidsRepository.save(bid);
        List<Bid> bids = auction.getBids();
        if (bids != null) {
            bids.remove(bid);
        }
        auctionRepository.save(auction);
        return bid;    
    }
}

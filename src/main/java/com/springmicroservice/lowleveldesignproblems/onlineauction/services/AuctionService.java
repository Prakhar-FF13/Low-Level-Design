package com.springmicroservice.lowleveldesignproblems.onlineauction.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Auction;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.AuctionStatus;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Seller;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.AuctionRepository;

public class AuctionService {
    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public Auction createAuction(Seller seller, double lowestBidLimit, double highestBidLimit, String sellerId, double participationCost) {
        Auction auction = new Auction(
            UUID.randomUUID().toString(), 
            lowestBidLimit, 
            highestBidLimit,
            seller,
            participationCost,
            new HashMap<>(), 
            new ArrayList<>(),
            AuctionStatus.CREATED);
        return auctionRepository.save(auction);
    }

    public Auction closeAuction(String auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        if (auction.getStatus() != AuctionStatus.CREATED && auction.getStatus() != AuctionStatus.OPEN) {
            throw new IllegalArgumentException("Auction cannot be closed in current state");
        }
        auction.setStatus(AuctionStatus.CLOSED);
        return auctionRepository.save(auction);
    }

    public Auction openAuction(String auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        if (auction.getStatus() != AuctionStatus.CREATED) {
            throw new IllegalArgumentException("Auction is not created");
        }
        auction.setStatus(AuctionStatus.OPEN);
        return auctionRepository.save(auction);
    }
}

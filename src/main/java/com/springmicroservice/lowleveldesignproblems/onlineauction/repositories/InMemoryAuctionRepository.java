package com.springmicroservice.lowleveldesignproblems.onlineauction.repositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Auction;

public class InMemoryAuctionRepository implements AuctionRepository {
    private final Map<String, Auction> auctionsById = new ConcurrentHashMap<>();

    @Override
    public Auction save(Auction auction) {
        auctionsById.put(auction.getId(), auction);
        return auction;
    }

    @Override
    public Optional<Auction> findById(String id) {
        return Optional.ofNullable(auctionsById.get(id));
    }
}

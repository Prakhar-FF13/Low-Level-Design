package com.springmicroservice.lowleveldesignproblems.onlineauction.repositories;

import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Auction;

public interface AuctionRepository {
    Auction save(Auction auction);
    Optional<Auction> findById(String id);
}

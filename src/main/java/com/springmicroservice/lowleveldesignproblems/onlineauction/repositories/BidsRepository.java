package com.springmicroservice.lowleveldesignproblems.onlineauction.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Bid;

public interface BidsRepository {
    Bid save(Bid bid);
    Optional<Bid> findById(String id);
    List<Bid> findByAuctionId(String auctionId);

    /**
     * Returns buyer IDs who have participated in more than one auction.
     */
    Set<String> findPreferredBuyerIds();
}

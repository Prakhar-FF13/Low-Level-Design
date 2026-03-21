package com.springmicroservice.lowleveldesignproblems.onlineauction.repositories;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Bid;

public class InMemoryBidsRepository implements BidsRepository {
    private final Map<String, Bid> bidsById = new ConcurrentHashMap<>();

    @Override
    public Bid save(Bid bid) {
        bidsById.put(bid.getId(), bid);
        return bid;
    }

    @Override
    public Optional<Bid> findById(String id) {
        return Optional.ofNullable(bidsById.get(id));
    }

    @Override
    public List<Bid> findByAuctionId(String auctionId) {
        return bidsById.values().stream()
                .filter(b -> auctionId.equals(b.getAuctionId()))
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> findPreferredBuyerIds() {
        return bidsById.values().stream()
                .collect(Collectors.groupingBy(Bid::getBuyerId,
                        Collectors.mapping(Bid::getAuctionId, Collectors.toSet())))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}

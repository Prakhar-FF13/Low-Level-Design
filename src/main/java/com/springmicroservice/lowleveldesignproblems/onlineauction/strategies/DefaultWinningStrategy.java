package com.springmicroservice.lowleveldesignproblems.onlineauction.strategies;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Auction;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Bid;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.BidStatus;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.BidsRepository;

/**
 * Winner selection per requirements:
 * - Highest bid wins; amount must appear exactly once (unique), else no winner.
 * - Bonus: if multiple bidders at same highest amount, preferred buyer wins.
 * - If multiple preferred at same max, fallback to next highest unique bid.
 */
public class DefaultWinningStrategy implements WinningStrategy {

    private final BidsRepository bidsRepository;

    public DefaultWinningStrategy(BidsRepository bidsRepository) {
        this.bidsRepository = bidsRepository;
    }

    @Override
    public Optional<Bid> getWinningBid(Auction auction) {
        if (auction == null || auction.getId() == null) {
            return Optional.empty();
        }

        List<Bid> activeBids = bidsRepository.findByAuctionId(auction.getId()).stream()
                .filter(b -> b.getStatus() == BidStatus.ACTIVE)
                .collect(Collectors.toList());

        if (activeBids.isEmpty()) {
            return Optional.empty();
        }

        Set<String> preferredBuyerIds = bidsRepository.findPreferredBuyerIds();
        var amountToBids = activeBids.stream()
                .collect(Collectors.groupingBy(Bid::getAmount));

        double maxAmount = amountToBids.keySet().stream().max(Comparator.naturalOrder()).orElse(0.0);
        List<Bid> bidsAtMax = amountToBids.get(maxAmount);

        if (bidsAtMax.size() == 1) {
            return Optional.of(bidsAtMax.get(0));
        }

        List<Bid> preferredAtMax = bidsAtMax.stream()
                .filter(b -> preferredBuyerIds.contains(b.getBuyerId()))
                .collect(Collectors.toList());

        if (preferredAtMax.size() == 1) {
            return Optional.of(preferredAtMax.get(0));
        }

        if (preferredAtMax.isEmpty()) {
            return Optional.empty();
        }

        List<Double> uniqueAmounts = amountToBids.entrySet().stream()
                .filter(e -> e.getValue().size() == 1)
                .map(e -> e.getKey())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        for (Double amount : uniqueAmounts) {
            if (amount < maxAmount) {
                return Optional.of(amountToBids.get(amount).get(0));
            }
        }

        return Optional.empty();
    }
}

package com.springmicroservice.lowleveldesignproblems.onlineauction.strategies;

import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Auction;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Bid;

public interface WinningStrategy {
    Optional<Bid> getWinningBid(Auction auction);
}

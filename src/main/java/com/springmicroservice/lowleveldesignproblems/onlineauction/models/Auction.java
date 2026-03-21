package com.springmicroservice.lowleveldesignproblems.onlineauction.models;

import lombok.*;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auction {
    private String id;
    private double lowestBidLimit;
    private double highestBidLimit;
    private Seller seller;
    private double participationCost;
    private HashMap<String, Boolean> buyerParticipation;
    private List<Bid> bids;
    private AuctionStatus status;
}

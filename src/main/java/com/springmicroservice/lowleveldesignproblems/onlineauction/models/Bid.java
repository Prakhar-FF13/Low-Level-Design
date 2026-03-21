package com.springmicroservice.lowleveldesignproblems.onlineauction.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bid {
    private String id;
    private double amount;
    private String buyerId;
    private String auctionId;
    private BidStatus status;
}

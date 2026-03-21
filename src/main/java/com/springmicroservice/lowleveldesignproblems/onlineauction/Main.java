package com.springmicroservice.lowleveldesignproblems.onlineauction;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Auction;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Buyer;
import com.springmicroservice.lowleveldesignproblems.onlineauction.models.Seller;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.InMemoryAuctionRepository;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.InMemoryBidsRepository;
import com.springmicroservice.lowleveldesignproblems.onlineauction.repositories.InMemoryUserRespository;
import com.springmicroservice.lowleveldesignproblems.onlineauction.services.AuctionService;
import com.springmicroservice.lowleveldesignproblems.onlineauction.services.BidsService;
import com.springmicroservice.lowleveldesignproblems.onlineauction.services.ProfitCalculationService;
import com.springmicroservice.lowleveldesignproblems.onlineauction.strategies.DefaultWinningStrategy;

public class Main {
    public static void main(String[] args) {
        InMemoryAuctionRepository auctionRepository = new InMemoryAuctionRepository();
        InMemoryUserRespository userRepository = new InMemoryUserRespository();
        InMemoryBidsRepository bidsRepository = new InMemoryBidsRepository();

        Seller seller = new Seller("1", "John Doe", "john.doe@example.com", "password", "1234567890", "123 Main St", "Anytown", "CA", "12345", "USA");
        Buyer buyer1 = new Buyer("2", "Jane Doe", "jane.doe@example.com", "password", "1234567890", "123 Main St", "Anytown", "CA", "12345", "USA");
        Buyer buyer2 = new Buyer("3", "Jim Doe", "jim.doe@example.com", "password", "1234567890", "123 Main St", "Anytown", "CA", "12345", "USA");
        userRepository.save(seller);
        userRepository.save(buyer1);
        userRepository.save(buyer2);

        AuctionService auctionService = new AuctionService(auctionRepository);
        Auction auction = auctionService.createAuction(seller, 100, 200, "1", 10);
        auctionService.openAuction(auction.getId());

        BidsService bidsService = new BidsService(bidsRepository, auctionRepository, userRepository);
        bidsService.createBid(150, auction.getId(), "2");
        bidsService.createBid(150, auction.getId(), "3");

        auctionService.closeAuction(auction.getId());

        ProfitCalculationService profitCalculationService = new ProfitCalculationService(
                new DefaultWinningStrategy(bidsRepository), bidsRepository);
        double profit = profitCalculationService.calculateProfit(
                auctionRepository.findById(auction.getId()).orElseThrow(() -> new IllegalArgumentException("Auction not found")));
        System.out.println("Seller profit/loss: " + profit);
    }
}

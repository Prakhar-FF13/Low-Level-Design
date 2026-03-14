package com.springmicroservice.lowleveldesignproblems.parkinglot.strategies;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Ticket;

public interface PaymentStrategy {
    double calculateFee(Ticket ticket);
}

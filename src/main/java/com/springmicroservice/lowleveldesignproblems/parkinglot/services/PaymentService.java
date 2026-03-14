package com.springmicroservice.lowleveldesignproblems.parkinglot.services;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Receipt;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Ticket;
import com.springmicroservice.lowleveldesignproblems.parkinglot.strategies.PaymentStrategy;

public class PaymentService {
    private final PaymentStrategy paymentStrategy;

    public PaymentService(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public Receipt processPayment(Ticket ticket) {
        double fee = paymentStrategy.calculateFee(ticket);
        return new Receipt("REC-" + System.currentTimeMillis() + "-" + ticket.getCarNumber(), ticket, fee);
    }
}

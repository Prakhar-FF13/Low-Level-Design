package com.springmicroservice.lowleveldesignproblems.parkinglot.strategies;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Ticket;

public class HourlyPaymentStrategy implements PaymentStrategy {
    private final double hourlyRate;

    public HourlyPaymentStrategy(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double calculateFee(Ticket ticket) {
        if (ticket.getOutTime() == null) {
            throw new IllegalArgumentException("Ticket does not have an out time");
        }

        long durationInMillis = ticket.getOutTime() - ticket.getInTime();
        double hours = Math.ceil((double) durationInMillis / (1000 * 60 * 60));
        
        // Ensure at least 1 hour is charged
        if (hours == 0) {
            hours = 1;
        }

        return hours * hourlyRate;
    }
}

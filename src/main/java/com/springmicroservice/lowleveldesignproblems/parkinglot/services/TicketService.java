package com.springmicroservice.lowleveldesignproblems.parkinglot.services;

import com.springmicroservice.lowleveldesignproblems.parkinglot.exceptions.InvalidTicketException;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Ticket;

import java.util.HashMap;
import java.util.Map;

public class TicketService {
    private final Map<String, Ticket> activeTickets = new HashMap<>();

    public void saveTicket(Ticket ticket) {
        activeTickets.put(ticket.getCarNumber(), ticket);
    }

    public Ticket getTicket(String vehicleNumber) {
        Ticket ticket = activeTickets.get(vehicleNumber);
        if (ticket == null) {
            throw new InvalidTicketException("Found a vehicle exiting the parking lot but without a ticket: " + vehicleNumber);
        }
        return ticket;
    }

    public void removeTicket(String vehicleNumber) {
        activeTickets.remove(vehicleNumber);
    }
}

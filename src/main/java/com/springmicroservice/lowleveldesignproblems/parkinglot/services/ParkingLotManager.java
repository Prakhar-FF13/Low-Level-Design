package com.springmicroservice.lowleveldesignproblems.parkinglot.services;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.ParkingLot;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Slot;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Receipt;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Ticket;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Vehicle;
import com.springmicroservice.lowleveldesignproblems.parkinglot.strategies.SlotDeterminingStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ParkingLotManager {
    private ParkingLot parkingLot;
    private SlotDeterminingStrategy slotDeterminingStrategy;
    private TicketService ticketService;
    private PaymentService paymentService;
    private EventPublisher eventPublisher;

    public Ticket getFreeSlot(Vehicle vehicle) {
        Slot parkingSlot = slotDeterminingStrategy.determineSlot(vehicle, parkingLot);
        Ticket ticket = new Ticket(
                vehicle.getVehicleNum(),
                parkingSlot,
                System.currentTimeMillis());
        
        ticketService.saveTicket(ticket);
        eventPublisher.publishVehicleParked(parkingSlot.getType());
        
        return ticket;
    }

    public Receipt getReceipt(Vehicle vehicle) {
        Ticket ticket = ticketService.getTicket(vehicle.getVehicleNum());
        
        ticket.setOutTime(System.currentTimeMillis());
        slotDeterminingStrategy.freeSlot(ticket.getSlot());
        ticketService.removeTicket(vehicle.getVehicleNum());
        
        eventPublisher.publishVehicleUnparked(ticket.getSlot().getType());

        return paymentService.processPayment(ticket);
    }
}

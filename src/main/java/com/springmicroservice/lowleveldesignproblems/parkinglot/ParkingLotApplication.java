package com.springmicroservice.lowleveldesignproblems.parkinglot;

import com.springmicroservice.lowleveldesignproblems.parkinglot.exceptions.InvalidTicketException;
import com.springmicroservice.lowleveldesignproblems.parkinglot.exceptions.ParkingLotFullException;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.*;
import com.springmicroservice.lowleveldesignproblems.parkinglot.services.*;
import com.springmicroservice.lowleveldesignproblems.parkinglot.strategies.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ParkingLotApplication {

    private final ParkingLotManager manager;

    public ParkingLotApplication() {
        this.manager = initializeParkingLotSystem();
    }

    public static void main(String[] args) {
        ParkingLotApplication app = new ParkingLotApplication();
        app.startInteractiveShell();
    }

    private ParkingLotManager initializeParkingLotSystem() {
        // 1. Setup the Domain Models (The physical lot)
        ParkingLot parkingLot = new ParkingLot();

        List<ParkingFloor> floors = new ArrayList<>();
        ParkingFloor floor1 = new ParkingFloor();
        floor1.setParkingFloorId(1L);

        List<Slot> slots = new ArrayList<>();

        // Add 2 Standard Car Slots
        slots.add(createCarSlot(101L, Set.of(ParkingFeature.CAR_SIZE)));
        slots.add(createCarSlot(102L, Set.of(ParkingFeature.CAR_SIZE)));

        // Add 1 EV Charger Slot
        slots.add(createCarSlot(103L, Set.of(ParkingFeature.CAR_SIZE, ParkingFeature.EV_CHARGER)));

        // Add 1 Bike Slot
        slots.add(createCarSlot(104L, Set.of(ParkingFeature.BIKE_SIZE)));

        floor1.setSlots(slots);
        floors.add(floor1);
        parkingLot.setParkingFloors(floors);

        // 2. Setup the Services
        TicketService ticketService = new TicketService();
        PaymentService paymentService = new PaymentService(new HourlyPaymentStrategy(10.0));
        EventPublisher eventPublisher = new EventPublisher();

        // 3. Setup the real-time Display Observer
        DisplayBoard displayBoard = new DisplayBoard();
        displayBoard.initializeSlots(SlotType.CAR, 3);
        displayBoard.initializeSlots(SlotType.BIKE, 1);

        DisplayBoardUpdater updater = new DisplayBoardUpdater(displayBoard);
        eventPublisher.addListener(updater);

        // 4. Create the Facade
        return new ParkingLotManager(
                parkingLot,
                new FirstFreeSlotStrategy(),
                ticketService,
                paymentService,
                eventPublisher);
    }

    private Slot createCarSlot(Long id, Set<ParkingFeature> features) {
        return new Slot() {
            private Vehicle parkedVehicle = null;

            @Override
            public Long getId() {
                return id;
            }

            @Override
            public int getWidth() {
                return 10;
            }

            @Override
            public int getHeight() {
                return 20;
            }

            @Override
            public SlotType getType() {
                return features.contains(ParkingFeature.BIKE_SIZE) ? SlotType.BIKE : SlotType.CAR;
            }

            @Override
            public Set<ParkingFeature> getProvidedFeatures() {
                return features;
            }

            @Override
            public boolean isFree() {
                return parkedVehicle == null;
            }

            @Override
            public void assignVehicle(Vehicle vehicle) {
                this.parkedVehicle = vehicle;
            }

            @Override
            public void freeSlot() {
                this.parkedVehicle = null;
            }
        };
    }

    private void startInteractiveShell() {
        System.out.println("==================================================");
        System.out.println("=        PARKING LOT SYSTEM INITIALIZED          =");
        System.out.println("==================================================");
        System.out.println("Available commands:");
        System.out.println("  1. PARK <VehicleType> <RegistrationNumber>");
        System.out.println("     Types: CAR, BIKE, ELECTRIC_CAR");
        System.out.println("  2. UNPARK <RegistrationNumber>");
        System.out.println("  3. EXIT");
        System.out.println("==================================================\n");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("EXIT")) {
                System.out.println("Shutting down the parking lot system. Goodbye!");
                break;
            }

            String[] parts = input.split(" ");
            String command = parts[0].toUpperCase();

            try {
                switch (command) {
                    case "PARK":
                        handlePark(parts);
                        break;
                    case "UNPARK":
                        handleUnpark(parts);
                        break;
                    default:
                        System.out.println("Invalid command. Use PARK, UNPARK, or EXIT.");
                }
            } catch (ParkingLotFullException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (InvalidTicketException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void handlePark(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Usage: PARK <VehicleType> <RegistrationNumber>");
            return;
        }

        VehicleType type;
        try {
            type = VehicleType.valueOf(parts[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Vehicle Type. Must be CAR, BIKE, or ELECTRIC_CAR");
            return;
        }

        String regNumber = parts[2];
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNum(regNumber);
        vehicle.setVehicleType(type);

        Ticket ticket = manager.getFreeSlot(vehicle);
        System.out.println("\n✅ SUCCESS: Vehicle successfully parked!");
        System.out.println("----------------------------------------");
        System.out.println("Ticket ID: " + ticket.getCarNumber() + "-" + ticket.getInTime());
        System.out.println("Slot ID: " + ticket.getSlot().getId() + " (" + ticket.getSlot().getType() + ")");
        System.out.println("Provided Features: " + ticket.getSlot().getProvidedFeatures());
        System.out.println("----------------------------------------");
    }

    private void handleUnpark(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Usage: UNPARK <RegistrationNumber>");
            return;
        }

        String regNumber = parts[1];

        // We only need the registration number to unpark, we pass a mockup vehicle.
        Vehicle tempVehicle = new Vehicle();
        tempVehicle.setVehicleNum(regNumber);

        Receipt receipt = manager.getReceipt(tempVehicle);

        System.out.println("\n✅ SUCCESS: Vehicle successfully unparked!");
        System.out.println("----------------------------------------");
        System.out.println("Receipt No: " + receipt.getReceiptNumber());
        System.out.println("Vehicle No: " + receipt.getTicket().getCarNumber());
        System.out.println("Fee Charged: $" + receipt.getFee());
        System.out.println("----------------------------------------");
    }
}

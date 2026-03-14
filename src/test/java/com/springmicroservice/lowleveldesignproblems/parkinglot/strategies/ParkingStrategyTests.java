package com.springmicroservice.lowleveldesignproblems.parkinglot.strategies;

import com.springmicroservice.lowleveldesignproblems.parkinglot.exceptions.ParkingLotFullException;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ParkingStrategyTests {

    private FirstFreeSlotStrategy strategy;
    private ParkingLot parkingLot;
    private ParkingFloor floor1;

    @BeforeEach
    void setUp() {
        // 1. Initialize our strategy instance before each test
        strategy = new FirstFreeSlotStrategy();
        
        // 2. Set up our mock parking lot structure
        parkingLot = new ParkingLot();
        
        // 3. Create a floor to hold our slots
        floor1 = new ParkingFloor();
        floor1.setParkingFloorId(1L);
        floor1.setSlots(new ArrayList<>()); // We will add specific slots per test
        
        // 4. Mount the floor into the Lot
        List<ParkingFloor> floors = new ArrayList<>();
        floors.add(floor1);
        parkingLot.setParkingFloors(floors);
    }

    /**
     * Nested class specifically testing the CompatibilityEngine mathematically.
     * We want to isolate the subset matching logic.
     */
    @Nested
    @DisplayName("Compatibility Engine (Subset Matching) Tests")
    class CompatibilityEngineTests {

        @Test
        @DisplayName("A CAR fits into a Standard Car Slot")
        void testCarFitsStandardSlot() {
            // Arrange
            Vehicle car = createVehicle("MH-04-1234", VehicleType.CAR);
            Slot standardSlot = createMockSlot(1L, Set.of(ParkingFeature.CAR_SIZE));

            // Act & Assert
            assertTrue(CompatibilityEngine.canFit(car, standardSlot), 
                "Car requires [CAR_SIZE], Slot provides [CAR_SIZE] -> Should be True");
        }

        @Test
        @DisplayName("An ELECTRIC_CAR fits into an EV Charger Slot")
        void testElectricCarFitsEVSlot() {
            // Arrange
            Vehicle ev = createVehicle("EV-1", VehicleType.ELECTRIC_CAR);
            Slot evSlot = createMockSlot(2L, Set.of(ParkingFeature.CAR_SIZE, ParkingFeature.EV_CHARGER));

            // Act & Assert
            // EV needs [CAR_SIZE, EV_CHARGER]. Slot provides [CAR_SIZE, EV_CHARGER].
            assertTrue(CompatibilityEngine.canFit(ev, evSlot), 
                "EV requires [CAR_SIZE, EV_CHARGER], Slot provides [CAR_SIZE, EV_CHARGER] -> Should be True");
        }

        @Test
        @DisplayName("An ELECTRIC_CAR does NOT fit into a Standard Car Slot")
        void testElectricCarFailsStandardSlot() {
            // Arrange
            Vehicle ev = createVehicle("EV-1", VehicleType.ELECTRIC_CAR);
            Slot standardSlot = createMockSlot(3L, Set.of(ParkingFeature.CAR_SIZE));

            // Act & Assert
            // EV needs [CAR_SIZE, EV_CHARGER]. Slot only provides [CAR_SIZE].
            assertFalse(CompatibilityEngine.canFit(ev, standardSlot), 
                "EV requires [CAR_SIZE, EV_CHARGER], Slot only provides [CAR_SIZE] -> Should be False (Missing EV_CHARGER)");
        }

        @Test
        @DisplayName("A STANDARD CAR CAN fit into an EV Charger Slot (but we prefer not to)")
        void testStandardCarFitsInEVSlot() {
            // Arrange
            Vehicle normalCar = createVehicle("CAR-1", VehicleType.CAR);
            Slot evSlot = createMockSlot(4L, Set.of(ParkingFeature.CAR_SIZE, ParkingFeature.EV_CHARGER));

            // Act & Assert
            // Car needs [CAR_SIZE]. Slot provides [CAR_SIZE, EV_CHARGER]. 
            // Subset matching mathematically works here because [CAR_SIZE] is present in the slot.
            assertTrue(CompatibilityEngine.canFit(normalCar, evSlot), 
                "Car requires [CAR_SIZE], Slot provides [CAR_SIZE, EV_CHARGER] -> Should be True (Subset is satisfied)");
        }
    }

    /**
     * Nested class testing the FirstFreeSlotStrategy logic.
     * We want to verify it searches floors properly and prioritizes Best Fit.
     */
    @Nested
    @DisplayName("First Free Slot Strategy (Best Fit) Tests")
    class StrategyLogicTests {

        @Test
        @DisplayName("Best Fit Routing: Normal Car should prefer a Standard Slot over an EV slot")
        void testNormalCarPrefersStandardSlot() {
            // Arrange
            // Create Floor 1 with an EV Slot at index 0, and a Standard Slot at index 1.
            Slot evSlot = createMockSlot(101L, Set.of(ParkingFeature.CAR_SIZE, ParkingFeature.EV_CHARGER));
            Slot standardSlot = createMockSlot(102L, Set.of(ParkingFeature.CAR_SIZE));
            
            floor1.getSlots().add(evSlot);
            floor1.getSlots().add(standardSlot);

            Vehicle normalCar = createVehicle("CAR-1", VehicleType.CAR);

            // Act
            // The strategy iterates. It sees the EV slot fits (1 wasted feature). 
            // It sees the Standard slot fits (0 wasted features -> Perfect Match).
            Slot assignedSlot = strategy.determineSlot(normalCar, parkingLot);

            // Assert
            assertEquals(102L, assignedSlot.getId(), "Normal car should bypass the EV slot to take the perfect-fit Standard slot.");
            assertFalse(standardSlot.isFree(), "The standard slot should now be marked as occupied.");
            assertTrue(evSlot.isFree(), "The EV slot should remain free for an actual EV.");
        }

        @Test
        @DisplayName("Best Fit Routing: EV should take the EV slot instantly")
        void testEVTakesEVSlot() {
            // Arrange
            Slot standardSlot = createMockSlot(101L, Set.of(ParkingFeature.CAR_SIZE));
            Slot evSlot = createMockSlot(102L, Set.of(ParkingFeature.CAR_SIZE, ParkingFeature.EV_CHARGER));
            
            floor1.getSlots().add(standardSlot);
            floor1.getSlots().add(evSlot);

            Vehicle ev = createVehicle("EV-1", VehicleType.ELECTRIC_CAR);

            // Act
            Slot assignedSlot = strategy.determineSlot(ev, parkingLot);

            // Assert
            assertEquals(102L, assignedSlot.getId(), "EV MUST take the EV slot since it's the only one that satisfies its required capabilities.");
        }

        @Test
        @DisplayName("Throws Exception when Parking Lot is Full or no compatible slots exist")
        void testParkingLotFullException() {
            // Arrange: The only slot is a Bike Slot
            Slot bikeSlot = createMockSlot(101L, Set.of(ParkingFeature.BIKE_SIZE));
            floor1.getSlots().add(bikeSlot);

            Vehicle car = createVehicle("CAR-1", VehicleType.CAR);

            // Act & Assert
            // The lot isn't strictly empty, but it's "Full" from the perspective of a Car.
            assertThrows(ParkingLotFullException.class, () -> {
                strategy.determineSlot(car, parkingLot);
            }, "Should throw ParkingLotFullException because no CAR_SIZE capable slots exist.");
        }
    }

    // --- Utility Methods for Testing --- 

    private Vehicle createVehicle(String registration, VehicleType type) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNum(registration);
        vehicle.setVehicleType(type);
        return vehicle;
    }

    private Slot createMockSlot(Long id, Set<ParkingFeature> features) {
        // We use an anonymous inner class to mock the Slot interface quickly for tests
        return new Slot() {
            private Vehicle parkedVehicle = null;

            @Override public Long getId() { return id; }
            @Override public int getWidth() { return 10; }
            @Override public int getHeight() { return 10; }
            @Override public SlotType getType() { return SlotType.CAR; } // Not used by capability engine anymore
            @Override public Set<ParkingFeature> getProvidedFeatures() { return features; }
            @Override public boolean isFree() { return parkedVehicle == null; }
            @Override public void assignVehicle(Vehicle vehicle) { this.parkedVehicle = vehicle; }
            @Override public void freeSlot() { this.parkedVehicle = null; }
        };
    }
}

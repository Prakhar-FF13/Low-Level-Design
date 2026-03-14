package com.springmicroservice.lowleveldesignproblems.parkinglot.strategies;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.ParkingFloor;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.ParkingLot;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Slot;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Vehicle;
import com.springmicroservice.lowleveldesignproblems.parkinglot.exceptions.ParkingLotFullException;

import java.util.List;

public class FirstFreeSlotStrategy implements SlotDeterminingStrategy {

    @Override
    public Slot determineSlot(Vehicle vehicle, ParkingLot parkingLot) {
        List<ParkingFloor> parkingFloors = parkingLot.getParkingFloors();
        
        Slot bestSlot = null;
        int minExtraFeatures = Integer.MAX_VALUE;

        for (ParkingFloor parkingFloor : parkingFloors) {
            for (Slot slot : parkingFloor.getSlots()) {
                if (slot.isFree() && slot.canFit(vehicle)) {
                    int extraFeatures = slot.getProvidedFeatures().size() - vehicle.getVehicleType().getRequiredFeatures().size();
                    
                    if (extraFeatures < minExtraFeatures) {
                        bestSlot = slot;
                        minExtraFeatures = extraFeatures;
                        
                        // Exact match found, no need to keep searching
                        if (extraFeatures == 0) {
                            break;
                        }
                    }
                }
            }
            if (bestSlot != null && minExtraFeatures == 0) {
                break;
            }
        }

        if (bestSlot != null) {
            bestSlot.assignVehicle(vehicle);
            return bestSlot;
        }

        throw new ParkingLotFullException("Slot not found for vehicle " + vehicle.getVehicleNum());
    }

    @Override
    public void freeSlot(Slot slot) {
        slot.freeSlot();
    }
}

package com.springmicroservice.lowleveldesignproblems.parkinglot.strategies;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.ParkingLot;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Slot;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Vehicle;

public interface SlotDeterminingStrategy {
    Slot determineSlot(Vehicle vehicle, ParkingLot parkingLot);
    void freeSlot(Slot slot);
}

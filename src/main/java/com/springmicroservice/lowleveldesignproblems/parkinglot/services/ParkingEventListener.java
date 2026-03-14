package com.springmicroservice.lowleveldesignproblems.parkinglot.services;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.SlotType;

public interface ParkingEventListener {
    void onVehicleParked(SlotType slotType);
    void onVehicleUnparked(SlotType slotType);
}

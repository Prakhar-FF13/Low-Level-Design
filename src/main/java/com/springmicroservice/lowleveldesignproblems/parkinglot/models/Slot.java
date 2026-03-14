package com.springmicroservice.lowleveldesignproblems.parkinglot.models;

import java.util.Set;

public interface Slot {
    Long getId();
    int getWidth();
    int getHeight();
    SlotType getType();
    boolean isFree();
    void assignVehicle(Vehicle vehicle);
    void freeSlot();
    Set<ParkingFeature> getProvidedFeatures();

    default boolean canFit(Vehicle vehicle) {
        return com.springmicroservice.lowleveldesignproblems.parkinglot.strategies.CompatibilityEngine.canFit(vehicle, this);
    }
}

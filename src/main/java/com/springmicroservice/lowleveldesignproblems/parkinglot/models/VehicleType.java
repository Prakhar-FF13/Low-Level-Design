package com.springmicroservice.lowleveldesignproblems.parkinglot.models;

import java.util.Set;

public enum VehicleType {
    CAR(Set.of(ParkingFeature.CAR_SIZE)),
    BIKE(Set.of(ParkingFeature.BIKE_SIZE)),
    ELECTRIC_CAR(Set.of(ParkingFeature.CAR_SIZE, ParkingFeature.EV_CHARGER));

    private final Set<ParkingFeature> requiredFeatures;

    VehicleType(Set<ParkingFeature> requiredFeatures) {
        this.requiredFeatures = requiredFeatures;
    }

    public Set<ParkingFeature> getRequiredFeatures() {
        return requiredFeatures;
    }
}

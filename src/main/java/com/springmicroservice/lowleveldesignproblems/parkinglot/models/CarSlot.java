package com.springmicroservice.lowleveldesignproblems.parkinglot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;
import java.util.HashSet;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarSlot implements Slot {
    int width;
    int height;

    private Long id;
    private Vehicle parkedVehicle;
    private Set<ParkingFeature> providedFeatures = new HashSet<>(Set.of(ParkingFeature.CAR_SIZE));
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public SlotType getType() {
        return SlotType.CAR;
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

    @Override
    public Set<ParkingFeature> getProvidedFeatures() {
        return providedFeatures;
    }
}

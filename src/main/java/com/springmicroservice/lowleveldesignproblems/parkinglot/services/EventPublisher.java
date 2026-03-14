package com.springmicroservice.lowleveldesignproblems.parkinglot.services;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.SlotType;
import java.util.ArrayList;
import java.util.List;

public class EventPublisher {
    private final List<ParkingEventListener> listeners = new ArrayList<>();

    public void addListener(ParkingEventListener listener) {
        this.listeners.add(listener);
    }

    public void publishVehicleParked(SlotType type) {
        for (ParkingEventListener listener : listeners) {
            listener.onVehicleParked(type);
        }
    }

    public void publishVehicleUnparked(SlotType type) {
        for (ParkingEventListener listener : listeners) {
            listener.onVehicleUnparked(type);
        }
    }
}

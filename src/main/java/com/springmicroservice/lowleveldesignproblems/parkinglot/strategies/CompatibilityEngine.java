package com.springmicroservice.lowleveldesignproblems.parkinglot.strategies;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Slot;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.Vehicle;

public class CompatibilityEngine {
    
    public static boolean canFit(Vehicle vehicle, Slot slot) {
        if (vehicle == null || vehicle.getVehicleType() == null || slot == null || slot.getProvidedFeatures() == null) {
            return false;
        }
        
        // Subset matching: The slot must provide AT LEAST everything the vehicle requires.
        return slot.getProvidedFeatures().containsAll(vehicle.getVehicleType().getRequiredFeatures());
    }
}

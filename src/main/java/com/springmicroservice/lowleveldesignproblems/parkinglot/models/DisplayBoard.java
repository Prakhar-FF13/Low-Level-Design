package com.springmicroservice.lowleveldesignproblems.parkinglot.models;

import java.util.HashMap;
import java.util.Map;

public class DisplayBoard {
    private final Map<SlotType, Integer> availableSlots;

    public DisplayBoard() {
        this.availableSlots = new HashMap<>();
    }

    public void initializeSlots(SlotType type, int count) {
        availableSlots.put(type, count);
    }

    public void incrementAvailableSlots(SlotType type) {
        availableSlots.put(type, availableSlots.getOrDefault(type, 0) + 1);
    }

    public void decrementAvailableSlots(SlotType type) {
        availableSlots.put(type, Math.max(0, availableSlots.getOrDefault(type, 0) - 1));
    }

    public int getAvailableSlots(SlotType type) {
        return availableSlots.getOrDefault(type, 0);
    }
    
    public void show() {
        System.out.println("--- Display Board ---");
        for (Map.Entry<SlotType, Integer> entry : availableSlots.entrySet()) {
            System.out.println(entry.getKey() + " Spots Free: " + entry.getValue());
        }
        System.out.println("---------------------");
    }
}

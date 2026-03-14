package com.springmicroservice.lowleveldesignproblems.parkinglot.services;

import com.springmicroservice.lowleveldesignproblems.parkinglot.models.DisplayBoard;
import com.springmicroservice.lowleveldesignproblems.parkinglot.models.SlotType;

public class DisplayBoardUpdater implements ParkingEventListener {
    private final DisplayBoard displayBoard;

    public DisplayBoardUpdater(DisplayBoard displayBoard) {
        this.displayBoard = displayBoard;
    }

    @Override
    public void onVehicleParked(SlotType slotType) {
        displayBoard.decrementAvailableSlots(slotType);
        displayBoard.show();
    }

    @Override
    public void onVehicleUnparked(SlotType slotType) {
        displayBoard.incrementAvailableSlots(slotType);
        displayBoard.show();
    }
}

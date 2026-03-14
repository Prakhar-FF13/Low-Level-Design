package com.springmicroservice.lowleveldesignproblems.parkinglot.exceptions;

public class ParkingLotFullException extends RuntimeException {
    public ParkingLotFullException(String message) {
        super(message);
    }
}

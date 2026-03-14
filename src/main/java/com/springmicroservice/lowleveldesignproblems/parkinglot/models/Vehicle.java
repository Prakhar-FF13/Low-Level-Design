package com.springmicroservice.lowleveldesignproblems.parkinglot.models;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


@Getter
@Setter
public class Vehicle {
    private Long id;

    @NonNull
    String vehicleNum;

    @NonNull
    VehicleType vehicleType;
}

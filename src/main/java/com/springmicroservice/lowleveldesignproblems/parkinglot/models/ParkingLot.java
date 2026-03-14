package com.springmicroservice.lowleveldesignproblems.parkinglot.models;


import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLot {
    private Long parkingLotId;


    @NonNull
    private List<ParkingFloor> parkingFloors;
}

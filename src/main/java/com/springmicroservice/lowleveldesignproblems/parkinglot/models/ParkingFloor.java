package com.springmicroservice.lowleveldesignproblems.parkinglot.models;


import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParkingFloor {
    private Long parkingFloorId;


    @NonNull
    private List<Slot> slots;
}

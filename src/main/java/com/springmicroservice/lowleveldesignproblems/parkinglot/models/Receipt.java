package com.springmicroservice.lowleveldesignproblems.parkinglot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Receipt {
    private String receiptNumber;
    private Ticket ticket;
    private double fee;
}

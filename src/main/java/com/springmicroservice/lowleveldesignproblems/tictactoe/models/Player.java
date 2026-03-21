package com.springmicroservice.lowleveldesignproblems.tictactoe.models;

import lombok.Data;
import lombok.NonNull;

@Data
public class Player {
    @NonNull
    private String name;
    @NonNull
    private String symbol;
}

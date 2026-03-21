package com.springmicroservice.lowleveldesignproblems.tictactoe.models;

import lombok.Data;
import lombok.NonNull;

@Data
public class Move {
    @NonNull
    Player player;

    int row;
    int column;

    public Move(Player player, int row, int column) {
        this.player = player;
        this.row = row;
        this.column = column;
    }
}

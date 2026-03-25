package com.springmicroservice.lowleveldesignproblems.chess.models.helpers;

import java.util.Objects;

/**
 * A single ply: relocate the piece currently on {@link #from} to {@link #to}.
 */
public record Move(Square from, Square to) {

    public Move {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
    }
}

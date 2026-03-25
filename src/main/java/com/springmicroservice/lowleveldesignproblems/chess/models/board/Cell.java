package com.springmicroservice.lowleveldesignproblems.chess.models.board;

import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.ChessPiece;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cell {
    Square coordinate;
    Optional<ChessPiece> piece;

    public void placePiece(ChessPiece piece) {
        this.piece = Optional.of(piece);
    }

    public void removePiece() {
        this.piece = Optional.empty();
    }

    public boolean isEmpty() {
        return piece.isEmpty();
    }

    public boolean isOccupied() {
        return piece.isPresent();
    }
}

package com.springmicroservice.lowleveldesignproblems.tictactoe.strategies;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Board;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;

public interface WinningStrategy {
    boolean isWinningMove(Board board, Move move);
}

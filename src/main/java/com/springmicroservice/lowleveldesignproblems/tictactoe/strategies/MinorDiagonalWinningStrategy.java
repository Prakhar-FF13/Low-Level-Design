package com.springmicroservice.lowleveldesignproblems.tictactoe.strategies;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Board;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;

public class MinorDiagonalWinningStrategy implements WinningStrategy {

    @Override
    public boolean isWinningMove(Board board, Move move) {
        int row = move.getRow();
        int col = move.getColumn();
        int size = board.getSize();
        if (row + col != size - 1) return false;

        String symbol = move.getPlayer().getSymbol();
        int winLength = board.getWinLength();

        int startIdx = Math.max(0, row - winLength + 1);
        int endIdx = Math.min(row, size - winLength);

        for (int i = startIdx; i <= endIdx; i++) {
            boolean allMatch = true;
            for (int k = 0; k < winLength; k++) {
                if (!symbol.equals(board.getCell(i + k, size - 1 - i - k))) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) return true;
        }
        return false;
    }
}

package com.springmicroservice.lowleveldesignproblems.tictactoe.strategies;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Board;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;

public class RowWinningStrategy implements WinningStrategy {

    @Override
    public boolean isWinningMove(Board board, Move move) {
        int row = move.getRow();
        int col = move.getColumn();
        String symbol = move.getPlayer().getSymbol();
        int winLength = board.getWinLength();
        int size = board.getSize();

        int startCol = Math.max(0, col - winLength + 1);
        int endCol = Math.min(col, size - winLength);

        for (int j = startCol; j <= endCol; j++) {
            boolean allMatch = true;
            for (int k = 0; k < winLength; k++) {
                if (!symbol.equals(board.getCell(row, j + k))) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) return true;
        }
        return false;
    }
}

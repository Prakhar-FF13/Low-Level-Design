package com.springmicroservice.lowleveldesignproblems.tictactoe.strategies;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Board;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;

public class ColumnWinningStrategy implements WinningStrategy {

    @Override
    public boolean isWinningMove(Board board, Move move) {
        int row = move.getRow();
        int col = move.getColumn();
        String symbol = move.getPlayer().getSymbol();
        int winLength = board.getWinLength();
        int size = board.getSize();

        int startRow = Math.max(0, row - winLength + 1);
        int endRow = Math.min(row, size - winLength);

        for (int i = startRow; i <= endRow; i++) {
            boolean allMatch = true;
            for (int k = 0; k < winLength; k++) {
                if (!symbol.equals(board.getCell(i + k, col))) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) return true;
        }
        return false;
    }
}

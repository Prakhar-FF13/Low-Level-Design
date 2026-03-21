package com.springmicroservice.lowleveldesignproblems.tictactoe.models;

import com.springmicroservice.lowleveldesignproblems.tictactoe.exceptions.GameException;

public class Board {
    private static final String EMPTY_CELL = " ";

    private final int size;
    private final int winLength;
    private final String[][] board;

    public Board(int size) {
        this(size, Math.min(3, size));
    }

    public Board(int size, int winLength) {
        if (size <= 0) {
            throw new GameException("Size must be greater than 0");
        }
        if (winLength < 2 || winLength > size) {
            throw new GameException("Win length must be between 2 and board size");
        }
        this.size = size;
        this.winLength = winLength;
        this.board = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = EMPTY_CELL;
            }
        }
    }

    public void printBoard() {
        int cellWidth = 3;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String cell = board[i][j];
                String display = EMPTY_CELL.equals(cell) ? " " : cell;
                System.out.print(" " + String.format("%-" + cellWidth + "s", display) + " ");
                if (j < size - 1) System.out.print("|");
            }
            System.out.println();
            if (i < size - 1) {
                System.out.println("-".repeat(size * (cellWidth + 4) - 1));
            }
        }
    }

    /**
     * Returns a defensive copy to prevent external mutation.
     */
    public String[][] getBoard() {
        String[][] copy = new String[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, size);
        }
        return copy;
    }

    public int getSize() {
        return size;
    }

    public int getWinLength() {
        return winLength;
    }

    public String getCell(int row, int column) {
        if (row < 0 || row >= size || column < 0 || column >= size) {
            throw new GameException("Invalid row or column");
        }
        return board[row][column];
    }

    public void setBoard(int row, int column, String symbol) {
        if (row < 0 || row >= size || column < 0 || column >= size) {
            throw new GameException("Invalid row or column");
        }
        board[row][column] = symbol;
    }

    public boolean isEmpty(int row, int column) {
        return EMPTY_CELL.equals(getCell(row, column));
    }
}

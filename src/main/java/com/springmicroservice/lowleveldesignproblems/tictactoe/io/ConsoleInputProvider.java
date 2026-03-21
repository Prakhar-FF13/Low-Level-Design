package com.springmicroservice.lowleveldesignproblems.tictactoe.io;

import java.util.Scanner;

public class ConsoleInputProvider implements InputProvider {
    private final Scanner scanner;

    public ConsoleInputProvider(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public int[] getNextMove(int boardSize) {
        System.out.print("Enter row (0-" + (boardSize - 1) + "): ");
        int row = scanner.nextInt();
        System.out.print("Enter column (0-" + (boardSize - 1) + "): ");
        int column = scanner.nextInt();
        return new int[]{row, column};
    }
}

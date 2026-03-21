package com.springmicroservice.lowleveldesignproblems.tictactoe.factory;

import java.util.ArrayList;
import java.util.List;

import com.springmicroservice.lowleveldesignproblems.tictactoe.strategies.ColumnWinningStrategy;
import com.springmicroservice.lowleveldesignproblems.tictactoe.strategies.MajorDiagonalWinningStrategy;
import com.springmicroservice.lowleveldesignproblems.tictactoe.strategies.MinorDiagonalWinningStrategy;
import com.springmicroservice.lowleveldesignproblems.tictactoe.strategies.RowWinningStrategy;
import com.springmicroservice.lowleveldesignproblems.tictactoe.strategies.WinningStrategy;

/**
 * Factory pattern: Centralizes creation of winning strategies.
 */
public class WinningStrategyFactory {

    public static List<WinningStrategy> createDefaultStrategies() {
        List<WinningStrategy> strategies = new ArrayList<>();
        strategies.add(new RowWinningStrategy());
        strategies.add(new ColumnWinningStrategy());
        strategies.add(new MajorDiagonalWinningStrategy());
        strategies.add(new MinorDiagonalWinningStrategy());
        return strategies;
    }
}

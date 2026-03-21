package com.springmicroservice.lowleveldesignproblems.tictactoe.factory;

import java.util.ArrayList;
import java.util.List;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Player;

/**
 * Factory pattern: Creates player configurations. Supports 2+ players.
 */
public class PlayerFactory {

    private static final String[] DEFAULT_TWO_SYMBOLS = {"X", "O"};

    /** Creates 2 players with X and O (classic Tic Tac Toe). */
    public static List<Player> createDefaultPlayers() {
        return createPlayers(2);
    }

    /** Creates N players with symbols P1, P2, P3, ... */
    public static List<Player> createPlayers(int count) {
        if (count < 2) {
            throw new IllegalArgumentException("At least 2 players required");
        }
        List<Player> players = new ArrayList<>();
        String[] symbols = count == 2 ? DEFAULT_TWO_SYMBOLS : generateSymbols(count);
        for (int i = 0; i < count; i++) {
            players.add(new Player("Player " + (i + 1), symbols[i]));
        }
        return players;
    }

    private static String[] generateSymbols(int count) {
        String[] symbols = new String[count];
        for (int i = 0; i < count; i++) {
            symbols[i] = "P" + (i + 1);
        }
        return symbols;
    }
}

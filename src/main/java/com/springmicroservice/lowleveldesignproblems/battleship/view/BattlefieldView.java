package com.springmicroservice.lowleveldesignproblems.battleship.view;

import com.springmicroservice.lowleveldesignproblems.battleship.grid.Bounds;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Battlefield;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Cell;
import com.springmicroservice.lowleveldesignproblems.battleship.models.CellStatus;

/**
 * Builds a string representation of the battlefield for display.
 * Supports different views: own territory (ships visible) vs opponent territory (fog of war).
 */
public class BattlefieldView {

    public enum ViewMode {
        OWN_TERRITORY,   // Viewer's own ships visible
        OPPONENT_TERRITORY  // Fog of war - only HIT/MISS visible
    }

    private static final char EMPTY = '.';
    private static final char SHIP = 'S';
    private static final char HIT = 'X';
    private static final char MISS = 'O';
    private static final char FOG = '~';

    public static String render(Battlefield battlefield, Bounds bounds, ViewMode mode) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int col = bounds.getMinCol(); col <= bounds.getMaxCol(); col++) {
            sb.append(col % 10);
        }
        sb.append("\n");

        for (int row = bounds.getMinRow(); row <= bounds.getMaxRow(); row++) {
            sb.append(row % 10).append(" ");
            for (int col = bounds.getMinCol(); col <= bounds.getMaxCol(); col++) {
                Cell cell = battlefield.getCell(row, col);
                sb.append(toChar(cell, mode));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static char toChar(Cell cell, ViewMode mode) {
        return switch (cell.getStatus()) {
            case HIT -> HIT;
            case MISS -> MISS;
            case OCCUPIED -> mode == ViewMode.OWN_TERRITORY ? SHIP : FOG;
            case EMPTY -> mode == ViewMode.OWN_TERRITORY ? EMPTY : FOG;
        };
    }

    public static String renderFull(Battlefield battlefield) {
        return render(battlefield,
                new Bounds(0, battlefield.getSize() - 1, 0, battlefield.getSize() - 1),
                ViewMode.OWN_TERRITORY);
    }
}

package com.springmicroservice.lowleveldesignproblems.battleship.targeting;

import com.springmicroservice.lowleveldesignproblems.battleship.models.Coordinate;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Player;

/**
 * Strategy for selecting the next target coordinate when firing.
 */
public interface TargetingStrategy {
    /**
     * Returns the next coordinate to fire at. The coordinate must be within target's territory
     * and not already in attacker's attackedCoordinates.
     */
    Coordinate getNextTarget(Player attacker, Player target);
}

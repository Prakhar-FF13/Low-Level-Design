package com.springmicroservice.lowleveldesignproblems.battleship.targeting;

import com.springmicroservice.lowleveldesignproblems.battleship.models.Coordinate;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Picks a random valid (unfired) coordinate from the target's territory.
 */
public class RandomTargetingStrategy implements TargetingStrategy {
    private final Random random = new Random();

    @Override
    public Coordinate getNextTarget(Player attacker, Player target) {
        List<Coordinate> available = new ArrayList<>();
        for (int x = target.getBounds().getMinRow(); x <= target.getBounds().getMaxRow(); x++) {
            for (int y = target.getBounds().getMinCol(); y <= target.getBounds().getMaxCol(); y++) {
                Coordinate coord = new Coordinate(x, y);
                if (!attacker.hasAttacked(coord)) {
                    available.add(coord);
                }
            }
        }
        if (available.isEmpty()) {
            throw new IllegalStateException("No valid target coordinates remaining");
        }
        return available.get(random.nextInt(available.size()));
    }
}

package com.springmicroservice.lowleveldesignproblems.battleship.models;

import com.springmicroservice.lowleveldesignproblems.battleship.grid.Bounds;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a player in the battleship game.
 */
@Getter
public class Player {
    private final String id;
    private final String name;
    private final Battlefield territory;
    private final Bounds bounds;
    private final List<Ship> ships;
    private final Set<Coordinate> attackedCoordinates = new HashSet<>();

    public Player(String id, String name, Battlefield territory, Bounds bounds, List<Ship> ships) {
        this.id = Objects.requireNonNull(id, "Player id cannot be null");
        this.name = name != null ? name : id;
        this.territory = Objects.requireNonNull(territory, "Territory cannot be null");
        this.bounds = Objects.requireNonNull(bounds, "Bounds cannot be null");
        this.ships = List.copyOf(Objects.requireNonNull(ships, "Ships cannot be null"));
    }

    public void recordAttack(Coordinate coordinate) {
        attackedCoordinates.add(coordinate);
    }

    public boolean hasAttacked(Coordinate coordinate) {
        return attackedCoordinates.contains(coordinate);
    }

    public boolean hasLost() {
        return ships.stream().allMatch(Ship::isDestroyed);
    }

    public int getShipsRemaining() {
        return (int) ships.stream().filter(s -> !s.isDestroyed()).count();
    }
}

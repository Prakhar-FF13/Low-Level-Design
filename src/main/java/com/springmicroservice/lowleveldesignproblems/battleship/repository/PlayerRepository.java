package com.springmicroservice.lowleveldesignproblems.battleship.repository;

import com.springmicroservice.lowleveldesignproblems.battleship.models.Player;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Player persistence.
 * Useful when players have profiles/stats across multiple games.
 * Abstraction allows swapping in-memory impl with DB later.
 */
public interface PlayerRepository {
    Player save(Player player);

    Optional<Player> findById(String id);

    List<Player> findAll();

    void deleteById(String id);
}

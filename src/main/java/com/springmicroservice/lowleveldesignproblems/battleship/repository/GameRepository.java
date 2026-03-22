package com.springmicroservice.lowleveldesignproblems.battleship.repository;

import com.springmicroservice.lowleveldesignproblems.battleship.game.Game;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Game persistence.
 * Abstraction allows swapping in-memory impl with DB later.
 */
public interface GameRepository {
    Game save(Game game);

    Optional<Game> findById(String id);

    List<Game> findAll();

    void deleteById(String id);
}

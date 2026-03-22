package com.springmicroservice.lowleveldesignproblems.battleship.repository;

import com.springmicroservice.lowleveldesignproblems.battleship.game.Game;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of GameRepository.
 * Uses ConcurrentHashMap for thread-safe storage.
 */
public class InMemoryGameRepository implements GameRepository {
    private final Map<String, Game> gamesById = new ConcurrentHashMap<>();

    @Override
    public Game save(Game game) {
        gamesById.put(game.getId(), game);
        return game;
    }

    @Override
    public Optional<Game> findById(String id) {
        return Optional.ofNullable(gamesById.get(id));
    }

    @Override
    public List<Game> findAll() {
        return List.copyOf(gamesById.values());
    }

    @Override
    public void deleteById(String id) {
        gamesById.remove(id);
    }
}

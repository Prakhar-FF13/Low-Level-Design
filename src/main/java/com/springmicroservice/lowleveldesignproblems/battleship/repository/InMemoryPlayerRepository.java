package com.springmicroservice.lowleveldesignproblems.battleship.repository;

import com.springmicroservice.lowleveldesignproblems.battleship.models.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of PlayerRepository.
 * Uses ConcurrentHashMap for thread-safe storage.
 */
public class InMemoryPlayerRepository implements PlayerRepository {
    private final Map<String, Player> playersById = new ConcurrentHashMap<>();

    @Override
    public Player save(Player player) {
        playersById.put(player.getId(), player);
        return player;
    }

    @Override
    public Optional<Player> findById(String id) {
        return Optional.ofNullable(playersById.get(id));
    }

    @Override
    public List<Player> findAll() {
        return List.copyOf(playersById.values());
    }

    @Override
    public void deleteById(String id) {
        playersById.remove(id);
    }
}

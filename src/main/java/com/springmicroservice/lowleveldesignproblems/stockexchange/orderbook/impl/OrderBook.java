package com.springmicroservice.lowleveldesignproblems.stockexchange.orderbook.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Order;
import com.springmicroservice.lowleveldesignproblems.stockexchange.orderbook.IOrderBook;

public class OrderBook implements IOrderBook {
    private final Map<String, List<Order>> orders = new ConcurrentHashMap<>(1000);
    private final Map<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>(1000);

    private ReentrantReadWriteLock getLock(String stockId) {
        return locks.computeIfAbsent(stockId, k -> new ReentrantReadWriteLock());
    }

    private static void applyOrderUpdate(Order target, Order source) {
        target.setFilledQuantity(source.getFilledQuantity());
        target.setReamingingQuantity(source.getReamingingQuantity());
        target.setPrice(source.getPrice());
        target.setStatus(source.getStatus());
    }

    @Override
    public void addOrder(Order order) {
        ReentrantReadWriteLock lock = getLock(order.getStockId());
        lock.writeLock().lock();
        try {
            orders.computeIfAbsent(order.getStockId(), k -> new ArrayList<>()).add(order);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeOrder(String orderId, String stockId) {
        ReentrantReadWriteLock lock = getLock(stockId);
        lock.writeLock().lock();
        try {
            List<Order> ordersList = orders.get(stockId);
            if (ordersList == null) {
                return false;
            }
            boolean removed = ordersList.removeIf(order -> order.getId().equals(orderId));
            if (removed && ordersList.isEmpty()) {
                orders.remove(stockId);
            }
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean updateOrder(Order order) {
        ReentrantReadWriteLock lock = getLock(order.getStockId());
        lock.writeLock().lock();
        try {
            List<Order> ordersList = orders.get(order.getStockId());
            if (ordersList == null) {
                return false;
            }
            Optional<Order> existing = ordersList.stream()
                    .filter(o -> o.getId().equals(order.getId()))
                    .findFirst();
            existing.ifPresent(e -> applyOrderUpdate(e, order));
            return existing.isPresent();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Snapshot of all resting orders. Locks per symbol in sorted key order to avoid deadlock with writers.
     */
    @Override
    public List<Order> getOrders() {
        List<String> stockIds = new ArrayList<>(orders.keySet());
        Collections.sort(stockIds);
        List<ReentrantReadWriteLock> acquired = new ArrayList<>();
        try {
            for (String stockId : stockIds) {
                ReentrantReadWriteLock lock = getLock(stockId);
                lock.readLock().lock();
                acquired.add(lock);
            }
            List<Order> result = new ArrayList<>();
            for (String stockId : stockIds) {
                List<Order> list = orders.get(stockId);
                if (list != null) {
                    result.addAll(new ArrayList<>(list));
                }
            }
            return result;
        } finally {
            for (int i = acquired.size() - 1; i >= 0; i--) {
                acquired.get(i).readLock().unlock();
            }
        }
    }

    @Override
    public List<Order> getOrders(String stockId) {
        ReentrantReadWriteLock lock = getLock(stockId);
        lock.readLock().lock();
        try {
            List<Order> list = orders.get(stockId);
            if (list == null || list.isEmpty()) {
                return Collections.emptyList();
            }
            return new ArrayList<>(list);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Order> findOrderById(String orderId) {
        List<String> stockIds = new ArrayList<>(orders.keySet());
        Collections.sort(stockIds);
        for (String stockId : stockIds) {
            ReentrantReadWriteLock lock = getLock(stockId);
            lock.readLock().lock();
            try {
                List<Order> list = orders.get(stockId);
                if (list != null) {
                    for (Order o : list) {
                        if (orderId.equals(o.getId())) {
                            return Optional.of(o);
                        }
                    }
                }
            } finally {
                lock.readLock().unlock();
            }
        }
        return Optional.empty();
    }
}

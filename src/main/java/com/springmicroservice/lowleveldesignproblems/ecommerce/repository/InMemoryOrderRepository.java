package com.springmicroservice.lowleveldesignproblems.ecommerce.repository;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Order;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> ordersById = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        ordersById.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(ordersById.get(orderId));
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return ordersById.values().stream()
                .filter(o -> userId.equals(o.getUserId()))
                .collect(Collectors.toList());
    }
}

package com.springmicroservice.lowleveldesignproblems.ecommerce.repository;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(String orderId);

    List<Order> findByUserId(String userId);
}

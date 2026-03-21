package com.springmicroservice.lowleveldesignproblems.ecommerce.repository;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Cart;

import java.util.Optional;

public interface CartRepository {
    Cart save(Cart cart);

    Optional<Cart> findById(String cartId);

    Optional<Cart> findByUserId(String userId);

    void delete(String cartId);
}

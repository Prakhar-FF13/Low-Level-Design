package com.springmicroservice.lowleveldesignproblems.ecommerce.repository;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Cart;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCartRepository implements CartRepository {
    private final Map<String, Cart> cartsById = new ConcurrentHashMap<>();
    private final Map<String, String> userIdToCartId = new ConcurrentHashMap<>();

    @Override
    public Cart save(Cart cart) {
        cartsById.put(cart.getCartId(), cart);
        userIdToCartId.put(cart.getUserId(), cart.getCartId());
        return cart;
    }

    @Override
    public Optional<Cart> findById(String cartId) {
        return Optional.ofNullable(cartsById.get(cartId));
    }

    @Override
    public Optional<Cart> findByUserId(String userId) {
        String cartId = userIdToCartId.get(userId);
        return cartId != null ? findById(cartId) : Optional.empty();
    }

    @Override
    public void delete(String cartId) {
        Cart cart = cartsById.remove(cartId);
        if (cart != null) {
            userIdToCartId.remove(cart.getUserId());
        }
    }
}

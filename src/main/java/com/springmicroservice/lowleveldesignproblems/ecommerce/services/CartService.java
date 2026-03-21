package com.springmicroservice.lowleveldesignproblems.ecommerce.services;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Cart;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.CartItem;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.CartRepository;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.ProductRepository;

import java.util.Optional;
import java.util.UUID;

public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart(UUID.randomUUID().toString(), userId);
                    return cartRepository.save(cart);
                });
    }

    public Optional<Cart> getCart(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public boolean addToCart(String userId, String productId, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return false;
        }

        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> productId.equals(i.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            cart.getItems().add(new CartItem(productId, quantity));
        }
        cartRepository.save(cart);
        return true;
    }

    public void clearCart(String userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }
}

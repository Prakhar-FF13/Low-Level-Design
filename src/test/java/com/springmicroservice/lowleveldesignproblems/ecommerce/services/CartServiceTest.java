package com.springmicroservice.lowleveldesignproblems.ecommerce.services;

import com.springmicroservice.lowleveldesignproblems.ecommerce.catalog.ProductCatalogFactory;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Cart;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.InMemoryCartRepository;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    private CartService cartService;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = ProductCatalogFactory.createSeededCatalog();
        cartService = new CartService(new InMemoryCartRepository(), productRepository);
    }

    @Test
    void addToCart_addsProduct() {
        boolean added = cartService.addToCart("user1", "prod-1", 2);
        assertTrue(added);

        Optional<Cart> cart = cartService.getCart("user1");
        assertTrue(cart.isPresent());
        assertEquals(1, cart.get().getItems().size());
        assertEquals("prod-1", cart.get().getItems().get(0).getProductId());
        assertEquals(2, cart.get().getItems().get(0).getQuantity());
    }

    @Test
    void addToCart_sameProductIncrementsQuantity() {
        cartService.addToCart("user1", "prod-1", 2);
        cartService.addToCart("user1", "prod-1", 3);

        Optional<Cart> cart = cartService.getCart("user1");
        assertTrue(cart.isPresent());
        assertEquals(1, cart.get().getItems().size());
        assertEquals(5, cart.get().getItems().get(0).getQuantity());
    }

    @Test
    void addToCart_invalidProductId_returnsFalse() {
        boolean added = cartService.addToCart("user1", "invalid-id", 1);
        assertFalse(added);
    }

    @Test
    void addToCart_zeroQuantity_returnsFalse() {
        boolean added = cartService.addToCart("user1", "prod-1", 0);
        assertFalse(added);
    }

    @Test
    void getOrCreateCart_createsNewCartForUser() {
        Cart cart = cartService.getOrCreateCart("user1");
        assertNotNull(cart.getCartId());
        assertEquals("user1", cart.getUserId());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void clearCart_removesAllItems() {
        cartService.addToCart("user1", "prod-1", 2);
        cartService.clearCart("user1");

        Optional<Cart> cart = cartService.getCart("user1");
        assertTrue(cart.isPresent());
        assertTrue(cart.get().getItems().isEmpty());
    }
}

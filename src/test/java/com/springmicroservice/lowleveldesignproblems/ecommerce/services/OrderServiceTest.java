package com.springmicroservice.lowleveldesignproblems.ecommerce.services;

import com.springmicroservice.lowleveldesignproblems.ecommerce.catalog.ProductCatalogFactory;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Order;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.OrderStatus;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.InMemoryCartRepository;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.InMemoryOrderRepository;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;
    private CartService cartService;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = ProductCatalogFactory.createSeededCatalog();
        cartService = new CartService(new InMemoryCartRepository(), productRepository);
        orderService = new OrderService(new InMemoryOrderRepository(), productRepository, cartService);
    }

    @Test
    void placeOrder_withItemsInCart_createsOrder() {
        cartService.addToCart("user1", "prod-1", 2);
        cartService.addToCart("user1", "prod-5", 1);

        Optional<Order> order = orderService.placeOrder("user1");
        assertTrue(order.isPresent());
        assertEquals(OrderStatus.PLACED, order.get().getStatus());
        assertEquals(2, order.get().getItems().size());
        assertEquals(999.99 * 2 + 49.99, order.get().getTotalAmount(), 0.01);
    }

    @Test
    void placeOrder_emptyCart_returnsEmpty() {
        Optional<Order> order = orderService.placeOrder("user1");
        assertTrue(order.isEmpty());
    }

    @Test
    void placeOrder_clearsCart() {
        cartService.addToCart("user1", "prod-1", 1);
        orderService.placeOrder("user1");

        assertTrue(cartService.getCart("user1").get().getItems().isEmpty());
    }

    @Test
    void cancelOrder_placedOrder_succeeds() {
        cartService.addToCart("user1", "prod-1", 1);
        Order order = orderService.placeOrder("user1").orElseThrow();
        String orderId = order.getOrderId();

        boolean cancelled = orderService.cancelOrder(orderId, "user1");
        assertTrue(cancelled);

        Optional<Order> updated = orderService.getOrder(orderId);
        assertTrue(updated.isPresent());
        assertEquals(OrderStatus.CANCELLED, updated.get().getStatus());
    }

    @Test
    void cancelOrder_wrongUser_returnsFalse() {
        cartService.addToCart("user1", "prod-1", 1);
        Order order = orderService.placeOrder("user1").orElseThrow();

        boolean cancelled = orderService.cancelOrder(order.getOrderId(), "user2");
        assertFalse(cancelled);
    }

    @Test
    void getOrdersByUser_returnsUserOrders() {
        cartService.addToCart("user1", "prod-1", 1);
        orderService.placeOrder("user1");
        cartService.addToCart("user1", "prod-2", 1);
        orderService.placeOrder("user1");

        List<Order> orders = orderService.getOrdersByUser("user1");
        assertEquals(2, orders.size());
    }

    @Test
    void updateOrderStatus_changesStatus() {
        cartService.addToCart("user1", "prod-1", 1);
        Order order = orderService.placeOrder("user1").orElseThrow();

        boolean updated = orderService.updateOrderStatus(order.getOrderId(), OrderStatus.SHIPPED);
        assertTrue(updated);
        assertEquals(OrderStatus.SHIPPED, orderService.getOrder(order.getOrderId()).orElseThrow().getStatus());
    }

    @Test
    void cancelOrder_shippedOrder_returnsFalse() {
        cartService.addToCart("user1", "prod-1", 1);
        Order order = orderService.placeOrder("user1").orElseThrow();
        orderService.updateOrderStatus(order.getOrderId(), OrderStatus.SHIPPED);

        boolean cancelled = orderService.cancelOrder(order.getOrderId(), "user1");
        assertFalse(cancelled);
    }
}

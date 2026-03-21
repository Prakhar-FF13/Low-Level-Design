package com.springmicroservice.lowleveldesignproblems.ecommerce.services;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Cart;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.CartItem;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Order;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.OrderItem;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.OrderStatus;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.OrderRepository;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    public Optional<Order> placeOrder(String userId) {
        Cart cart = cartService.getCart(userId).orElse(null);
        if (cart == null || cart.getItems().isEmpty()) {
            return Optional.empty();
        }

        Order order = new Order(UUID.randomUUID().toString(), userId);
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            if (product != null) {
                order.getItems().add(new OrderItem(
                        product.getProductId(),
                        product.getName(),
                        product.getPrice(),
                        cartItem.getQuantity()
                ));
            }
        }

        if (order.getItems().isEmpty()) {
            return Optional.empty();
        }

        orderRepository.save(order);
        cartService.clearCart(userId);
        return Optional.of(order);
    }

    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public boolean cancelOrder(String orderId, String userId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent() || !userId.equals(orderOpt.get().getUserId())) {
            return false;
        }
        Order order = orderOpt.get();
        if (!order.canBeCancelled()) {
            return false;
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return true;
    }

    public boolean updateOrderStatus(String orderId, OrderStatus newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }
        Order order = orderOpt.get();
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return false;
        }
        order.setStatus(newStatus);
        orderRepository.save(order);
        return true;
    }
}

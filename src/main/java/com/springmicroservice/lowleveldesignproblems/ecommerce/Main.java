package com.springmicroservice.lowleveldesignproblems.ecommerce;

import com.springmicroservice.lowleveldesignproblems.ecommerce.catalog.ProductCatalogFactory;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.*;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.InMemoryCartRepository;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.InMemoryOrderRepository;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.ProductRepository;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.CartService;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.OrderService;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.AndFilterCriteria;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.Criteria;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.PriceFilterCriteria;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.factories.PriceComparisonStrategyFactory;
import com.springmicroservice.lowleveldesignproblems.ecommerce.utils.Operator;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * CLI entry point for the e-commerce LLD.
 * Supports: product search, add to cart, place order, cancel order, order status.
 */
public class Main {
    private static final String DEFAULT_USER_ID = "user1";
    private static final Scanner SCANNER = new Scanner(System.in);

    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderService orderService;

    public Main(ProductRepository productRepository, CartService cartService, OrderService orderService) {
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    public static void main(String[] args) {
        ProductRepository productRepo = ProductCatalogFactory.createSeededCatalog();
        CartService cartService = new CartService(new InMemoryCartRepository(), productRepo);
        OrderService orderService = new OrderService(new InMemoryOrderRepository(), productRepo, cartService);

        Main main = new Main(productRepo, cartService, orderService);
        main.printWelcome();
        main.runCliLoop();
    }

    private void runCliLoop() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1" -> listAllProducts();
                case "2" -> searchByPriceRange();
                case "3" -> searchByCombinedFilters();
                case "4" -> addToCart();
                case "5" -> viewCart();
                case "6" -> placeOrder();
                case "7" -> viewOrders();
                case "8" -> cancelOrder();
                case "9" -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
            if (running) {
                System.out.println();
            }
        }
        System.out.println("Goodbye!");
    }

    private void printWelcome() {
        System.out.println("========================================");
        System.out.println("  E-Commerce LLD (Full Demo)             ");
        System.out.println("========================================");
        System.out.println("Logged in as: " + DEFAULT_USER_ID);
        System.out.println();
    }

    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("  1. List all products");
        System.out.println("  2. Search by price (min/max)");
        System.out.println("  3. Search with combined filters");
        System.out.println("  4. Add product to cart");
        System.out.println("  5. View cart");
        System.out.println("  6. Place order");
        System.out.println("  7. View my orders & status");
        System.out.println("  8. Cancel order");
        System.out.println("  9. Exit");
        System.out.print("Choice: ");
    }

    private void listAllProducts() {
        System.out.println("--- All Products ---");
        displayProducts(productRepository.findAll());
    }

    private void searchByPriceRange() {
        System.out.print("Enter minimum price (or press Enter to skip): ");
        String minInput = SCANNER.nextLine().trim();
        System.out.print("Enter maximum price (or press Enter to skip): ");
        String maxInput = SCANNER.nextLine().trim();

        Criteria criteria = buildPriceRangeCriteria(minInput, maxInput);
        if (criteria == null) {
            System.out.println("Invalid input. Please provide at least one valid number.");
            return;
        }

        List<Product> results = criteria.satisfy(productRepository.findAll());
        System.out.println("Found " + results.size() + " product(s):");
        displayProductsWithId(results);
    }

    private Criteria buildPriceRangeCriteria(String minInput, String maxInput) {
        Double min = parseDouble(minInput);
        Double max = parseDouble(maxInput);

        if (min == null && max == null) return null;
        if (min != null && max != null) {
            return new AndFilterCriteria(List.of(
                    new PriceFilterCriteria(min, PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.GreaterThanOrEquals)),
                    new PriceFilterCriteria(max, PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.LessThanOrEquals))
            ));
        }
        if (min != null) {
            return new PriceFilterCriteria(min, PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.GreaterThan));
        }
        return new PriceFilterCriteria(max, PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.LessThan));
    }

    private void searchByCombinedFilters() {
        System.out.print("Price greater than: ");
        String gtInput = SCANNER.nextLine().trim();
        System.out.print("Price less than: ");
        String ltInput = SCANNER.nextLine().trim();

        double minPrice = parseDoubleOrDefault(gtInput, 0);
        double maxPrice = parseDoubleOrDefault(ltInput, Double.MAX_VALUE);

        Criteria criteria = new AndFilterCriteria(List.of(
                new PriceFilterCriteria(minPrice, PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.GreaterThan)),
                new PriceFilterCriteria(maxPrice, PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.LessThan))
        ));

        List<Product> results = criteria.satisfy(productRepository.findAll());
        System.out.println("Found " + results.size() + " product(s) between $" + minPrice + " and $" + maxPrice + ":");
        displayProductsWithId(results);
    }

    private void addToCart() {
        System.out.println("--- Products (use ID to add) ---");
        displayProductsWithId(productRepository.findAll());
        System.out.print("Enter product ID: ");
        String productId = SCANNER.nextLine().trim();
        System.out.print("Enter quantity: ");
        String qtyStr = SCANNER.nextLine().trim();
        int quantity = parseIntOrDefault(qtyStr, 1);

        if (cartService.addToCart(DEFAULT_USER_ID, productId, quantity)) {
            System.out.println("Added to cart.");
        } else {
            System.out.println("Failed to add. Check product ID exists and quantity > 0.");
        }
    }

    private void viewCart() {
        Optional<Cart> cartOpt = cartService.getCart(DEFAULT_USER_ID);
        if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        Cart cart = cartOpt.get();
        System.out.println("--- Your Cart ---");
        double total = 0;
        for (CartItem item : cart.getItems()) {
            Optional<Product> product = productRepository.findById(item.getProductId());
            if (product.isPresent()) {
                double subtotal = product.get().getPrice() * item.getQuantity();
                total += subtotal;
                System.out.println("  " + product.get().getName() + " x " + item.getQuantity() + " = $" + String.format("%.2f", subtotal));
            }
        }
        System.out.println("Total: $" + String.format("%.2f", total));
    }

    private void placeOrder() {
        Optional<Order> orderOpt = orderService.placeOrder(DEFAULT_USER_ID);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            System.out.println("Order placed! Order ID: " + order.getOrderId());
            System.out.println("Total: $" + String.format("%.2f", order.getTotalAmount()));
            System.out.println("Status: " + order.getStatus());
        } else {
            System.out.println("Cannot place order. Cart is empty.");
        }
    }

    private void viewOrders() {
        List<Order> orders = orderService.getOrdersByUser(DEFAULT_USER_ID);
        if (orders.isEmpty()) {
            System.out.println("You have no orders.");
            return;
        }

        System.out.println("--- Your Orders ---");
        for (Order order : orders) {
            System.out.println("  Order " + order.getOrderId() + " | Status: " + order.getStatus() + " | Total: $" + String.format("%.2f", order.getTotalAmount()));
        }
    }

    private void cancelOrder() {
        viewOrders();
        System.out.print("Enter order ID to cancel: ");
        String orderId = SCANNER.nextLine().trim();

        if (orderService.cancelOrder(orderId, DEFAULT_USER_ID)) {
            System.out.println("Order cancelled.");
        } else {
            System.out.println("Cannot cancel. Order not found, not yours, or already shipped/delivered.");
        }
    }

    private Double parseDouble(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double parseDoubleOrDefault(String s, double defaultValue) {
        Double parsed = parseDouble(s);
        return parsed != null ? parsed : defaultValue;
    }

    private int parseIntOrDefault(String s, int defaultValue) {
        if (s == null || s.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void displayProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("(No products found)");
            return;
        }
        for (Product p : products) {
            String categoryName = p.getCategory() != null ? p.getCategory().getCategoryName() : "N/A";
            System.out.println("  - " + p.getName() + " | $" + p.getPrice() + " | " + categoryName);
        }
    }

    private void displayProductsWithId(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("(No products found)");
            return;
        }
        for (Product p : products) {
            String categoryName = p.getCategory() != null ? p.getCategory().getCategoryName() : "N/A";
            System.out.println("  [" + p.getProductId() + "] " + p.getName() + " | $" + p.getPrice() + " | " + categoryName);
        }
    }
}

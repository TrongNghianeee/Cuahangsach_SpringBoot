package com.example.bookstore;

import com.example.bookstore.model.Order;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.service.OrderService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Test_JUNIT_User_Order {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Integer userId = 4;

    @BeforeAll
    static void setupClass() {
        System.out.println("Starting OrderService JUnit Tests");
    }

       @BeforeEach
    void setup() {
        System.out.println("Setting up Order test");
    }

    @AfterEach
    void teardown() {
        System.out.println("Cleaning up after Order test");
    }

    @Test
    @DisplayName("Test createOrder logic - should create and save order correctly")
    void testCreateOrder() {
         // Fetch user entity instead of just ID
         User user = userRepository.findById(userId).orElseThrow();

         Order order = new Order();
         order.setUser(user);
         order.setStatus("Đang xử lý");
         order.setOrderDate(LocalDateTime.now());
         order.setTotalAmount(new BigDecimal("100.00"));
         order.setShippingAddress("123 Test Street, Test City");

        Order saved = orderService.save(order);

        System.out.println("Saved order ID: " + saved.getOrderId());

        assertNotNull(saved, "Saved order should not be null");
        assertNotNull(saved.getOrderId(), "Saved order must have ID");
        assertEquals("Đang xử lý", saved.getStatus(), "Status should match");
    }

    @Test
    @DisplayName("Test getOrdersByUserId returns correct list")
    void testGetOrdersByUserId() {
        List<Order> orders = orderService.getOrdersByUserId(userId);

        System.out.println("Orders for userId=" + userId + ": " + orders.size() + " found");
        assertNotNull(orders, "Orders list should not be null");
        orders.forEach(order ->
            assertEquals(userId, order.getUser().getUserId(), "Each order should belong to userId")
        );
    }

    @Test
    @DisplayName("Test getOrdersByUserIdAndStatus returns correct filtered list")
    void testGetOrdersByUserIdAndStatus() {
        String status = "PENDING";
        List<Order> orders = orderService.getOrdersByUserIdAndStatus(userId, status);

        System.out.println("Orders for userId=" + userId + " with status=" + status + ": " + orders.size());
        assertNotNull(orders, "Orders list should not be null");
        orders.forEach(order -> {
            assertEquals(userId, order.getUser().getUserId(), "Order userId should match");
            assertEquals(status, order.getStatus(), "Order status should match");
        });
    }

    @Test
    @DisplayName("Test getOrderById returns correct entity")
    void testGetOrderById() {
        // Assume there is at least one order
        List<Order> orders = orderRepository.findAll();
        assertFalse(orders.isEmpty(), "At least one order should exist for this test");

        Integer orderId = orders.get(0).getOrderId();
        Optional<Order> orderOpt = orderService.getOrderById(orderId);

        assertTrue(orderOpt.isPresent(), "Order should be found by ID");
        assertEquals(orderId, orderOpt.get().getOrderId(), "Returned order ID should match");

        System.out.println("Found order ID: " + orderOpt.get().getOrderId());
    }

    @Test
    @DisplayName("Test countOrdersByUserId returns correct count")
    void testCountOrdersByUserId() {
        Long count = orderService.countOrdersByUserId(userId);

        System.out.println("Order count for userId=" + userId + ": " + count);

        assertNotNull(count, "Count should not be null");
        assertTrue(count >= 0, "Count should be >= 0");
    }

}

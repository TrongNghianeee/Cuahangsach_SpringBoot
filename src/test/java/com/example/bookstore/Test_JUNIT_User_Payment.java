package com.example.bookstore;

import com.example.bookstore.model.Payment;
import com.example.bookstore.service.PaymentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.bookstore.model.Order;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Test_JUNIT_User_Payment {

    @Autowired
    private PaymentService paymentService;

    @BeforeAll
    static void setupClass() {
        System.out.println("Starting PaymentService JUnit Tests");
    }

    @BeforeEach
    void setUp() {
        System.out.println("Setting up test environment");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Cleaning up after test");
    }

    @Test
    @DisplayName("Verify sumPaymentAmountsByOrderId returns correct total")
    void testSumPaymentAmountsByOrderId() {
        Integer orderId = 1;

        BigDecimal total = paymentService.sumPaymentAmountsByOrderId(orderId);

        System.out.println("👉 Total payment amount for orderId=" + orderId + ": " + total);

        assertNotNull(total, "Total payment amount should not be null");
        assertTrue(total.compareTo(BigDecimal.ZERO) >= 0, "Total should be >= 0");
    }

    @Test
    @DisplayName("Verify getPaymentsByOrderId returns correct list")
    void testGetPaymentsByOrderId() {
        Integer orderId = 1;

        List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);

        System.out.println("Payments for orderId=" + orderId + ": " + payments.size() + " records found");

        assertNotNull(payments, "Payments list should not be null");
        payments.forEach(payment ->
                assertEquals(orderId, payment.getOrder().getOrderId(),
                        "Each payment must have matching orderId"));
    }

    @Test
    @DisplayName("Verify getPaymentsByUserId returns correct list")
    void testGetPaymentsByUserId() {
        Integer userId = 4;

        List<Payment> payments = paymentService.getPaymentsByUserId(userId);

        System.out.println("Payments for userId=" + userId + ": " + payments.size() + " records found");

        assertNotNull(payments, "Payments list should not be null");
        payments.forEach(payment ->
                assertNotNull(payment.getOrder().getOrderId(),
                        "Each payment should have an orderId"));
    }

    @Test
    @DisplayName("Verify save stores payment successfully")
    void testSavePayment() {
        Payment payment = new Payment();
        payment.setOrder(new Order());
        payment.getOrder().setOrderId(1);
        payment.setAmount(new BigDecimal("50.00"));
        payment.setPaymentMethod("COD");

        Payment savedPayment = paymentService.save(payment);

        System.out.println("Saved payment successfully with ID: " + savedPayment.getPaymentId());

        assertNotNull(savedPayment, "Saved payment should not be null");
        assertNotNull(savedPayment.getPaymentId(), "Saved payment must have an ID");
        assertEquals("COD", savedPayment.getPaymentMethod(), "Payment method should match");
    }
}

package com.example.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bookstore.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    // Find all payments for a specific order
    @Query("SELECT p FROM Payment p WHERE p.order.orderId = :orderId ORDER BY p.paymentDate DESC")
    List<Payment> findByOrderId(@Param("orderId") Integer orderId);
    
    // Find payments by payment method
    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :paymentMethod ORDER BY p.paymentDate DESC")
    List<Payment> findByPaymentMethod(@Param("paymentMethod") String paymentMethod);
    
    // Find payments by user (through order relationship)
    @Query("SELECT p FROM Payment p WHERE p.order.user.userId = :userId ORDER BY p.paymentDate DESC")
    List<Payment> findByUserId(@Param("userId") Integer userId);
    
    // Count payments by order
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.order.orderId = :orderId")
    Long countByOrderId(@Param("orderId") Integer orderId);
    
    // Sum payment amounts by order
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.order.orderId = :orderId")
    java.math.BigDecimal sumAmountByOrderId(@Param("orderId") Integer orderId);
}

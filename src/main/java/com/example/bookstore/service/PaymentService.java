package com.example.bookstore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.dto.PaymentDTO;
import com.example.bookstore.model.Payment;
import com.example.bookstore.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Get payment by ID
    public Optional<Payment> getPaymentById(Integer paymentId) {
        return paymentRepository.findById(paymentId);
    }

    // Get payments by order ID
    public List<Payment> getPaymentsByOrderId(Integer orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    // Get payments by payment method
    public List<Payment> getPaymentsByPaymentMethod(String paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod);
    }

    // Get payments by user ID (through order relationship)
    public List<Payment> getPaymentsByUserId(Integer userId) {
        return paymentRepository.findByUserId(userId);
    }

    // Count payments by order ID
    public Long countPaymentsByOrderId(Integer orderId) {
        return paymentRepository.countByOrderId(orderId);
    }

    // Sum payment amounts by order ID
    public BigDecimal sumPaymentAmountsByOrderId(Integer orderId) {
        return paymentRepository.sumAmountByOrderId(orderId);
    }

    // Create payment - placeholder for future implementation
    @Transactional
    public Payment createPayment(PaymentDTO paymentDTO) {
        // TODO: Implement payment creation logic
        throw new UnsupportedOperationException("Payment creation not implemented yet");
    }

    // Update payment - placeholder for future implementation
    @Transactional
    public Payment updatePayment(Integer paymentId, PaymentDTO paymentDTO) {
        // TODO: Implement payment update logic
        throw new UnsupportedOperationException("Payment update not implemented yet");
    }

    // Delete payment - placeholder for future implementation
    @Transactional
    public void deletePayment(Integer paymentId) {
        // TODO: Implement payment deletion logic
        throw new UnsupportedOperationException("Payment deletion not implemented yet");
    }
}

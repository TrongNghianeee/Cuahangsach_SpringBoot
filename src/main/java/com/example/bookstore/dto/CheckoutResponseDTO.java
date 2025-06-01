package com.example.bookstore.dto;

import lombok.Data;

@Data
public class CheckoutResponseDTO {
    
    private boolean success;
    private String message;
    private OrderDTO order;
    private PaymentDTO payment;
    
    // Constructor
    public CheckoutResponseDTO() {}
    
    public CheckoutResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public CheckoutResponseDTO(boolean success, String message, OrderDTO order, PaymentDTO payment) {
        this.success = success;
        this.message = message;
        this.order = order;
        this.payment = payment;
    }
}

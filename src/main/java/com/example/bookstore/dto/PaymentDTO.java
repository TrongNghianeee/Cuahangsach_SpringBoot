package com.example.bookstore.dto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.example.bookstore.model.Payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentDTO {
  // Fields for both request and response
  private Integer paymentId; // Null for request, filled for response

  @NotNull(message = "ID đơn hàng không được để trống")
  private Integer orderId;

  @NotNull(message = "Số tiền không được để trống")
  @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
  private BigDecimal amount;
  @NotBlank(message = "Phương thức thanh toán không được để trống")
  @Size(max = 50, message = "Phương thức thanh toán không quá 50 ký tự")
  private String paymentMethod; // COD, Credit Card, Bank Transfer, E-Wallet
  private String paymentDate; // For response only

  // Constructors
  public PaymentDTO() {
  } // Constructor for request (create payment)

  public PaymentDTO(Integer orderId, BigDecimal amount, String paymentMethod) {
    this.orderId = orderId;
    this.amount = amount;
    this.paymentMethod = paymentMethod;
  }

  // Constructor for response (from entity)
  public PaymentDTO(Payment payment) {
    this.paymentId = payment.getPaymentId();
    this.orderId = payment.getOrder().getOrderId();
    this.amount = payment.getAmount();
    this.paymentMethod = payment.getPaymentMethod();
    this.paymentDate = payment.getPaymentDate() != null
        ? payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        : null;
  }

  // Utility methods
  public boolean isRequest() {
    return this.paymentId == null;
  }

  public boolean isResponse() {
    return this.paymentId != null;
  }
}

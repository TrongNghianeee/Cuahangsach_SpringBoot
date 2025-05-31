package com.example.bookstore.dto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.example.bookstore.model.Order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderDTO {
    // Fields for both request and response
    private Integer orderId; // Null for request, filled for response
    
    @NotNull(message = "ID người dùng không được để trống")
    private Integer userId;
    
    private String username; // For response only
    
    @NotNull(message = "Tổng tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tổng tiền phải lớn hơn 0")
    private BigDecimal totalAmount;
    
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddress;
    
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Size(max = 50, message = "Phương thức thanh toán không quá 50 ký tự")
    private String paymentMethod;
    
    private String status; // For response only (Pending, Processing, Shipped, Delivered, Cancelled)
    
    private String orderDate; // For response only
    
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<OrderDetailDTO> orderDetails;
    
    // Constructors
    public OrderDTO() {}
    
    // Constructor for request (create order)
    public OrderDTO(Integer userId, BigDecimal totalAmount, String shippingAddress, 
                   String paymentMethod, List<OrderDetailDTO> orderDetails) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.orderDetails = orderDetails;
    }
    
    // Constructor for response (from entity)
    public OrderDTO(Order order) {
        this.orderId = order.getOrderId();
        this.userId = order.getUser().getUserId();
        this.username = order.getUser().getUsername();
        this.totalAmount = order.getTotalAmount();
        this.shippingAddress = order.getShippingAddress();
        this.status = order.getStatus();
        this.orderDate = order.getOrderDate() != null 
            ? order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) 
            : null;
        
        // Convert order details if available
        if (order.getOrderDetails() != null) {
            this.orderDetails = order.getOrderDetails().stream()
                .map(OrderDetailDTO::new)
                .collect(Collectors.toList());
        }
    }
    
    // Utility method to check if this is a request (no orderId) or response (has orderId)
    public boolean isRequest() {
        return this.orderId == null;
    }
    
    public boolean isResponse() {
        return this.orderId != null;
    }
}

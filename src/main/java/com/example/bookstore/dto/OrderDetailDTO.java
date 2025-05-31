package com.example.bookstore.dto;

import java.math.BigDecimal;

import com.example.bookstore.model.OrderDetail;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailDTO {
    // Fields for both request and response
    private Integer orderDetailId; // Null for request, filled for response
    private Integer orderId; // For response only
    
    @NotNull(message = "ID sách không được để trống")
    private Integer bookId;
    
    private String bookTitle; // For response only
    private String bookAuthor; // For response only
    private String bookImageUrl; // For response only
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
    
    @NotNull(message = "Giá tại thời điểm đặt hàng không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal priceAtOrder;
    
    private BigDecimal subtotal; // For response only (quantity * priceAtOrder)
    
    // Constructors
    public OrderDetailDTO() {}
    
    // Constructor for request (create order detail)
    public OrderDetailDTO(Integer bookId, Integer quantity, BigDecimal priceAtOrder) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }
      // Constructor for response (from entity)
    public OrderDetailDTO(OrderDetail orderDetail) {
        this.orderDetailId = orderDetail.getOrderDetailId();
        this.orderId = orderDetail.getOrder().getOrderId();
        this.bookId = orderDetail.getBook().getBookId();
        this.bookTitle = orderDetail.getBook().getTitle();
        this.bookAuthor = orderDetail.getBook().getAuthor();
        // Note: Book entity doesn't have imageUrl field directly
        // We'll need to get primary image from BookImage relationship
        this.bookImageUrl = null; // Will be set separately if needed
        this.quantity = orderDetail.getQuantity();
        this.priceAtOrder = orderDetail.getPriceAtOrder();
        this.subtotal = orderDetail.getPriceAtOrder().multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
    }
    
    // Utility methods
    public boolean isRequest() {
        return this.orderDetailId == null;
    }
    
    public boolean isResponse() {
        return this.orderDetailId != null;
    }
    
    // Calculate subtotal if not set
    public BigDecimal getSubtotal() {
        if (subtotal == null && priceAtOrder != null && quantity != null) {
            return priceAtOrder.multiply(BigDecimal.valueOf(quantity));
        }
        return subtotal;
    }
}

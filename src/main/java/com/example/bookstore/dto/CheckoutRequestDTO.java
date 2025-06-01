package com.example.bookstore.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckoutRequestDTO {
    
    @NotNull(message = "ID người dùng không được để trống")
    private Integer userId;
    
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Size(max = 255, message = "Địa chỉ giao hàng không quá 255 ký tự")
    private String shippingAddress;
    
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Size(max = 50, message = "Phương thức thanh toán không quá 50 ký tự")
    private String paymentMethod;
    
    @NotNull(message = "Tổng tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tổng tiền phải lớn hơn 0")
    private BigDecimal totalAmount;
    
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<CheckoutItemDTO> items;
    
    // Constructor
    public CheckoutRequestDTO() {}
    
    public CheckoutRequestDTO(Integer userId, String shippingAddress, String paymentMethod, 
                             BigDecimal totalAmount, List<CheckoutItemDTO> items) {
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.items = items;
    }
}

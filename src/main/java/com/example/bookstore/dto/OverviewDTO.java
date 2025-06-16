package com.example.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverviewDTO {
    private Long totalUsers;
    private Long totalProducts;
    private Long totalOrders;
}

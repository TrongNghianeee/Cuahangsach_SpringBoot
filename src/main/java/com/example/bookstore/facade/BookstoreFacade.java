package com.example.bookstore.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bookstore.dto.OverviewDTO;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import com.example.bookstore.service.OrderService;
import com.example.bookstore.service.UserService;

@Component
public class BookstoreFacade {

    @Autowired
    private UserService userService;    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    public OverviewDTO getOverviewDTO() {
        Long totalUsers = userService.getTotalUserCount();
        Long totalProducts = bookService.getTotalBookCount();
        Long totalOrders = orderService.getTotalOrderCount();
        
        return new OverviewDTO(totalUsers, totalProducts, totalOrders);
    }
}
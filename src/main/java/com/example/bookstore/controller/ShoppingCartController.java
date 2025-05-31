package com.example.bookstore.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.ShoppingCartDTO;
import com.example.bookstore.facade.UserFacade;

@RestController
@RequestMapping("/api/user/shoppingcart")
public class ShoppingCartController {

    @Autowired
    private UserFacade userFacade;

    /**
     * GET /api/user/shoppingcart/{userId} - Lấy danh sách sách trong giỏ hàng
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getShoppingCart(@PathVariable Integer userId) {
        return userFacade.getUserShoppingCartResponse(userId);
    }

    /**
     * POST /api/user/shoppingcart/{bookId} - Thêm sách vào giỏ hàng
     * Body: {"userId": 1}
     */
    @PostMapping("/{bookId}")
    public ResponseEntity<Map<String, Object>> addToCart(
            @PathVariable Integer bookId,
            @RequestBody ShoppingCartDTO requestDto) {
        return userFacade.addBookToCartResponse(requestDto.getUserId(), bookId);
    }

    /**
     * DELETE /api/user/shoppingcart/{bookId} - Xóa sách khỏi giỏ hàng
     * Body: {"userId": 1}
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @PathVariable Integer bookId,
            @RequestBody ShoppingCartDTO requestDto) {
        return userFacade.removeBookFromCartResponse(requestDto.getUserId(), bookId);
    }
}

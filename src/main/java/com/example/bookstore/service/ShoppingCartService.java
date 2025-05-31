package com.example.bookstore.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.dto.ShoppingCartDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.ShoppingCartKey;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import com.example.bookstore.repository.UserRepository;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    // Get all cart items for a user
    public List<ShoppingCartDTO> getCartByUserId(Integer userId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserId(userId);
        return cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Add book to cart
    @Transactional
    public ShoppingCartDTO addToCart(Integer userId, Integer bookId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Check if book exists
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

        // Check if book is in stock
        if (book.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Book is out of stock");
        }

        // Check if item already exists in cart
        ShoppingCartKey cartKey = new ShoppingCartKey(userId, bookId);
        Optional<ShoppingCart> existingItem = shoppingCartRepository.findById(cartKey);
        
        if (existingItem.isPresent()) {
            throw new IllegalArgumentException("Book is already in the cart");
        }

        // Create new cart item
        ShoppingCart cartItem = new ShoppingCart();
        cartItem.setId(cartKey);
        cartItem.setUser(user);
        cartItem.setBook(book);

        ShoppingCart savedItem = shoppingCartRepository.save(cartItem);
        return convertToDTO(savedItem);
    }

    // Remove book from cart
    @Transactional
    public void removeFromCart(Integer userId, Integer bookId) {
        ShoppingCartKey cartKey = new ShoppingCartKey(userId, bookId);
        
        if (!shoppingCartRepository.existsById(cartKey)) {
            throw new IllegalArgumentException("Cart item not found");
        }

        shoppingCartRepository.deleteById(cartKey);
    }

    // Clear all cart items for a user
    @Transactional
    public void clearCart(Integer userId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserId(userId);
        shoppingCartRepository.deleteAll(cartItems);
    }

    // Get cart item count for a user
    public Long getCartItemCount(Integer userId) {
        return shoppingCartRepository.countByUserId(userId);
    }

    // Check if book is in user's cart
    public boolean isBookInCart(Integer userId, Integer bookId) {
        return shoppingCartRepository.findByUserIdAndBookId(userId, bookId).isPresent();
    }

    // Convert ShoppingCart entity to DTO
    private ShoppingCartDTO convertToDTO(ShoppingCart cartItem) {
        Book book = cartItem.getBook();
        return new ShoppingCartDTO(
                cartItem.getId().getUserId(),
                cartItem.getId().getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getDescription(),
                book.getPrice(),
                book.getStockQuantity()
        );
    }
}

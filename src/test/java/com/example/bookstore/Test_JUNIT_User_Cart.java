package com.example.bookstore;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import com.example.bookstore.service.ShoppingCartService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Test_JUNIT_User_Cart {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    private Integer userId = 4;
    private Integer bookId = 16;

    @BeforeAll
    static void setupClass() {
        System.out.println("Starting ShoppingCartService JUnit Tests");
    }

    @BeforeEach
    void setUp() {
        System.out.println("Setting up test environment");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Cleaning up after test");
    }

    @Test
    @DisplayName("Test addToCart with out-of-stock book should fail")
    void testAddToCartOutOfStock() {
        // Make sure book exists and has zero stock
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setStockQuantity(0);
        bookRepository.save(book);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> shoppingCartService.addToCart(userId, bookId),
                "Should throw for out-of-stock book");

        System.out.println("Exception message: " + exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("out of stock"));
    }

    @Test
    @DisplayName("Test addToCart with book already in cart should fail")
    void testAddToCartAlreadyInCart() {
        // Make sure book has stock
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setStockQuantity(5);
        bookRepository.save(book);

        // Clear only this user's cart and ensure item is in cart
        shoppingCartRepository.deleteAllByUserId(userId);
        shoppingCartService.addToCart(userId, bookId);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> shoppingCartService.addToCart(userId, bookId),
                "Should throw when adding duplicate item");

        System.out.println("Exception message: " + exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("already in the cart"));
    }

    @Test
    @DisplayName("Test removeFromCart with non-existing item should fail")
    void testRemoveFromCartItemNotFound() {
        Integer nonExistingBookId = 9999;

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> shoppingCartService.removeFromCart(userId, nonExistingBookId),
                "Should throw for non-existing cart item");

        System.out.println("Exception message: " + exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    @DisplayName("Test getCartItemCount returns correct number")
    void testGetCartItemCount() {
        // Clear only this user's cart & add 2 books
        shoppingCartRepository.deleteAllByUserId(userId);

        Book book1 = bookRepository.findById(1).orElseThrow();
        Book book2 = bookRepository.findById(2).orElseThrow();

        shoppingCartService.addToCart(userId, 1);
        shoppingCartService.addToCart(userId, 2);

        Long count = shoppingCartService.getCartItemCount(userId);
        System.out.println("Cart item count: " + count);
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Test isBookInCart returns true/false correctly")
    void testIsBookInCart() {
        shoppingCartRepository.deleteAllByUserId(userId);

        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setStockQuantity(5);
        bookRepository.save(book);

        // Not in cart yet
        assertFalse(shoppingCartService.isBookInCart(userId, bookId), "Should be false when not in cart");

        // Add to cart
        shoppingCartService.addToCart(userId, bookId);

        // Now should be true
        assertTrue(shoppingCartService.isBookInCart(userId, bookId), "Should be true when in cart");
    }
}

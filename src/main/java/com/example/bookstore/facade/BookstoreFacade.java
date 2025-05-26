package com.example.bookstore.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bookstore.dto.ProductDTO;
import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.model.User;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import com.example.bookstore.service.UserService;

@Component
public class BookstoreFacade {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    public User registerUser(UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    public User updateUser(Integer userId, UserDTO userDTO) {
        return userService.updateUser(userId, userDTO);
    }

    public void toggleUserStatus(Integer userId, String status) {
        userService.toggleUserStatus(userId, status);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public User getUserById(Integer userId) {
        return userService.getUserById(userId);
    }

    // ===== PRODUCT MANAGEMENT METHODS =====
    
    // Category operations
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    public Category createCategory(String categoryName) {
        return categoryService.createCategory(categoryName);
    }

    public Category getCategoryById(Integer categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    // Book operations
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    public Book createBook(ProductDTO productDTO) {
        return bookService.createBook(productDTO);
    }

    public Book updateBook(Integer bookId, ProductDTO productDTO) {
        return bookService.updateBook(bookId, productDTO);
    }

    public Book getBookById(Integer bookId) {
        return bookService.getBookById(bookId);
    }

    public void deleteBook(Integer bookId) {
        bookService.deleteBook(bookId);
    }

    public ProductDTO convertToProductDTO(Book book) {
        return bookService.convertToProductDTO(book);
    }
}
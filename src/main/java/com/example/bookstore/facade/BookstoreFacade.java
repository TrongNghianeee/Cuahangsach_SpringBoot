package com.example.bookstore.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bookstore.dto.InventoryDTO;
import com.example.bookstore.dto.ProductDTO;
import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.dto.UserResponseDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.model.InventoryTransaction;
import com.example.bookstore.model.User;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import com.example.bookstore.service.InventoryService;
import com.example.bookstore.service.UserService;

@Component
public class BookstoreFacade {

    @Autowired
    private UserService userService;    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private InventoryService inventoryService;    
    // ===== USER MANAGEMENT METHODS =====
    
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

    public List<UserResponseDTO> getAllUsersDTO() {
        return userService.getAllUsersAsDTO();
    }

    public User getUserById(Integer userId) {
        return userService.getUserById(userId);
    }

    // ===== PRODUCT MANAGEMENT METHODS =====
    
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
    
    // ===== INVENTORY MANAGEMENT METHODS =====

    public InventoryTransaction processInventoryTransaction(InventoryDTO inventoryDTO, Integer userId) {
        return inventoryService.processInventoryTransaction(inventoryDTO, userId);
    }

    public List<InventoryDTO> getAllInventoryTransactions() {
        return inventoryService.getAllTransactions();
    }

    public List<InventoryDTO> getInventoryTransactionsByBookId(Integer bookId) {
        return inventoryService.getTransactionsByBookId(bookId);
    }

    public Integer getCurrentStock(Integer bookId) {
        return inventoryService.getCurrentStock(bookId);
    }

    public InventoryDTO createInventoryDTOForBook(Integer bookId) {
        return inventoryService.createInventoryDTOForBook(bookId);
    }
}
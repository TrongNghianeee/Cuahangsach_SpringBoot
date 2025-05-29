package com.example.bookstore.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bookstore.model.Category;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import com.example.bookstore.service.UserService;

@Component
public class BookstoreFacade {

    @Autowired
    private UserService userService;    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    // @Autowired
    // private InventoryService inventoryService;    

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

    public Category updateCategory(Integer categoryId, String categoryName) {
        return categoryService.updateCategory(categoryId, categoryName);
    }

    public void deleteCategory(Integer categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    // Book operations
    // public List<Book> getAllBooks() {
    //     return bookService.getAllBooks();
    // }

    // public List<ProductDTO> getAllBookstoDTO() {
    //     return bookService.getAllBookstoDTO();
    // }

    // public Book createBook(ProductDTO productDTO) {
    //     return bookService.createBook(productDTO);
    // }

    // public Book updateBook(Integer bookId, ProductDTO productDTO) {
    //     return bookService.updateBook(bookId, productDTO);
    // }

    // public Book getBookById(Integer bookId) {
    //     return bookService.getBookById(bookId);
    // }

    // public void deleteBook(Integer bookId) {
    //     bookService.deleteBook(bookId);
    // }

    // public ProductDTO convertToProductDTO(Book book) {
    //     return bookService.convertToProductDTO(book);
    // }    
    
    // ===== INVENTORY MANAGEMENT METHODS =====

    // public InventoryTransaction processInventoryTransaction(InventoryDTO inventoryDTO, Integer userId) {
    //     return inventoryService.processInventoryTransaction(inventoryDTO, userId);
    // }

    // public List<InventoryDTO> getAllInventoryTransactions() {
    //     return inventoryService.getAllTransactions();
    // }

    // public List<InventoryDTO> getInventoryTransactionsByBookId(Integer bookId) {
    //     return inventoryService.getTransactionsByBookId(bookId);
    // }

    // public Integer getCurrentStock(Integer bookId) {
    //     return inventoryService.getCurrentStock(bookId);
    // }

    // public InventoryDTO createInventoryDTOForBook(Integer bookId) {
    //     return inventoryService.createInventoryDTOForBook(bookId);
    // }
}
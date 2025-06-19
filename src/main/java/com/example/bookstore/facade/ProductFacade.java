package com.example.bookstore.facade;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bookstore.dto.BookImageDTO;
import com.example.bookstore.dto.CategoryDTO;
import com.example.bookstore.dto.InventoryDTO;
import com.example.bookstore.dto.ProductDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.model.InventoryTransaction;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import com.example.bookstore.service.InventoryService;

@Component
public class ProductFacade {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private InventoryService inventoryService;

    // Existing methods for Product
    public List<ProductDTO> getAllProductsDTO() {
        List<Book> books = bookService.getAllBooks();
        return books.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductByIdDTO(Integer id) {
        Book book = bookService.getBookById(id);
        return mapToProductDTO(book);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        Book book = bookService.createBook(productDTO);
        return mapToProductDTO(book);
    }

    public ProductDTO updateProduct(Integer id, ProductDTO productDTO) {
        Book book = bookService.updateBook(id, productDTO);
        return mapToProductDTO(book);
    }

    public void deleteProduct(Integer id) {
        bookService.deleteBook(id);
    }

    // Existing methods for Category
    public List<CategoryDTO> getAllCategoriesDTO() {
        List<Category> categories = categoryService.getAllCategories();
        return categories.stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryByIdDTO(Integer id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new IllegalArgumentException("Danh mục không tồn tại");
        }
        return new CategoryDTO(category);
    }

    public CategoryDTO createCategory(String categoryName) {
        Category category = categoryService.createCategory(categoryName);
        return new CategoryDTO(category);
    }

    public CategoryDTO updateCategory(Integer id, String categoryName) {
        Category category = categoryService.updateCategory(id, categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Danh mục không tồn tại");
        }
        return new CategoryDTO(category);
    }

    public void deleteCategory(Integer id) {
        if (categoryService.getCategoryById(id) == null) {
            throw new IllegalArgumentException("Danh mục không tồn tại");
        }
        categoryService.deleteCategory(id);
    }

    // Updated methods for InventoryTransaction
    public List<InventoryDTO> createInventoryTransactions(List<InventoryDTO> inventoryDTOs) {
        List<InventoryTransaction> transactions = inventoryService.createInventoryTransactions(inventoryDTOs);
        return transactions.stream()
                .map(this::mapToInventoryDTO)
                .collect(Collectors.toList());
    }

    public InventoryDTO getInventoryTransactionByIdDTO(Integer id) {
        InventoryTransaction transaction = inventoryService.getInventoryTransactionById(id);
        if (transaction == null) {
            throw new IllegalArgumentException("Giao dịch kho không tồn tại");
        }
        return mapToInventoryDTO(transaction);
    }

    public List<InventoryDTO> getAllInventoryTransactionsDTO() {
        List<InventoryTransaction> transactions = inventoryService.getAllInventoryTransactions();
        return transactions.stream()
                .map(this::mapToInventoryDTO)
                .collect(Collectors.toList());
    }

    public InventoryDTO updateInventoryTransaction(Integer id, InventoryDTO inventoryDTO) {
        InventoryTransaction transaction = inventoryService.updateInventoryTransaction(id, inventoryDTO);
        if (transaction == null) {
            throw new IllegalArgumentException("Giao dịch kho không tồn tại");
        }
        return mapToInventoryDTO(transaction);
    }

    public void deleteInventoryTransaction(Integer id) {
        if (inventoryService.getInventoryTransactionById(id) == null) {
            throw new IllegalArgumentException("Giao dịch kho không tồn tại");
        }
        inventoryService.deleteInventoryTransaction(id);
    }

    private ProductDTO mapToProductDTO(Book book) {
        ProductDTO dto = new ProductDTO();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        dto.setStockQuantity(book.getStockQuantity());
        dto.setImages(book.getImages() != null ? book.getImages().stream()
                .map(BookImageDTO::new)
                .collect(Collectors.toList()) : null);
        dto.setCategories(book.getCategories() != null ? book.getCategories().stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList()) : null);
        dto.setCategoryIds(book.getCategories() != null ? book.getCategories().stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList()) : null);
        return dto;
    }    private InventoryDTO mapToInventoryDTO(InventoryTransaction transaction) {
        return new InventoryDTO(transaction);
    }
}
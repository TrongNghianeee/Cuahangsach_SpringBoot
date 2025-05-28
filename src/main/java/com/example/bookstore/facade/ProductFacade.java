package com.example.bookstore.facade;

import com.example.bookstore.dto.CategoryDTO;
import com.example.bookstore.dto.ProductDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.bookstore.dto.BookImageDTO;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductFacade {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

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
    }
}
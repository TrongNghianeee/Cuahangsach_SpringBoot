package com.example.bookstore.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.dto.BookImageDTO;
import com.example.bookstore.dto.CategoryDTO;
import com.example.bookstore.dto.ProductDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.BookImage;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.BookRepository;


@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookImageService bookImageService;    public List<Book> getAllBooks() {
        // Lấy sách với categories trước
        List<Book> books = bookRepository.findAllWithCategories();
        
        if (!books.isEmpty()) {
            // Lấy bookIds
            List<Integer> bookIds = books.stream()
                .map(Book::getBookId)
                .collect(Collectors.toList());
            
            // Lấy images cho các sách
            List<Book> booksWithImages = bookRepository.findBooksWithImages(bookIds);
            
            // Map images vào books
            Map<Integer, List<BookImage>> imageMap = booksWithImages.stream()
                .filter(b -> b.getImages() != null)
                .collect(Collectors.toMap(
                    Book::getBookId,
                    Book::getImages,
                    (existing, replacement) -> existing
                ));
            
            // Set images cho từng book
            books.forEach(book -> {
                if (imageMap.containsKey(book.getBookId())) {
                    book.setImages(imageMap.get(book.getBookId()));
                }
            });
        }
        
        return books;
    }

    public Book getBookById(Integer bookId) {
        Book book = bookRepository.findByIdWithCategories(bookId);
        if (book != null) {
            // Load images riêng
            List<Book> booksWithImages = bookRepository.findBooksWithImages(Arrays.asList(bookId));
            if (!booksWithImages.isEmpty()) {
                book.setImages(booksWithImages.get(0).getImages());
            }
        }
        return book;
    }

    @Transactional
    public Book createBook(ProductDTO productDTO) {
        Book book = new Book();
        book.setTitle(productDTO.getTitle());
        book.setAuthor(productDTO.getAuthor());
        book.setPublisher(productDTO.getPublisher());
        book.setPublicationYear(productDTO.getPublicationYear());
        book.setDescription(productDTO.getDescription());
        book.setPrice(productDTO.getPrice());
        book.setStockQuantity(0); // Mặc định

        // Thêm categories từ CategoryDTO
        if (productDTO.getCategories() != null && !productDTO.getCategories().isEmpty()) {
            List<Category> categories = productDTO.getCategories().stream()
                .map(dto -> categoryService.getCategoryById(dto.getCategoryId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            book.setCategories(categories);
        }

        // Lưu book trước
        book = bookRepository.save(book);

        // Thêm ảnh chính nếu có
        // if (productDTO.getPrimaryImageUrl() != null && !productDTO.getPrimaryImageUrl().trim().isEmpty()) {
        //     bookImageService.createBookImage(book, productDTO.getPrimaryImageUrl(), "Ảnh chính", true);
        // }

        return book;
    }

    @Transactional
    public Book updateBook(Integer bookId, ProductDTO productDTO) {
        Book book = getBookById(bookId);
        if (book != null) {
            book.setTitle(productDTO.getTitle());
            book.setAuthor(productDTO.getAuthor());
            book.setPublisher(productDTO.getPublisher());
            book.setPublicationYear(productDTO.getPublicationYear());
            book.setDescription(productDTO.getDescription());
            book.setPrice(productDTO.getPrice());

            // Cập nhật categories từ CategoryDTO
            if (productDTO.getCategories() != null) {
                List<Category> categories = productDTO.getCategories().stream()
                    .map(dto -> categoryService.getCategoryById(dto.getCategoryId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                book.setCategories(categories);
            }

            return bookRepository.save(book);
        }
        return null;
    }

    @Transactional
    public void deleteBook(Integer bookId) {
        // Xóa ảnh trước
        bookImageService.deleteImagesByBookId(bookId);
        bookRepository.deleteById(bookId);
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Chuyển đổi Book sang ProductDTO
    public ProductDTO convertToProductDTO(Book book) {
        ProductDTO dto = new ProductDTO();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        
        // Convert Category -> CategoryDTO
        if (book.getCategories() != null) {
            List<CategoryDTO> categoryDTOs = book.getCategories().stream()
                .map(category -> new CategoryDTO(
                    category.getCategoryId(),
                    category.getCategoryName()
                ))
                .collect(Collectors.toList());
            dto.setCategories(categoryDTOs);
        }

        // Convert BookImage -> BookImageDTO
        if (book.getImages() != null) {
            List<BookImageDTO> imageDTOs = book.getImages().stream()
                .map(image -> new BookImageDTO(
                    image.getImageId(),
                    image.getImageUrl(),
                    image.getDescription(),
                    image.getIsPrimary(),
                    image.getUploadedAt()
                ))
                .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }
        
        return dto;
    }
}

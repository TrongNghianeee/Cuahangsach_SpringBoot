package com.example.bookstore.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private CategoryService categoryService;    @Autowired
    private BookImageService bookImageService;

    @Autowired
    private InventoryService inventoryService;

    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAllWithCategories();
        if (!books.isEmpty()) {
            List<Integer> bookIds = books.stream()
                .map(Book::getBookId)
                .collect(Collectors.toList());
            List<Book> booksWithImages = bookRepository.findBooksWithImages(bookIds);
            Map<Integer, List<BookImage>> imageMap = booksWithImages.stream()
                .filter(b -> b.getImages() != null)
                .collect(Collectors.toMap(
                    Book::getBookId,
                    Book::getImages,
                    (existing, replacement) -> existing
                ));
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
        if (book == null) {
            throw new IllegalArgumentException("Sách không tồn tại");
        }
        List<Book> booksWithImages = bookRepository.findBooksWithImages(Arrays.asList(bookId));
        if (!booksWithImages.isEmpty()) {
            book.setImages(booksWithImages.get(0).getImages());
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
        book.setStockQuantity(productDTO.getStockQuantity() != null ? productDTO.getStockQuantity() : 0);

        // Xử lý categories từ categoryIds
        if (productDTO.getCategoryIds() != null && !productDTO.getCategoryIds().isEmpty()) {
            List<Category> categories = productDTO.getCategoryIds().stream()
                .map(categoryId -> {
                    Category category = categoryService.getCategoryById(categoryId);
                    if (category == null) {
                        throw new IllegalArgumentException("Danh mục với ID " + categoryId + " không tồn tại");
                    }
                    return category;
                })
                .collect(Collectors.toList());
            book.setCategories(categories);
        }

        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Integer bookId, ProductDTO productDTO) {
        Book book = getBookById(bookId);
        book.setTitle(productDTO.getTitle());
        book.setAuthor(productDTO.getAuthor());
        book.setPublisher(productDTO.getPublisher());
        book.setPublicationYear(productDTO.getPublicationYear());
        book.setDescription(productDTO.getDescription());
        book.setPrice(productDTO.getPrice());
        book.setStockQuantity(productDTO.getStockQuantity() != null ? productDTO.getStockQuantity() : book.getStockQuantity());

        // Xử lý categories từ categoryIds
        if (productDTO.getCategoryIds() != null) {
            List<Category> categories = productDTO.getCategoryIds().stream()
                .map(categoryId -> {
                    Category category = categoryService.getCategoryById(categoryId);
                    if (category == null) {
                        throw new IllegalArgumentException("Danh mục với ID " + categoryId + " không tồn tại");
                    }
                    return category;
                })
                .collect(Collectors.toList());
            book.setCategories(categories);
        }

        return bookRepository.save(book);
    }    @Transactional
    public void deleteBook(Integer bookId) {
        // Kiểm tra sách có tồn tại không
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại với ID: " + bookId));
        
        // Validate điều kiện xóa sách
        validateBookDeletion(book);
        
        // Nếu tất cả điều kiện đều thỏa mãn, tiến hành xóa
        // Xóa ảnh liên quan trước
        bookImageService.deleteImagesByBookId(bookId);
        
        // Xóa sách
        bookRepository.deleteById(bookId);
        
        System.out.println("✅ Đã xóa thành công sách: " + book.getTitle() + " (ID: " + bookId + ")");
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }
    
    /**
     * Update book stock quantity
     */
    @Transactional
    public void updateBookStock(Integer bookId, Integer newStockQuantity) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại với ID: " + bookId));
        
        if (newStockQuantity < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không thể âm");
        }
        
        book.setStockQuantity(newStockQuantity);
        bookRepository.save(book);
    }    public Long getTotalBookCount() {
        return bookRepository.count();
    }
    
    /**
     * Validate if a book can be deleted
     * @param book The book to validate
     * @throws IllegalArgumentException if book cannot be deleted
     */
    private void validateBookDeletion(Book book) {
        // Kiểm tra điều kiện 1: Số lượng tồn kho phải bằng 0
        if (book.getStockQuantity() != null && book.getStockQuantity() > 0) {
            throw new IllegalArgumentException(
                String.format("Không thể xóa sách '%s' vì vẫn còn %d cuốn trong kho. " +
                             "Vui lòng xuất hết sách khỏi kho trước khi xóa.", 
                             book.getTitle(), book.getStockQuantity())
            );
        }
        
        // Kiểm tra điều kiện 2: Sách không có trong inventory_transactions
        boolean hasInventoryTransactions = inventoryService.hasInventoryTransactionsByBookId(book.getBookId());
        if (hasInventoryTransactions) {
            throw new IllegalArgumentException(
                String.format("Không thể xóa sách '%s' vì đã có lịch sử nhập/xuất kho. " +
                             "Sách này đã từng được giao dịch trong hệ thống.", 
                             book.getTitle())
            );
        }
    }
}
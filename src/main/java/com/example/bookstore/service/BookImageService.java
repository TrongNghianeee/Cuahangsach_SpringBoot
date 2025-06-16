package com.example.bookstore.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.bookstore.dto.BookImageDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.BookImage;
import com.example.bookstore.repository.BookImageRepository;
import com.example.bookstore.repository.BookRepository;

@Service
public class BookImageService {

    @Autowired
    private BookImageRepository bookImageRepository;

    @Autowired
    private BookRepository bookRepository;

    private static final String UPLOAD_DIR = "uploads";    public List<BookImage> getImagesByBookId(Integer bookId) {
        return bookImageRepository.findByBookBookIdOrderByIsPrimaryDescUploadedAtAsc(bookId);
    }

    // Method trả về DTO để tránh vòng lặp dữ liệu
    public List<BookImageDTO> getImagesDTOByBookId(Integer bookId) {
        List<BookImage> images = bookImageRepository.findByBookBookIdOrderByIsPrimaryDescUploadedAtAsc(bookId);
        return images.stream()
                .map(BookImageDTO::new)
                .collect(Collectors.toList());
    }

    public BookImage getPrimaryImageByBookId(Integer bookId) {
        return bookImageRepository.findByBookBookIdAndIsPrimaryTrue(bookId);
    }

    public BookImage createBookImage(Book book, String imageUrl, String description, Boolean isPrimary) {
        // Nếu là ảnh chính, cần remove ảnh chính cũ
        if (isPrimary != null && isPrimary) {
            BookImage existingPrimary = getPrimaryImageByBookId(book.getBookId());
            if (existingPrimary != null) {
                existingPrimary.setIsPrimary(false);
                bookImageRepository.save(existingPrimary);
            }
        }

        BookImage bookImage = new BookImage();
        bookImage.setBook(book);
        bookImage.setImageUrl(imageUrl);
        bookImage.setDescription(description);
        bookImage.setIsPrimary(isPrimary != null ? isPrimary : false);
        
        return bookImageRepository.save(bookImage);
    }

    public BookImage updateBookImage(Integer imageId, String imageUrl, String description, Boolean isPrimary) {
        BookImage bookImage = bookImageRepository.findById(imageId).orElse(null);
        if (bookImage != null) {
            // Nếu set làm ảnh chính, cần remove ảnh chính cũ của cùng sách
            if (isPrimary != null && isPrimary && !bookImage.getIsPrimary()) {
                BookImage existingPrimary = getPrimaryImageByBookId(bookImage.getBook().getBookId());
                if (existingPrimary != null) {
                    existingPrimary.setIsPrimary(false);
                    bookImageRepository.save(existingPrimary);
                }
            }

            bookImage.setImageUrl(imageUrl);
            bookImage.setDescription(description);
            bookImage.setIsPrimary(isPrimary != null ? isPrimary : bookImage.getIsPrimary());
            return bookImageRepository.save(bookImage);
        }
        return null;
    }

    public void deleteBookImage(Integer imageId) {
        bookImageRepository.deleteById(imageId);
    }

    @Transactional
    public void deleteImagesByBookId(Integer bookId) {
        bookImageRepository.deleteByBookBookId(bookId);
    }

    // New methods for file upload functionality
    public BookImage uploadBookImage(Integer bookId, MultipartFile file, String description, Boolean isPrimary) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Find the book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // If this should be primary, set all other images of this book to non-primary
        if (isPrimary != null && isPrimary) {
            BookImage existingPrimary = getPrimaryImageByBookId(bookId);
            if (existingPrimary != null) {
                existingPrimary.setIsPrimary(false);
                bookImageRepository.save(existingPrimary);
            }
        }

        // Create BookImage entity
        BookImage bookImage = new BookImage();
        bookImage.setBook(book);
        bookImage.setImageUrl(filename);
        bookImage.setDescription(description);
        bookImage.setIsPrimary(isPrimary != null ? isPrimary : false);
        bookImage.setUploadedAt(LocalDateTime.now());

        return bookImageRepository.save(bookImage);
    }

    public void setPrimaryImage(Integer imageId) {
        BookImage image = bookImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + imageId));

        // Set all other images of this book to non-primary
        BookImage existingPrimary = getPrimaryImageByBookId(image.getBook().getBookId());
        if (existingPrimary != null && !existingPrimary.getImageId().equals(imageId)) {
            existingPrimary.setIsPrimary(false);
            bookImageRepository.save(existingPrimary);
        }

        // Set this image as primary
        image.setIsPrimary(true);
        bookImageRepository.save(image);
    }

    public void deleteImage(Integer imageId) throws IOException {
        BookImage image = bookImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + imageId));

        // Delete file from filesystem
        Path filePath = Paths.get(UPLOAD_DIR, image.getImageUrl());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Delete from database
        bookImageRepository.delete(image);
    }
}

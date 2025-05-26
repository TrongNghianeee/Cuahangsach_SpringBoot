package com.example.bookstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.BookImage;
import com.example.bookstore.repository.BookImageRepository;

@Service
public class BookImageService {

    @Autowired
    private BookImageRepository bookImageRepository;

    public List<BookImage> getImagesByBookId(Integer bookId) {
        return bookImageRepository.findByBookBookId(bookId);
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
}

package com.example.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.ShoppingCartKey;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, ShoppingCartKey> {

    // Find all cart items for a specific user
    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.id.userId = :userId")
    List<ShoppingCart> findByUserId(@Param("userId") Integer userId);

    // Find a specific cart item by user ID and book ID
    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.id.userId = :userId AND sc.id.bookId = :bookId")
    Optional<ShoppingCart> findByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") Integer bookId); // Delete
                                                                                                                    // cart
                                                                                                                    // item
                                                                                                                    // by
                                                                                                                    // user
                                                                                                                    // ID
                                                                                                                    // and
                                                                                                                    // book
                                                                                                                    // ID

    @Modifying
    @Transactional
    @Query("DELETE FROM ShoppingCart sc WHERE sc.id.userId = :userId AND sc.id.bookId = :bookId")
    void deleteByUserIdAndBookId(@Param("userId") Integer userId, @Param("bookId") Integer bookId);

    // Delete all cart items for a specific user
    @Modifying
    @Transactional
    @Query("DELETE FROM ShoppingCart sc WHERE sc.id.userId = :userId")
    void deleteAllByUserId(@Param("userId") Integer userId);

    // Count cart items for a user
    @Query("SELECT COUNT(sc) FROM ShoppingCart sc WHERE sc.id.userId = :userId")
    Long countByUserId(@Param("userId") Integer userId);
}

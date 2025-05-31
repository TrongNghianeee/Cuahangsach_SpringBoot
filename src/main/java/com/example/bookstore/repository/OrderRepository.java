package com.example.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bookstore.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    // Find all orders for a specific user
    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId ORDER BY o.orderDate DESC")
    List<Order> findByUserId(@Param("userId") Integer userId);
    
    // Find orders by status
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.orderDate DESC")
    List<Order> findByStatus(@Param("status") String status);
    
    // Find orders by user ID and status
    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId AND o.status = :status ORDER BY o.orderDate DESC")
    List<Order> findByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status);
    
    // Count orders by user
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.userId = :userId")
    Long countByUserId(@Param("userId") Integer userId);
    
    // Count orders by status
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") String status);
}

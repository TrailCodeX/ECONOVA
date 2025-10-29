package com.econova.repository;

import com.econova.entity.Order;
import com.econova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
    List<Order> findByUserOrderByIdDesc(User user);

    List<Order> findByStatus(String status);
}
package com.econova.repository;

import com.econova.entity.Cart;
import com.econova.entity.Product;
import com.econova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Find all cart items by a specific user
    List<Cart> findByUser(User user);

    // ✅ This is the correct way to find by User and Product
    Optional<Cart> findByUserAndProduct(User user, Product product);
}

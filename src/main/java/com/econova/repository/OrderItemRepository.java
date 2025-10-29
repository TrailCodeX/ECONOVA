package com.econova.repository;

import com.econova.entity.OrderItem;
import com.econova.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // ✅ Add this method
    List<OrderItem> findByOrder(Order order);
}

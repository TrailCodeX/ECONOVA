package com.econova.service;

import com.econova.entity.Order;
import com.econova.entity.Address;
import com.econova.entity.User;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Order order);
    Order createOrderFromCart(User user, Address address, double total);
    Map<String, Object> createRazorpayOrder(int amount) throws Exception;
    void updateOrderPayment(String razorpayOrderId, String razorpayPaymentId, String status);
    String getRazorpayKey();
    Order getOrderById(Long orderId);
    List<Order> getUserOrders(User user);
    List<Order> getOrdersByStatus(String status);
    List<Order> getAllOrders();
    
    Order createOrderWithItems(Order order, User user);
    void updateDeliveryStatus(Long orderId, String deliveryStatus);
}
package com.econova.service;

import com.econova.config.RazorpayConfig;
import com.econova.entity.Order;
import com.econova.entity.OrderItem;
import com.econova.entity.Address;
import com.econova.entity.Cart;
import com.econova.entity.User;
import com.econova.repository.CartRepository;
import com.econova.repository.OrderItemRepository;
import com.econova.repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RazorpayConfig razorpayConfig;

    @Override
    public Order createOrder(Order order) {
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
        }
        if (order.getDeliveryStatus() == null || order.getDeliveryStatus().isEmpty()) {
            order.setDeliveryStatus("Pending");
        }
        return orderRepository.save(order);
    }

    // ✅ NEW METHOD: Create order with items for COD
    @Override
    @Transactional
    public Order createOrderWithItems(Order order, User user) {
        // Save the order first
        Order savedOrder = createOrder(order);
        
        // If status is CONFIRMED (COD orders), create order items
        if ("CONFIRMED".equals(savedOrder.getStatus())) {
            createOrderItemsFromCart(savedOrder, user);
        }
          
        return savedOrder;
    }
    

    // ✅ HELPER METHOD: Create order items from cart
    @Transactional
    private void createOrderItemsFromCart(Order order, User user) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        
        System.out.println("Creating order items for Order ID: " + order.getId() + 
                         ", Cart items count: " + cartItems.size());
        
        for (Cart cart : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cart.getProduct());
            orderItem.setProductName(cart.getProduct().getName());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(cart.getProduct().getPrice());
            orderItem.setSubtotal(cart.getQuantity() * cart.getProduct().getPrice());
            
            orderItemRepository.save(orderItem);
            System.out.println("Created order item: Product=" + cart.getProduct().getName() + 
                             ", Qty=" + cart.getQuantity());
        }
    }

    @Override
    @Transactional
    public Order createOrderFromCart(User user, Address address, double total) {
        // Step 1: Create main order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setAmount(total);
        order.setCurrency("INR");
        order.setStatus("PENDING");
        order.setDeliveryStatus("Pending");
        order.setPaymentMethod("RAZORPAY"); // Set payment method
        order = orderRepository.save(order);

        // Step 2: Fetch all items from user's cart
        List<Cart> cartItems = cartRepository.findByUser(user);

        // Step 3: Convert each cart item to order item
        for (Cart cart : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cart.getProduct());
            orderItem.setProductName(cart.getProduct().getName());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(cart.getProduct().getPrice());
            orderItem.setSubtotal(cart.getQuantity() * cart.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        System.out.println("Order created from cart - Order ID: " + order.getId() + 
                         ", Items: " + cartItems.size());

        return order;
    }

    @Override
    public Map<String, Object> createRazorpayOrder(int amount) throws Exception {
        try {
            RazorpayClient razorpay = new RazorpayClient(
                razorpayConfig.getRazorpayKeyId(),
                razorpayConfig.getRazorpayKeySecret()
            );

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt#" + System.currentTimeMillis());

            com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("id", razorpayOrder.get("id"));
            response.put("amount", razorpayOrder.get("amount"));
            response.put("currency", razorpayOrder.get("currency"));

            return response;

        } catch (RazorpayException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return errorResponse;
        }
    }

    @Override
    @Transactional
    public void updateOrderPayment(String razorpayOrderId, String razorpayPaymentId, String paymentStatus) {
        try {
            Optional<Order> optionalOrder = orderRepository.findByRazorpayOrderId(razorpayOrderId);
            
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                order.setRazorpayOrderId(razorpayOrderId);
                order.setRazorpayPaymentId(razorpayPaymentId);
                order.setPaymentMethod("RAZORPAY");
                
                if ("PAID".equalsIgnoreCase(paymentStatus)) {
                    order.setStatus("CONFIRMED");
                    System.out.println("Order " + razorpayOrderId + " status: CONFIRMED");
                } else {
                    order.setStatus("FAILED");
                    System.out.println("Order " + razorpayOrderId + " status: FAILED");
                }
                
                orderRepository.save(order);
            } else {
                System.err.println("Order not found: " + razorpayOrderId);
            }
        } catch (Exception e) {
            System.err.println("Error updating order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getRazorpayKey() {
        return razorpayConfig.getRazorpayKeyId();
    }

    @Override
    public Order getOrderById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.orElse(null);
    }

    @Override
    public List<Order> getUserOrders(User user) {
        List<Order> orders = orderRepository.findByUserOrderByIdDesc(user);

        // Load order items
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrder(order);
            order.setOrderItems(items);
        }

        return orders;
    }




    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
    
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @Override
    @Transactional
    public void updateDeliveryStatus(Long orderId, String deliveryStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setDeliveryStatus(deliveryStatus);
            orderRepository.save(order);
            System.out.println("Order " + orderId + " delivery status updated to: " + deliveryStatus);
        } else {
            System.err.println("Order not found: " + orderId);
            throw new RuntimeException("Order not found with id: " + orderId);
        }
    }
}
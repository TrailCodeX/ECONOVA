package com.econova.controller;

import com.econova.entity.Order;
import com.econova.entity.OrderItem;
import com.econova.entity.User;
import com.econova.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/my-orders")
    public String viewMyOrders(HttpSession session, Model model) {
        // ✅ Check session attribute name - might be "currentUser" or "loggedInUser"
        User currentUser = (User) session.getAttribute("currentUser");
        
        // If not found, try alternate attribute name
        if (currentUser == null) {
            currentUser = (User) session.getAttribute("loggedInUser");
        }
        
        if (currentUser == null) {
            System.out.println("❌ No user in session - redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("✅ Current User ID: " + currentUser.getId());
        System.out.println("✅ Current User Email: " + currentUser.getEmail());
        
        List<Order> orders = orderService.getUserOrders(currentUser);
        System.out.println("📦 Orders found: " + orders.size());
        
        if (!orders.isEmpty()) {
            for (Order order : orders) {
                System.out.println("Order ID: " + order.getId() + 
                                 ", Amount: " + order.getAmount() + 
                                 ", Status: " + order.getStatus() +
                                 ", Created: " + order.getCreatedAt() +
                                 ", Items: " + (order.getOrderItems() != null ? order.getOrderItems().size() : 0));
                
                // ✅ Debug order items
                if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                    for (OrderItem item : order.getOrderItems()) {
                        String productInfo = "null";
                        String imageInfo = "null";
                        
                        if (item.getProduct() != null) {
                            productInfo = item.getProduct().getName();
                            imageInfo = item.getProduct().getImageUrl();
                        }
                        
                        System.out.println("  - Product: " + item.getProductName() + 
                                         " (Object: " + productInfo + ")" +
                                         ", Qty: " + item.getQuantity() +
                                         ", Price: " + item.getPrice() +
                                         ", Image: " + imageInfo);
                    }
                } else {
                    System.out.println("  ⚠️ No order items found for order #" + order.getId());
                }
            }
        }
        
        model.addAttribute("orders", orders);
        return "customer_orders";
    }
}
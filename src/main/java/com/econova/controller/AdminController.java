package com.econova.controller;

import com.econova.entity.Order;
import com.econova.entity.User;
import com.econova.repository.CategoryRepository;
import com.econova.repository.ProductRepository;
import com.econova.repository.UserRepository;
import com.econova.service.CategoryService;
import com.econova.service.OrderService;
import com.econova.service.ProductService;
import com.econova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * 🏠 Admin Dashboard Home Page with Statistics
     */
    @GetMapping("/home")
    public String adminHome(Model model) {
        long totalProducts = productRepository.count();
        long totalCategories = categoryRepository.count();
        long totalUsers = userRepository.count();
        long totalCustomers = userRepository.countByRole(User.Role.CUSTOMER);
        long totalAdmins = userRepository.countByRole(User.Role.ADMIN);
        long totalOrders = orderService.getAllOrders().size();

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("totalOrders", totalOrders);

        return "admin_home";
    }

    /**
     * 🛍️ View all orders
     */
    @GetMapping("/all")
    public String viewAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin_orders";
    }

    /**
     * ✅ Mark order as delivered
     */
    @PostMapping("/order/deliver/{id}")
    public String markAsDelivered(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.updateDeliveryStatus(id, "Delivered");
            redirectAttributes.addFlashAttribute("successMessage", 
                "Order #" + id + " marked as delivered successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to update delivery status: " + e.getMessage());
        }
        return "redirect:/admin/all";
    }
    
    
}
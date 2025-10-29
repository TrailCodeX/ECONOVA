package com.econova.controller;

import com.econova.entity.User;
import com.econova.repository.CategoryRepository;
import com.econova.repository.ProductRepository;
import com.econova.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Test endpoint to check if repositories work
     * Access: http://localhost:8080/debug/counts
     */
    @GetMapping("/counts")
    public Map<String, Object> getCounts() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long products = productRepository.count();
            result.put("products", products);
            result.put("productsSuccess", true);
        } catch (Exception e) {
            result.put("products", 0);
            result.put("productsError", e.getMessage());
        }
        
        try {
            long categories = categoryRepository.count();
            result.put("categories", categories);
            result.put("categoriesSuccess", true);
        } catch (Exception e) {
            result.put("categories", 0);
            result.put("categoriesError", e.getMessage());
        }
        
        try {
            long allUsers = userRepository.count();
            result.put("allUsers", allUsers);
            result.put("allUsersSuccess", true);
        } catch (Exception e) {
            result.put("allUsers", 0);
            result.put("allUsersError", e.getMessage());
        }
        
        try {
            long customers = userRepository.countByRole(User.Role.CUSTOMER);
            result.put("customers", customers);
            result.put("customersSuccess", true);
        } catch (Exception e) {
            result.put("customers", 0);
            result.put("customersError", e.getMessage());
        }
        
        try {
            long admins = userRepository.countByRole(User.Role.ADMIN);
            result.put("admins", admins);
            result.put("adminsSuccess", true);
        } catch (Exception e) {
            result.put("admins", 0);
            result.put("adminsError", e.getMessage());
        }
        
        return result;
    }
}
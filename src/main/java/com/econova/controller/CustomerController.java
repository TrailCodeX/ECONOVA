package com.econova.controller;

import com.econova.entity.Category;
import com.econova.entity.Product;
import com.econova.entity.User;
import com.econova.entity.Order;
import com.econova.repository.CategoryRepository;
import com.econova.service.OrderService;
import com.econova.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderService orderService;
    
    


    @GetMapping("/customerhome")
    public String customerHome(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        List<Category> categories = categoryRepository.findAll();
        List<Product> products = productService.getAllProducts();

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "customer_home";
    }

   
    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) return "redirect:/customerhome";

        model.addAttribute("product", product);
        return "product_detail"; // optional page
    }
    
    @GetMapping("/about")
    public String aboutPage(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        
        return "about";
    }
    
    @GetMapping("/contact")
    public String contactPage(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        
        return "contact";
    }
    
   

}

package com.econova.controller;

import com.econova.entity.Category;
import com.econova.entity.SubCategory;
import com.econova.entity.Product;
import com.econova.repository.CategoryRepository;
import com.econova.repository.SubCategoryRepository;
import com.econova.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @GetMapping("/adminhome")
    public String adminHome() {
        return "admin_home"; 
    }

    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Category> categories = categoryRepository.findAll();
        List<SubCategory> subcategories = subCategoryRepository.findAll();
        List<Product> products = productService.getAllProducts();

        model.addAttribute("categories", categories);
        model.addAttribute("subcategories", subcategories);
        model.addAttribute("products", products);

        return "home";
    }
    
    @GetMapping("/subcategories/{categoryId}")
    public String viewSubcategories(@PathVariable("categoryId") Long categoryId, Model model) {
        // Fetch the category (optional, if you want to display category info)
        Category category = categoryRepository.findById(categoryId).orElse(null);

        // Fetch subcategories for this category
        List<SubCategory> subcategories = subCategoryRepository.findByCategoryId(categoryId);

        model.addAttribute("category", category);
        model.addAttribute("subcategories", subcategories);

        return "subcategories"; // Thymeleaf page: subcategories.html
    }

}

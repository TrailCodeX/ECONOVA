package com.econova.controller;

import com.econova.entity.Product;
import com.econova.repository.SubCategoryRepository;
import com.econova.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    // ✅ Show Add Product Form
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("subcategories", subCategoryRepository.findAll());
        return "product_add";
    }

    // ✅ Handle Product Submission
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("image") MultipartFile image,
                             Model model) {
        productService.saveProduct(product, image);
        model.addAttribute("success", "Product added successfully!");
        model.addAttribute("product", new Product());
        model.addAttribute("subcategories", subCategoryRepository.findAll());
        return "product_add";
    }

    // ✅ Manage Products Page with Subcategory Filter
    @GetMapping("/manage")
    public String manageProducts(@RequestParam(value = "subcategory", required = false, defaultValue = "0") Long subcategoryId,
                                 Model model) {

        List<Product> products;
        if (subcategoryId != null && subcategoryId != 0) {
            products = productService.getProductsBySubcategory(subcategoryId);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("subcategories", subCategoryRepository.findAll());
        model.addAttribute("selectedSubcategory", subcategoryId != null ? subcategoryId : 0);

        return "product_manage";
    }

    // ✅ Edit Product Page
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("subcategories", subCategoryRepository.findAll());
        return "product_edit";
    }

    // ✅ Update Product
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam(value = "image", required = false) MultipartFile image) {
        productService.updateProduct(product, image);
        return "redirect:/admin/product/manage";
    }

    // ✅ Delete Product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/product/manage";
    }
}

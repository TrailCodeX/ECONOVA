package com.econova.service;

import com.econova.entity.Product;
import com.econova.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/products/";
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void saveProduct(Product product, MultipartFile image) {
        try {
            if (image != null && !image.isEmpty()) {
                // Create unique filename
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                
                // Create directories if they don't exist
                Files.createDirectories(path.getParent());
                
                // Save the file
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                
                // Store the URL in database
                product.setImageUrl("/uploads/products/" + fileName);
            }
            productRepository.save(product);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image file", e);
        }
    }
    
    @Override
    public long countProducts() {
        return productRepository.count();
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
    
    @Override
    public void deleteProductById(Long id) {
        // Optional: Delete the image file when deleting product
        Product product = productRepository.findById(id).orElse(null);
        if (product != null && product.getImageUrl() != null) {
            try {
                String fileName = product.getImageUrl().substring(product.getImageUrl().lastIndexOf("/") + 1);
                Path imagePath = Paths.get(UPLOAD_DIR + fileName);
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                // Log error but continue with deletion
                System.err.println("Failed to delete image file: " + e.getMessage());
            }
        }
        productRepository.deleteById(id);
    }
    
    @Override
    public void updateProduct(Product product, MultipartFile image) {
        try {
            Product existing = productRepository.findById(product.getId()).orElse(null);
            if (existing != null) {
                existing.setName(product.getName());
                existing.setDescription(product.getDescription());
                existing.setPrice(product.getPrice());
                existing.setQuantity(product.getQuantity());
                existing.setSubcategory(product.getSubcategory());
                
                // Update image if new one is provided
                if (image != null && !image.isEmpty()) {
                    // Delete old image if exists
                    if (existing.getImageUrl() != null) {
                        try {
                            String oldFileName = existing.getImageUrl().substring(existing.getImageUrl().lastIndexOf("/") + 1);
                            Path oldImagePath = Paths.get(UPLOAD_DIR + oldFileName);
                            Files.deleteIfExists(oldImagePath);
                        } catch (IOException e) {
                            System.err.println("Failed to delete old image: " + e.getMessage());
                        }
                    }
                    
                    // Save new image
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path path = Paths.get(UPLOAD_DIR + fileName);
                    Files.createDirectories(path.getParent());
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    existing.setImageUrl("/uploads/products/" + fileName);
                }
                
                productRepository.save(existing);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update product image", e);
        }
    }
    
    @Override
    public List<Product> getProductsBySubcategory(Long subcategoryId) {
        return productRepository.findBySubcategoryId(subcategoryId);
    }
}
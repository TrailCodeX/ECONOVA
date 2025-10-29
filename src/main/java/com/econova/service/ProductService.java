package com.econova.service;

import com.econova.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
	
	long countProducts();

    void saveProduct(Product product, MultipartFile image);

    List<Product> getAllProducts();

    Product getProductById(Long id);

    void deleteProductById(Long id);
    
    List<Product> getProductsBySubcategory(Long subcategoryId);

    void updateProduct(Product product, MultipartFile image);
}

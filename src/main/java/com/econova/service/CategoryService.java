package com.econova.service;

import com.econova.entity.Category;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CategoryService {
	
	long countCategories();

    void saveCategory(Category category);

    List<Category> getAllCategories();

    void deleteCategory(Long id);

    Category getCategoryById(Long id);

    void updateCategory(Category category, MultipartFile imageFile);
}

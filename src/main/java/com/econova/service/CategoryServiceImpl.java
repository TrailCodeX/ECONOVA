package com.econova.service;

import com.econova.entity.Category;
import com.econova.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/categories/";

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    @Override
    public long countCategories() {
        return categoryRepository.count();
    }

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        return optionalCategory.orElse(null);
    }

    @Override
    public void updateCategory(Category category, MultipartFile imageFile) {
        try {
            // Fetch the existing category
            Category existingCategory = categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());

            // If a new image is uploaded, replace it with a unique filename
            if (imageFile != null && !imageFile.isEmpty()) {
                String originalFileName = imageFile.getOriginalFilename();
                
                // Extract file extension
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                
                // Create unique filename with timestamp
                String uniqueFileName = "category_" + category.getId() + "_" + System.currentTimeMillis() + fileExtension;
                
                Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // Delete old image file if it exists
                if (existingCategory.getImageUrl() != null && !existingCategory.getImageUrl().isEmpty()) {
                    try {
                        String oldFileName = existingCategory.getImageUrl().substring(existingCategory.getImageUrl().lastIndexOf("/") + 1);
                        Path oldFilePath = Paths.get(UPLOAD_DIR + oldFileName);
                        Files.deleteIfExists(oldFilePath);
                    } catch (Exception e) {
                        // Log but don't fail if old file can't be deleted
                        System.err.println("Could not delete old image: " + e.getMessage());
                    }
                }
                
                existingCategory.setImageUrl("/uploads/categories/" + uniqueFileName);
            }

            categoryRepository.save(existingCategory);

        } catch (IOException e) {
            throw new RuntimeException("Error updating category image", e);
        }
    }
}
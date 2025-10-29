package com.econova.controller;

import com.econova.entity.Category;
import com.econova.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/admin/category") // Make URL prefix unique
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	private static final String UPLOAD_DIR = "src/main/resources/static/uploads/categories/";

	// Show add category form
	@GetMapping("/add")
	public String showAddCategoryForm(Model model) {
		model.addAttribute("category", new Category());
		return "add_category";
	}

	// Add category
	@PostMapping("/add")
	public String addCategory(@RequestParam("name") String name, @RequestParam("description") String description,
			@RequestParam("image") MultipartFile image, Model model) {

		try {
			String fileName = image.getOriginalFilename();
			if (fileName != null && !fileName.isEmpty()) {
				Path filePath = Paths.get(UPLOAD_DIR + fileName);
				Files.createDirectories(filePath.getParent());
				Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				Category category = new Category();
				category.setName(name);
				category.setDescription(description);
				category.setImageUrl("/uploads/categories/" + fileName);

				categoryService.saveCategory(category);
				model.addAttribute("success", "Category added successfully!");
			} else {
				model.addAttribute("error", "Please select an image!");
			}

		} catch (IOException e) {
			model.addAttribute("error", "Image upload failed!");
			e.printStackTrace();
		}

		return "add_category";
	}

	// Show all categories
	@GetMapping("/manage")
	public String manageCategories(Model model) {
		model.addAttribute("categories", categoryService.getAllCategories());
		return "manage_category";
	}

	// Delete category
	@GetMapping("/delete/{id}")
	public String deleteCategory(@PathVariable("id") Long id) {
		categoryService.deleteCategory(id);
		return "redirect:/admin/category/manage";
	}

	// Edit category
	@GetMapping("/edit/{id}")
	public String editCategory(@PathVariable Long id, Model model) {
	    Category category = categoryService.getCategoryById(id);
	    model.addAttribute("category", category);
	    return "edit_category";
	}

	// Update category
	@PostMapping("/update")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("image") MultipartFile imageFile) {
	    categoryService.updateCategory(category, imageFile);
	    return "redirect:/admin/category/manage";
	}
	
	


}

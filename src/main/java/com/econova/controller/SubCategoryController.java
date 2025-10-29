package com.econova.controller;

import com.econova.entity.SubCategory;
import com.econova.repository.CategoryRepository;
import com.econova.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/subcategory")
public class SubCategoryController {

	@Autowired
	private SubCategoryService subCategoryService;

	@Autowired
	private CategoryRepository categoryRepository;

	@GetMapping("/add")
	public String showAddSubCategoryForm(Model model) {
		model.addAttribute("subcategory", new SubCategory());
		model.addAttribute("categories", categoryRepository.findAll());
		return "subcategory_add";
	}

	@PostMapping("/add")
	public String addSubCategory(@ModelAttribute SubCategory subCategory, @RequestParam("image") MultipartFile image,
			Model model) {

		subCategoryService.saveSubCategory(subCategory, image);
		model.addAttribute("success", "Subcategory added successfully!");
		model.addAttribute("categories", categoryRepository.findAll());
		model.addAttribute("subcategory", new SubCategory());
		return "subcategory_add";
	}

	@GetMapping("/manage")
	public String manageSubCategories(Model model) {
		model.addAttribute("subcategories", subCategoryService.getAllSubCategories());
		return "subcategory_manage";
	}

	@GetMapping("/delete/{id}")
	public String deleteSubCategory(@PathVariable("id") Long id) {
		subCategoryService.deleteSubCategory(id);
		return "redirect:/admin/subcategory/manage";
	}
	
	@GetMapping("/edit/{id}")
	public String showEditSubCategoryForm(@PathVariable("id") Long id, Model model) {
	    SubCategory subCategory = subCategoryService.getSubCategoryById(id);
	    model.addAttribute("subcategory", subCategory);
	    model.addAttribute("categories", categoryRepository.findAll());
	    return "subcategory_edit"; // points to the edit HTML
	}
	
	@PostMapping("/update")
	public String updateSubCategory(@ModelAttribute SubCategory subCategory,
	                                @RequestParam(value = "image", required = false) MultipartFile image,
	                                Model model) {
	    subCategoryService.updateSubCategory(subCategory, image);
	    model.addAttribute("success", "Subcategory updated successfully!");
	    model.addAttribute("subcategory", subCategory);
	    model.addAttribute("categories", categoryRepository.findAll());
	    return "subcategory_edit";
	}
	


}

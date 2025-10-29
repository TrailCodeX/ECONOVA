package com.econova.service;

import com.econova.entity.SubCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubCategoryService {
	void saveSubCategory(SubCategory subCategory, MultipartFile image);

	List<SubCategory> getAllSubCategories();

	SubCategory getSubCategoryById(Long id);

	void deleteSubCategory(Long id);
	
	void updateSubCategory(SubCategory subCategory, MultipartFile image);
}

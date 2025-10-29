package com.econova.service;

import com.econova.entity.SubCategory;
import com.econova.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

import java.util.List;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

	@Autowired
	private SubCategoryRepository subCategoryRepo;

	private static final String UPLOAD_DIR = "src/main/resources/static/uploads/subcategories/";

	@Override
	public void saveSubCategory(SubCategory subCategory, MultipartFile image) {
		try {
			if (!image.isEmpty()) {
				String fileName = image.getOriginalFilename();
				Path path = Paths.get(UPLOAD_DIR + fileName);
				Files.createDirectories(path.getParent());
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				subCategory.setImageUrl("/uploads/subcategories/" + fileName);
			}
			subCategoryRepo.save(subCategory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<SubCategory> getAllSubCategories() {
		return subCategoryRepo.findAll();
	}

	@Override
	public SubCategory getSubCategoryById(Long id) {
		return subCategoryRepo.findById(id).orElse(null);
	}

	@Override
	public void deleteSubCategory(Long id) {
		subCategoryRepo.deleteById(id);
	}

	public void updateSubCategory(SubCategory subCategory, MultipartFile image) {
		try {
			SubCategory existing = subCategoryRepo.findById(subCategory.getId()).orElse(null); // <-- use
																								// subCategoryRepo
			if (existing != null) {
				existing.setName(subCategory.getName());
				existing.setDescription(subCategory.getDescription());
				existing.setCategory(subCategory.getCategory());

				if (image != null && !image.isEmpty()) {
					Path filePath = Paths.get(UPLOAD_DIR + image.getOriginalFilename());
					Files.createDirectories(filePath.getParent());
					Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
					existing.setImageUrl("/uploads/subcategories/" + image.getOriginalFilename());
				}

				subCategoryRepo.save(existing);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

}

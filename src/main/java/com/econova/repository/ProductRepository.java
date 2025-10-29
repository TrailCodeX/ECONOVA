package com.econova.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.econova.entity.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findBySubcategoryId(Long subcategoryId);

}


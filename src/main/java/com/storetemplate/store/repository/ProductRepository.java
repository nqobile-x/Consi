package com.storetemplate.store.repository;

import com.storetemplate.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrueOrderByCreatedAtDesc();

    List<Product> findByActiveTrueAndCategoryIgnoreCaseOrderByCreatedAtDesc(String category);
}

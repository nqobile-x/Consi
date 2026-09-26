package com.storetemplate.store.service;

import com.storetemplate.store.model.Product;
import com.storetemplate.store.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    public List<Product> listActive() {
        return products.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Product> listActive(String category) {
        if (category == null || category.isBlank()) return listActive();
        return products.findByActiveTrueAndCategoryIgnoreCaseOrderByCreatedAtDesc(category);
    }

    public List<Product> featured(int limit) {
        return listActive().stream().limit(limit).toList();
    }

    public Optional<Product> find(Long id) {
        return products.findById(id);
    }

    public List<Product> all() {
        return products.findAll();
    }

    public Product save(Product p) {
        return products.save(p);
    }

    public void delete(Long id) {
        products.deleteById(id);
    }

    public List<String> categories() {
        return listActive().stream()
                .map(Product::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .toList();
    }
}

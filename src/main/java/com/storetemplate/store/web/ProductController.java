package com.storetemplate.store.web;

import com.storetemplate.store.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    @GetMapping("/products")
    public String list(@RequestParam(required = false) String category, Model model) {
        model.addAttribute("products", products.listActive(category));
        model.addAttribute("categories", products.categories());
        model.addAttribute("activeCategory", category);
        return "products/list";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var product = products.find(id).orElse(null);
        if (product == null || !product.isActive()) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("related", products.featured(4));
        return "products/detail";
    }
}

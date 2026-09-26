package com.storetemplate.store.web;

import com.storetemplate.store.model.OrderStatus;
import com.storetemplate.store.model.Product;
import com.storetemplate.store.service.OrderService;
import com.storetemplate.store.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService products;
    private final OrderService orders;

    public AdminController(ProductService products, OrderService orders) {
        this.products = products;
        this.orders = orders;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("productCount", products.all().size());
        model.addAttribute("orderList", orders.all());
        model.addAttribute("orderCount", orders.all().size());
        return "admin/dashboard";
    }

    // ---- Products ----------------------------------------------------------

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", products.all());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        Product p = products.find(id).orElse(null);
        if (p == null) return "redirect:/admin/products";
        model.addAttribute("product", p);
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) {
        products.save(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        products.delete(id);
        return "redirect:/admin/products";
    }

    // ---- Orders ------------------------------------------------------------

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orders.all());
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        orders.updateStatus(id, status);
        return "redirect:/admin/orders";
    }
}

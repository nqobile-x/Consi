package com.storetemplate.store.web;

import com.storetemplate.store.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductService products;

    public HomeController(ProductService products) {
        this.products = products;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featured", products.featured(4));
        model.addAttribute("lookbook", products.featured(6));
        return "index";
    }
}

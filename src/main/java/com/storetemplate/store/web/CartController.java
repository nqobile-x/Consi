package com.storetemplate.store.web;

import com.storetemplate.store.service.Cart;
import com.storetemplate.store.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final Cart cart;
    private final ProductService products;

    public CartController(Cart cart, ProductService products) {
        this.cart = cart;
        this.products = products;
    }

    @GetMapping
    public String view(Model model) {
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity) {
        products.find(productId).ifPresent(p -> cart.add(p, quantity));
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String update(@RequestParam Long productId, @RequestParam int quantity) {
        cart.setQuantity(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long productId) {
        cart.remove(productId);
        return "redirect:/cart";
    }
}

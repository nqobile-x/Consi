package com.storetemplate.store.web;

import com.storetemplate.store.model.AppUser;
import com.storetemplate.store.model.CustomerOrder;
import com.storetemplate.store.service.Cart;
import com.storetemplate.store.service.OrderService;
import com.storetemplate.store.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class CheckoutController {

    private final Cart cart;
    private final OrderService orders;
    private final UserService users;

    public CheckoutController(Cart cart, OrderService orders, UserService users) {
        this.cart = cart;
        this.orders = orders;
        this.users = users;
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        AppUser user = users.findByEmail(principal.getName()).orElse(null);
        CustomerOrder prefill = new CustomerOrder();
        if (user != null) {
            prefill.setRecipientName(user.getFullName());
            prefill.setPhone(user.getPhone());
            prefill.setCountry("South Africa");
        }
        model.addAttribute("order", prefill);
        model.addAttribute("cart", cart);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@ModelAttribute("order") CustomerOrder shipping, Principal principal) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        AppUser user = users.findByEmail(principal.getName()).orElse(null);
        CustomerOrder saved = orders.placeOrder(user, cart, shipping);
        cart.clear();
        return "redirect:/account/orders/" + saved.getId() + "?placed";
    }
}

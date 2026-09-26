package com.storetemplate.store.web;

import com.storetemplate.store.model.AppUser;
import com.storetemplate.store.model.CustomerOrder;
import com.storetemplate.store.service.OrderService;
import com.storetemplate.store.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService users;
    private final OrderService orders;

    public AccountController(UserService users, OrderService orders) {
        this.users = users;
        this.orders = orders;
    }

    @GetMapping
    public String account(Model model, Principal principal) {
        AppUser user = users.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("orders", orders.forUser(user));
        return "account/index";
    }

    @GetMapping("/orders/{id}")
    public String order(@PathVariable Long id, Model model, Principal principal) {
        AppUser user = users.findByEmail(principal.getName()).orElseThrow();
        CustomerOrder order = orders.find(id);
        // Only let a customer see their own order.
        if (order == null || order.getUser() == null
                || !order.getUser().getId().equals(user.getId())) {
            return "redirect:/account";
        }
        model.addAttribute("order", order);
        return "account/order";
    }
}

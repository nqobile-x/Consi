package com.storetemplate.store.web;

import com.storetemplate.store.service.Cart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Injects values every template needs (brand name, live cart count). */
@ControllerAdvice
public class GlobalModelAdvice {

    private final Cart cart;

    @Value("${store.brand-name:ICONSI}")
    private String brandName;

    @Value("${store.tagline:Bold graphic tees, made in Joburg for the world.}")
    private String tagline;

    public GlobalModelAdvice(Cart cart) {
        this.cart = cart;
    }

    @ModelAttribute("brandName")
    public String brandName() { return brandName; }

    @ModelAttribute("tagline")
    public String tagline() { return tagline; }

    @ModelAttribute("cartCount")
    public int cartCount() { return cart.getItemCount(); }
}

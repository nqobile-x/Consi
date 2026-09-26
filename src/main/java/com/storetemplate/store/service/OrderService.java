package com.storetemplate.store.service;

import com.storetemplate.store.model.*;
import com.storetemplate.store.repository.OrderRepository;
import com.storetemplate.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final ProductRepository products;

    public OrderService(OrderRepository orders, ProductRepository products) {
        this.orders = orders;
        this.products = products;
    }

    /**
     * Turns the session cart into a persisted order, decrementing stock. In this
     * template payment is stubbed: the order is created straight in RECEIVED.
     */
    @Transactional
    public CustomerOrder placeOrder(AppUser user, Cart cart, CustomerOrder shipping) {
        shipping.setUser(user);
        shipping.setStatus(OrderStatus.RECEIVED);
        shipping.setTotal(cart.getSubtotal());

        for (Cart.Line line : cart.getLines()) {
            Product product = products.findById(line.getProductId()).orElse(null);
            if (product == null) continue;

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(line.getQuantity());
            item.setPriceAtPurchase(line.getPrice());
            shipping.addItem(item);

            // Best-effort stock decrement for the demo.
            product.setStock(Math.max(0, product.getStock() - line.getQuantity()));
            products.save(product);
        }

        return orders.save(shipping);
    }

    public List<CustomerOrder> forUser(AppUser user) {
        return orders.findByUserOrderByCreatedAtDesc(user);
    }

    public List<CustomerOrder> all() {
        return orders.findAllByOrderByCreatedAtDesc();
    }

    public CustomerOrder find(Long id) {
        return orders.findById(id).orElse(null);
    }

    @Transactional
    public void updateStatus(Long id, OrderStatus status) {
        orders.findById(id).ifPresent(o -> {
            o.setStatus(status);
            orders.save(o);
        });
    }
}

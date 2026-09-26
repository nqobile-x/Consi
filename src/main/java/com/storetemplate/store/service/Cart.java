package com.storetemplate.store.service;

import com.storetemplate.store.model.Product;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * A per-session shopping cart. Kept deliberately simple: a map of product id to
 * an in-memory line holding a snapshot of the product plus a quantity.
 */
@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Cart implements Serializable {

    public static class Line implements Serializable {
        private Long productId;
        private String name;
        private String image;
        private BigDecimal price;
        private int quantity;

        public Long getProductId() { return productId; }
        public String getName() { return name; }
        public String getImage() { return image; }
        public BigDecimal getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getLineTotal() {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
    }

    private final Map<Long, Line> lines = new LinkedHashMap<>();

    public void add(Product product, int qty) {
        Line line = lines.get(product.getId());
        if (line == null) {
            line = new Line();
            line.productId = product.getId();
            line.name = product.getName();
            line.image = product.getPrimaryImage();
            line.price = product.getPrice();
            line.quantity = 0;
            lines.put(product.getId(), line);
        }
        line.quantity = Math.max(1, line.quantity + qty);
    }

    public void setQuantity(Long productId, int qty) {
        Line line = lines.get(productId);
        if (line == null) return;
        if (qty <= 0) {
            lines.remove(productId);
        } else {
            line.quantity = qty;
        }
    }

    public void remove(Long productId) {
        lines.remove(productId);
    }

    public void clear() {
        lines.clear();
    }

    public Collection<Line> getLines() {
        return lines.values();
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public int getItemCount() {
        return lines.values().stream().mapToInt(Line::getQuantity).sum();
    }

    public BigDecimal getSubtotal() {
        return lines.values().stream()
                .map(Line::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

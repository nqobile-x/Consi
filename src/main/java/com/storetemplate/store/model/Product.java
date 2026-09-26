package com.storetemplate.store.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A catalogue item. Image URLs are stored as a single comma-separated string to
 * keep the template schema simple (no extra join table for a demo storefront).
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    private int stock;

    private String category;

    /** Comma-separated image URLs. Use {@link #getImageUrls()} to read as a list. */
    @Column(length = 2000)
    private String imageUrls;

    private boolean active = true;

    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** Convenience: first image, or a placeholder if none set. */
    @Transient
    public String getPrimaryImage() {
        if (imageUrls == null || imageUrls.isBlank()) {
            return "https://picsum.photos/seed/placeholder/600/800";
        }
        return imageUrls.split(",")[0].trim();
    }

    @Transient
    public String[] getImageList() {
        if (imageUrls == null || imageUrls.isBlank()) return new String[0];
        return imageUrls.split(",");
    }
}

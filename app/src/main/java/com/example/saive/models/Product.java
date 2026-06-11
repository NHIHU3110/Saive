package com.example.saive.models;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String price;
    private String originalPrice;
    private int imageResId;
    private String category;
    private String description;
    private long timestamp;

    private int quantity = 1;
    private String selectedSize;
    private String selectedColor;

    public Product(String name, String price, int imageResId, String category) {
        this(name, price, null, imageResId, category);
    }

    public Product(String name, String price, String originalPrice, int imageResId, String category) {
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.imageResId = imageResId;
        this.category = category;
        this.description = "A study of form and function. This piece is crafted from premium materials with meticulous attention to detail.";
        this.timestamp = System.currentTimeMillis();
    }

    public Product(String name, String price, int imageResId, String category, String description) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.category = category;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(String originalPrice) { this.originalPrice = originalPrice; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public String getSelectedColor() { return selectedColor; }
    public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }
}

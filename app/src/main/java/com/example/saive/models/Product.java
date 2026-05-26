package com.example.saive.models;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String price;
    private int imageResId;
    private String category;
    private String description;
    private long timestamp;

    private int quantity = 1;
    private String selectedSize;
    private String selectedColor;

    public Product(String name, String price, int imageResId, String category) {
        this.name = name;
        this.price = price;
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
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public String getSelectedColor() { return selectedColor; }
    public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }
}

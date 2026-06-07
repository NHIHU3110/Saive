package com.example.saive.models;

public class OrderItem {
    private String name;
    private String size;
    private String color;
    private int quantity;
    private String price;
    private int imageResId;

    public OrderItem(String name, String size, String color, int quantity, String price, int imageResId) {
        this.name = name;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.price = price;
        this.imageResId = imageResId;
    }

    public OrderItem(String name, String size, int quantity, String price, int imageResId) {
        this.name = name;
        this.size = size;
        this.color = "Default";
        this.quantity = quantity;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public int getQuantity() { return quantity; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    
    public void setName(String name) { this.name = name; }
    public void setSize(String size) { this.size = size; }
    public void setColor(String color) { this.color = color; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(String price) { this.price = price; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}

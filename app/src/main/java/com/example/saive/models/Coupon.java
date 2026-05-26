package com.example.saive.models;

public class Coupon {
    private String title;
    private String description;
    private String discount;
    private String expiryDate;
    private String code;

    public Coupon(String title, String description, String discount, String expiryDate, String code) {
        this.title = title;
        this.description = description;
        this.discount = discount;
        this.expiryDate = expiryDate;
        this.code = code;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDiscount() { return discount; }
    public String getExpiryDate() { return expiryDate; }
    public String getCode() { return code; }
}

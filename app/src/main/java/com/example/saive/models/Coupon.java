package com.example.saive.models;

public class Coupon {
    private String title;
    private String description;
    private String discount;
    private String expiryDate;
    private String code;
    private String status; // "Active", "Expired", "Scheduled"
    private int usageCount;

    public Coupon(String title, String description, String discount, String expiryDate, String code) {
        this(title, description, discount, expiryDate, code, "Active", 0);
    }

    public Coupon(String title, String description, String discount, String expiryDate, String code, String status, int usageCount) {
        this.title = title;
        this.description = description;
        this.discount = discount;
        this.expiryDate = expiryDate;
        this.code = code;
        this.status = status;
        this.usageCount = usageCount;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDiscount() { return discount; }
    public String getExpiryDate() { return expiryDate; }
    public String getCode() { return code; }
    public String getStatus() { return status; }
    public int getUsageCount() { return usageCount; }
}

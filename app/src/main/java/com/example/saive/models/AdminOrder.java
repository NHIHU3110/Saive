package com.example.saive.models;

public class AdminOrder {
    private String orderId;
    private String customerName;
    private String itemsSummary;
    private String totalAmount;
    private String status;
    private String timeAgo;

    public AdminOrder(String orderId, String customerName, String itemsSummary, String totalAmount, String status, String timeAgo) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.itemsSummary = itemsSummary;
        this.totalAmount = totalAmount;
        this.status = status;
        this.timeAgo = timeAgo;
    }

    public void setStatus(String status) { this.status = status; }
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getItemsSummary() { return itemsSummary; }
    public String getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getTimeAgo() { return timeAgo; }
}
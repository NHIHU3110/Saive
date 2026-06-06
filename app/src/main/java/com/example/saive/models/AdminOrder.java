package com.example.saive.models;

public class AdminOrder {
    private String orderId;
    private String customerName;
    private String itemsSummary;
    private String totalAmount;
    private String status;
    private String timeAgo;
    private int productImageResId;
    private String size;
    private int quantity;
    private String paymentMethod;
    private String shippingAddress;
    private java.util.List<OrderItem> items;

    public AdminOrder(String orderId, String customerName, String itemsSummary, String totalAmount, String status, String timeAgo) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.itemsSummary = itemsSummary;
        this.totalAmount = totalAmount;
        this.status = status;
        this.timeAgo = timeAgo;
        this.productImageResId = 0;
        this.size = "L";
        this.quantity = 1;
        this.paymentMethod = "Momo";
        this.shippingAddress = "123 Le Loi, District 1, HCMC, Vietnam";
    }

    public AdminOrder(String orderId, String customerName, String itemsSummary, String totalAmount, String status, String timeAgo, int productImageResId, String size, int quantity, String paymentMethod, String shippingAddress) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.itemsSummary = itemsSummary;
        this.totalAmount = totalAmount;
        this.status = status;
        this.timeAgo = timeAgo;
        this.productImageResId = productImageResId;
        this.size = size;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
        this.items = new java.util.ArrayList<>();
    }

    public void setItems(java.util.List<OrderItem> items) { this.items = items; }
    public java.util.List<OrderItem> getItems() { return items; }
    public void setStatus(String status) { this.status = status; }
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getItemsSummary() { return itemsSummary; }
    public String getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getTimeAgo() { return timeAgo; }
    public int getProductImageResId() { return productImageResId; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getShippingAddress() { return shippingAddress; }
    public void setProductImageResId(int productImageResId) { this.productImageResId = productImageResId; }
}
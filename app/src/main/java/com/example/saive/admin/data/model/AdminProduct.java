package com.example.saive.admin.data.model;

import com.google.firebase.database.PropertyName;
import java.util.List;

public class AdminProduct {
    private String productId; // populated from the Firebase node key, see FirebaseConnector

    @PropertyName("CategoryId")
    private String categoryId;

    @PropertyName("ProductName")
    private String productName;

    @PropertyName("OriginalPrice")
    private double originalPrice;

    @PropertyName("Price")
    private double price;

    @PropertyName("Images")
    private List<String> images;

    @PropertyName("Description")
    private String description;

    @PropertyName("StockQuantity")
    private int stockQuantity;

    @PropertyName("Rating")
    private double rating;

    @PropertyName("IsDeleted")
    private boolean isDeleted;

    @PropertyName("CreatedAt")
    private String createdAt;

    @PropertyName("IsActive")
    private boolean isActive;

    @PropertyName("IsFeatured")
    private boolean isFeatured;

    @PropertyName("UpdatedAt")
    private String updatedAt;

    @PropertyName("tag_color")
    private List<String> tagColor;

    @PropertyName("tag_style")
    private String tagStyle;

    @PropertyName("tag_type")
    private String tagType;

    @PropertyName("tag_type_group")
    private String tagTypeGroup;

    @PropertyName("NumBuy")
    private int numBuy;

    @PropertyName("Stock")
    private java.util.Map<String, java.util.Map<String, Integer>> variantsStock;

    public AdminProduct() {
        // Required for Firebase
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    @PropertyName("CategoryId")
    public String getCategoryId() { return categoryId; }
    @PropertyName("CategoryId")
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    @PropertyName("ProductName")
    public String getProductName() { return productName; }
    @PropertyName("ProductName")
    public void setProductName(String productName) { this.productName = productName; }

    @PropertyName("OriginalPrice")
    public double getOriginalPrice() { return originalPrice; }
    @PropertyName("OriginalPrice")
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    @PropertyName("Price")
    public double getPrice() { return price; }
    @PropertyName("Price")
    public void setPrice(double price) { this.price = price; }

    @PropertyName("Images")
    public List<String> getImages() { return images; }
    @PropertyName("Images")
    public void setImages(List<String> images) { this.images = images; }

    @PropertyName("Description")
    public String getDescription() { return description; }
    @PropertyName("Description")
    public void setDescription(String description) { this.description = description; }

    @PropertyName("StockQuantity")
    public int getStockQuantity() { return stockQuantity; }
    @PropertyName("StockQuantity")
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    @PropertyName("Rating")
    public double getRating() { return rating; }
    @PropertyName("Rating")
    public void setRating(double rating) { this.rating = rating; }

    @PropertyName("IsDeleted")
    public boolean isDeleted() { return isDeleted; }
    @PropertyName("IsDeleted")
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    @PropertyName("CreatedAt")
    public String getCreatedAt() { return createdAt; }
    @PropertyName("CreatedAt")
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @PropertyName("IsActive")
    public boolean isActive() { return isActive; }
    @PropertyName("IsActive")
    public void setActive(boolean active) { isActive = active; }

    @PropertyName("IsFeatured")
    public boolean isFeatured() { return isFeatured; }
    @PropertyName("IsFeatured")
    public void setFeatured(boolean featured) { isFeatured = featured; }

    @PropertyName("UpdatedAt")
    public String getUpdatedAt() { return updatedAt; }
    @PropertyName("UpdatedAt")
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @PropertyName("tag_color")
    public List<String> getTagColor() { return tagColor; }
    @PropertyName("tag_color")
    public void setTagColor(List<String> tagColor) { this.tagColor = tagColor; }

    @PropertyName("tag_style")
    public String getTagStyle() { return tagStyle; }
    @PropertyName("tag_style")
    public void setTagStyle(String tagStyle) { this.tagStyle = tagStyle; }

    @PropertyName("tag_type")
    public String getTagType() { return tagType; }
    @PropertyName("tag_type")
    public void setTagType(String tagType) { this.tagType = tagType; }

    @PropertyName("tag_type_group")
    public String getTagTypeGroup() { return tagTypeGroup; }
    @PropertyName("tag_type_group")
    public void setTagTypeGroup(String tagTypeGroup) { this.tagTypeGroup = tagTypeGroup; }

    @PropertyName("NumBuy")
    public int getNumBuy() { return numBuy; }
    @PropertyName("NumBuy")
    public void setNumBuy(int numBuy) { this.numBuy = numBuy; }

    @PropertyName("Stock")
    public java.util.Map<String, java.util.Map<String, Integer>> getVariantsStock() { return variantsStock; }
    @PropertyName("Stock")
    public void setVariantsStock(java.util.Map<String, java.util.Map<String, Integer>> variantsStock) { this.variantsStock = variantsStock; }

    @com.google.firebase.database.Exclude
    public int getCalculatedTotalStock() {
        if (variantsStock == null || variantsStock.isEmpty()) {
            return stockQuantity;
        }
        int total = 0;
        for (java.util.Map<String, Integer> colorMap : variantsStock.values()) {
            if (colorMap != null) {
                for (Object qtyObj : colorMap.values()) {
                    if (qtyObj instanceof Number) {
                        total += ((Number) qtyObj).intValue();
                    }
                }
            }
        }
        return total;
    }

    /** Convenience helper: first image of the gallery, or null if none. */
    @com.google.firebase.database.Exclude
    public String getFirstImage() {
        return (images != null && !images.isEmpty()) ? images.get(0) : null;
    }
}
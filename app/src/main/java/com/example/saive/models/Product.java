package com.example.saive.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String name;
    private String price;
    private String originalPrice;
    private int imageResId;
    
    @SerializedName(value = "imageUrl", alternate = {"image", "img", "productImage", "thumbnail"})
    private String imageUrl;      // URL ảnh chính từ server

    @SerializedName(value = "imageUrls", alternate = {"images", "gallery", "imageList"})
    private java.util.List<String> imageUrls; // Danh sách các URL ảnh cho slideshow
    private java.util.List<Integer> imageResIds; // Danh sách các resource ID ảnh local
    private String productId;     // ID từ MongoDB
    private String category;
    private String description;
    private long timestamp;
    private boolean isFeatured;
    /** tag_type_group từ Firebase: top, bottom, dress, outerwear, shoes, bag, accessory */
    private String tagTypeGroup;

    /** tag_style từ Firebase: casual, formal, streetwear, minimalist, bohemian, ... */
    @SerializedName(value = "tagStyle", alternate = {"tag_style"})
    private String tagStyle;

    /** tag_color từ Firebase: danh sách màu sắc */
    @SerializedName(value = "tagColor", alternate = {"tag_color"})
    private List<String> tagColor;

    /** tag_type từ Firebase: chi tiết loại sản phẩm */
    @SerializedName(value = "tagType", alternate = {"tag_type"})
    private String tagType;

    @SerializedName("StockQuantity")
    private int stockQuantity;

    @SerializedName(value = "variantsStock", alternate = {"Variants", "Stock"})
    private java.util.Map<String, java.util.Map<String, Integer>> variantsStock;

    private int quantity = 1;
    private String selectedSize;
    private String selectedColor;

    public Product(String name, String price, int imageResId, String category) {
        this(name, price, null, imageResId, category);
    }

    public Product(String name, String price, String originalPrice, int imageResId, String category) {
        this(name, price, originalPrice, imageResId, category, "A study of form and function. This piece is crafted from premium materials with meticulous attention to detail.");
    }

    public Product(String name, String price, String originalPrice, int imageResId, String category, String description) {
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.imageResId = imageResId;
        this.category = category;
        this.description = description;
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
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public java.util.List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(java.util.List<String> imageUrls) { this.imageUrls = imageUrls; }

    public java.util.List<Integer> getImageResIds() { return imageResIds; }
    public void setImageResIds(java.util.List<Integer> imageResIds) { this.imageResIds = imageResIds; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTagTypeGroup() { return tagTypeGroup; }
    public void setTagTypeGroup(String tagTypeGroup) { this.tagTypeGroup = tagTypeGroup; }

    public String getTagStyle() { return tagStyle; }
    public void setTagStyle(String tagStyle) { this.tagStyle = tagStyle; }

    public List<String> getTagColor() { return tagColor; }
    public void setTagColor(List<String> tagColor) { this.tagColor = tagColor; }

    public String getTagType() { return tagType; }
    public void setTagType(String tagType) { this.tagType = tagType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public java.util.Map<String, java.util.Map<String, Integer>> getVariantsStock() { return variantsStock; }
    public void setVariantsStock(java.util.Map<String, java.util.Map<String, Integer>> variantsStock) { this.variantsStock = variantsStock; }

    public int getStockForVariant(String size, String color) {
        if (variantsStock == null) return stockQuantity;
        
        // If both are provided, get specific stock
        if (size != null && color != null) {
            java.util.Map<String, Integer> colorMap = variantsStock.get(size);
            if (colorMap != null) {
                // Try exact match first
                if (colorMap.containsKey(color)) return colorMap.get(color);
                
                // Try case-insensitive match
                for (String key : colorMap.keySet()) {
                    if (key.equalsIgnoreCase(color)) return colorMap.get(key);
                }
            }
            return 0; // Variant defined but no stock for this specific color
        }
        
        // If only size is provided, sum all colors for that size
        if (size != null) {
            java.util.Map<String, Integer> colorMap = null;
            // Case-insensitive size lookup
            for (String key : variantsStock.keySet()) {
                if (key.equalsIgnoreCase(size)) {
                    colorMap = variantsStock.get(key);
                    break;
                }
            }

            if (colorMap != null) {
                int sum = 0;
                for (int s : colorMap.values()) sum += s;
                return sum;
            }
            return 0;
        }

        // NEW: If only color is provided, sum all sizes for that color
        if (color != null) {
            int sum = 0;
            for (java.util.Map<String, Integer> colorMap : variantsStock.values()) {
                for (java.util.Map.Entry<String, Integer> entry : colorMap.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase(color)) {
                        sum += entry.getValue();
                    }
                }
            }
            return sum;
        }

        return stockQuantity;
    }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public String getSelectedColor() { return selectedColor; }
    public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return name != null ? name.equals(product.name) : product.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

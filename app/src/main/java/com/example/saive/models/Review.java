package com.example.saive.models;

import java.io.Serializable;
import java.util.List;

public class Review implements Serializable {
    private String userName;
    private float rating;
    private String comment;
    private String date;
    private List<String> imageUrls; // Can be local URIs or URLs

    private String productName;
    private boolean isApproved;

    public Review(String productName, String userName, float rating, String comment, String date, List<String> imageUrls) {
        this.productName = productName;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.imageUrls = imageUrls;
        this.isApproved = false;
    }

    public String getProductName() { return productName; }
    public String getUserName() { return userName; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }
    public List<String> getImageUrls() { return imageUrls; }
    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }
}

package com.example.saive.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.saive.models.Coupon;
import com.example.saive.models.Product;
import com.example.saive.models.AdminOrder;
import com.example.saive.models.User;
import com.example.saive.models.Review;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String PREF_NAME = "saive_data_prefs";
    private static final String KEY_COUPONS = "coupons";
    private static final String KEY_PRODUCTS = "products";
    private static final String KEY_ORDERS = "orders";
    private static final String KEY_USERS = "users";
    private static final String KEY_REVIEWS = "reviews";
    private static final String KEY_FLASH_SALE = "flash_sale";
    private static final String KEY_PAYMENT_CARDS = "payment_cards";

    private static DataManager instance;
    private Context context;
    private Gson gson;
    private SharedPreferences prefs;

    private DataManager(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson();
        this.prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    // --- Coupons ---
    public List<Coupon> getCoupons() {
        String json = prefs.getString(KEY_COUPONS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Coupon>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveCoupons(List<Coupon> coupons) {
        prefs.edit().putString(KEY_COUPONS, gson.toJson(coupons)).apply();
    }

    public void addCoupon(Coupon coupon) {
        List<Coupon> coupons = getCoupons();
        coupons.add(0, coupon);
        saveCoupons(coupons);
    }

    // --- Products ---
    public List<Product> getProducts() {
        String json = prefs.getString(KEY_PRODUCTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveProducts(List<Product> products) {
        prefs.edit().putString(KEY_PRODUCTS, gson.toJson(products)).apply();
    }

    // --- Orders ---
    public List<AdminOrder> getOrders() {
        String json = prefs.getString(KEY_ORDERS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<AdminOrder>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveOrders(List<AdminOrder> orders) {
        prefs.edit().putString(KEY_ORDERS, gson.toJson(orders)).apply();
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        List<AdminOrder> orders = getOrders();
        for (AdminOrder order : orders) {
            if (order.getOrderId().equals(orderId)) {
                order.setStatus(newStatus);
                break;
            }
        }
        saveOrders(orders);
    }

    // --- Users ---
    public List<User> getUsers() {
        String json = prefs.getString(KEY_USERS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveUsers(List<User> users) {
        prefs.edit().putString(KEY_USERS, gson.toJson(users)).apply();
    }

    public void setUserBlocked(String userEmail, boolean isBlocked) {
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getEmail().equals(userEmail)) {
                user.setBlocked(isBlocked);
                break;
            }
        }
        saveUsers(users);
    }
    
    public boolean isUserBlocked(String email) {
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user.isBlocked();
            }
        }
        return false;
    }

    // --- Reviews ---
    public List<Review> getReviews() {
        String json = prefs.getString(KEY_REVIEWS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Review>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveReviews(List<Review> reviews) {
        prefs.edit().putString(KEY_REVIEWS, gson.toJson(reviews)).apply();
    }

    public void addReview(Review review) {
        List<Review> reviews = getReviews();
        reviews.add(0, review);
        saveReviews(reviews);
    }

    // --- Flash Sale ---
    private static final String KEY_FLASH_SALE_END_TIME = "flash_sale_end_time";
    private static final String KEY_FLASH_SALE_PRODUCTS = "flash_sale_products";

    public void setFlashSaleEndTime(long endTimeMillis) {
        prefs.edit().putLong(KEY_FLASH_SALE_END_TIME, endTimeMillis).apply();
    }

    public long getFlashSaleEndTime() {
        return prefs.getLong(KEY_FLASH_SALE_END_TIME, 0);
    }

    public List<Product> getFlashSaleProducts() {
        String json = prefs.getString(KEY_FLASH_SALE_PRODUCTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveFlashSaleProducts(List<Product> products) {
        prefs.edit().putString(KEY_FLASH_SALE_PRODUCTS, gson.toJson(products)).apply();
    }

    public void setFlashSale(String productId, double discountPercent, long endTimeMillis) {
        prefs.edit().putString(KEY_FLASH_SALE + "_" + productId, discountPercent + ":" + endTimeMillis).apply();
    }

    public String getFlashSale(String productId) {
        return prefs.getString(KEY_FLASH_SALE + "_" + productId, null);
    }

    // --- Payment Cards ---
    public List<com.example.saive.models.PaymentCard> getPaymentCards() {
        String json = prefs.getString(KEY_PAYMENT_CARDS, null);
        if (json == null) {
            List<com.example.saive.models.PaymentCard> initial = new ArrayList<>();
            initial.add(new com.example.saive.models.PaymentCard("1234567890123456", "THAO NHI HUYNH", "12/26", "123", "VISA"));
            return initial;
        }
        Type type = new TypeToken<ArrayList<com.example.saive.models.PaymentCard>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void savePaymentCards(List<com.example.saive.models.PaymentCard> cards) {
        prefs.edit().putString(KEY_PAYMENT_CARDS, gson.toJson(cards)).apply();
    }

    public void addPaymentCard(com.example.saive.models.PaymentCard card) {
        List<com.example.saive.models.PaymentCard> cards = getPaymentCards();
        cards.add(card);
        savePaymentCards(cards);
    }

    public void removePaymentCard(int index) {
        List<com.example.saive.models.PaymentCard> cards = getPaymentCards();
        if (index >= 0 && index < cards.size()) {
            cards.remove(index);
            savePaymentCards(cards);
        }
    }
}

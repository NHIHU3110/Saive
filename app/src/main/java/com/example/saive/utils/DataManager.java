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

    // Memory Cache
    private List<Coupon> cachedCoupons;
    private List<Product> cachedProducts;
    private List<AdminOrder> cachedOrders;
    private List<User> cachedUsers;
    private List<Review> cachedReviews;
    private List<Product> cachedFlashSaleProducts;
    private List<com.example.saive.models.PaymentCard> cachedPaymentCards;

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

    // Clear cache if needed (e.g., on logout)
    public void clearCache() {
        cachedCoupons = null;
        cachedProducts = null;
        cachedOrders = null;
        cachedUsers = null;
        cachedReviews = null;
        cachedFlashSaleProducts = null;
        cachedPaymentCards = null;
    }

    // --- Coupons ---
    public List<Coupon> getCoupons() {
        if (cachedCoupons != null) return new ArrayList<>(cachedCoupons);
        
        String json = prefs.getString(KEY_COUPONS, null);
        if (json == null) {
            List<Coupon> defaults = new ArrayList<>();
            defaults.add(new Coupon("SAIVE WELCOME", "On your first archive access.", "20%", "2024-12-31", "WELCOME20", "Active", 856));
            defaults.add(new Coupon("SILK STORY", "Exclusive for Silk series.", "15%", "2024-11-15", "SILK15", "Active", 142));
            defaults.add(new Coupon("REWARD", "Loyalty reward for you.", "10%", "2025-01-01", "REWARD10", "Active", 50));
            cachedCoupons = new ArrayList<>(defaults);
            return defaults;
        }
        Type type = new TypeToken<ArrayList<Coupon>>() {}.getType();
        cachedCoupons = gson.fromJson(json, type);
        return cachedCoupons != null ? new ArrayList<>(cachedCoupons) : new ArrayList<>();
    }

    public void saveCoupons(List<Coupon> coupons) {
        cachedCoupons = coupons != null ? new ArrayList<>(coupons) : null;
        prefs.edit().putString(KEY_COUPONS, gson.toJson(coupons)).apply();
    }

    public void addCoupon(Coupon coupon) {
        List<Coupon> coupons = getCoupons();
        coupons.add(0, coupon);
        saveCoupons(coupons);
    }

    // --- Products ---
    public List<Product> getProducts() {
        if (cachedProducts != null) return new ArrayList<>(cachedProducts);
        
        String json = prefs.getString(KEY_PRODUCTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        cachedProducts = gson.fromJson(json, type);
        return cachedProducts != null ? new ArrayList<>(cachedProducts) : new ArrayList<>();
    }

    public void saveProducts(List<Product> products) {
        cachedProducts = products != null ? new ArrayList<>(products) : null;
        prefs.edit().putString(KEY_PRODUCTS, gson.toJson(products)).apply();
    }

    // --- Orders ---
    public List<AdminOrder> getOrders() {
        if (cachedOrders != null) return new ArrayList<>(cachedOrders);
        
        String json = prefs.getString(KEY_ORDERS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<AdminOrder>>() {}.getType();
        cachedOrders = gson.fromJson(json, type);
        return cachedOrders != null ? new ArrayList<>(cachedOrders) : new ArrayList<>();
    }

    public void saveOrders(List<AdminOrder> orders) {
        cachedOrders = orders != null ? new ArrayList<>(orders) : null;
        prefs.edit().putString(KEY_ORDERS, gson.toJson(orders)).apply();
    }

    public void addOrder(AdminOrder order) {
        List<AdminOrder> orders = getOrders();
        orders.add(0, order);
        saveOrders(orders);
    }

    public AdminOrder getOrderById(String orderId) {
        List<AdminOrder> orders = getOrders();
        for (AdminOrder order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
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
        if (cachedUsers != null) return new ArrayList<>(cachedUsers);
        
        String json = prefs.getString(KEY_USERS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        cachedUsers = gson.fromJson(json, type);
        return cachedUsers != null ? new ArrayList<>(cachedUsers) : new ArrayList<>();
    }

    public void saveUsers(List<User> users) {
        cachedUsers = users != null ? new ArrayList<>(users) : null;
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
        if (cachedReviews != null) return new ArrayList<>(cachedReviews);
        
        String json = prefs.getString(KEY_REVIEWS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Review>>() {}.getType();
        cachedReviews = gson.fromJson(json, type);
        return cachedReviews != null ? new ArrayList<>(cachedReviews) : new ArrayList<>();
    }

    public void saveReviews(List<Review> reviews) {
        cachedReviews = reviews != null ? new ArrayList<>(reviews) : null;
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
        if (cachedFlashSaleProducts != null) return new ArrayList<>(cachedFlashSaleProducts);
        
        String json = prefs.getString(KEY_FLASH_SALE_PRODUCTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        cachedFlashSaleProducts = gson.fromJson(json, type);
        return cachedFlashSaleProducts != null ? new ArrayList<>(cachedFlashSaleProducts) : new ArrayList<>();
    }

    public void saveFlashSaleProducts(List<Product> products) {
        cachedFlashSaleProducts = products != null ? new ArrayList<>(products) : null;
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
        if (cachedPaymentCards != null) return new ArrayList<>(cachedPaymentCards);
        
        String json = prefs.getString(KEY_PAYMENT_CARDS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<com.example.saive.models.PaymentCard>>() {}.getType();
        cachedPaymentCards = gson.fromJson(json, type);
        return cachedPaymentCards != null ? new ArrayList<>(cachedPaymentCards) : new ArrayList<>();
    }

    public void savePaymentCards(List<com.example.saive.models.PaymentCard> cards) {
        cachedPaymentCards = cards != null ? new ArrayList<>(cards) : null;
        prefs.edit().putString(KEY_PAYMENT_CARDS, gson.toJson(cards)).apply();
    }

    public void addPaymentCard(com.example.saive.models.PaymentCard card) {
        List<com.example.saive.models.PaymentCard> cards = getPaymentCards();
        cards.add(card);
        savePaymentCards(cards);
    }

}

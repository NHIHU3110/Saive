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

    private static final String KEY_DATA_VERSION = "data_version";
    private static final int CURRENT_DATA_VERSION = 3; // Version 3 for multi-item orders demo

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
        int savedVersion = prefs.getInt(KEY_DATA_VERSION, 0);

        if (json == null || savedVersion < CURRENT_DATA_VERSION) {
            List<Product> defaults = new ArrayList<>();
            defaults.add(new Product("Structured Wool Coat", "1.200.000 ₫", com.example.saive.R.mipmap.jacket1, "Jackets"));
            defaults.add(new Product("Archive Parka", "2.100.000 ₫", com.example.saive.R.mipmap.jacket2, "Jackets"));
            defaults.add(new Product("Classic Cotton T-Shirt", "350.000 ₫", com.example.saive.R.mipmap.tshirt1, "T-Shirts"));
            defaults.add(new Product("Straight Fit Jeans", "850.000 ₫", com.example.saive.R.mipmap.pant1, "Pants"));
            defaults.add(new Product("Minimalist Bomber", "1.500.000 ₫", com.example.saive.R.mipmap.jacket3, "Jackets"));
            defaults.add(new Product("Slim Tailored Pants", "950.000 ₫", com.example.saive.R.mipmap.pant3, "Pants"));
            defaults.add(new Product("Signature Aviators", "450.000 ₫", com.example.saive.R.mipmap.sunglass1, "Accessories"));
            
            cachedProducts = new ArrayList<>(defaults);
            saveProducts(defaults);
            // We'll update the version in getOrders to ensure both are synced
            return defaults;
        }
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
        int savedVersion = prefs.getInt(KEY_DATA_VERSION, 0);

        // Force reload if data is missing or old version
        if (json == null || savedVersion < CURRENT_DATA_VERSION) {
            List<AdminOrder> defaults = new ArrayList<>();
            
            // Order 1: Delivered, 2 items
            AdminOrder order1 = new AdminOrder("ORD-2024-001", "John Doe", "Structured Wool Coat, Archive Parka", "3.300.000 ₫", "DELIVERED", "2 days ago");
            List<com.example.saive.models.OrderItem> items1 = new ArrayList<>();
            items1.add(new com.example.saive.models.OrderItem("Structured Wool Coat", "L", "Black", 1, "1.200.000 ₫", com.example.saive.R.mipmap.jacket1));
            items1.add(new com.example.saive.models.OrderItem("Archive Parka", "XL", "Green", 1, "2.100.000 ₫", com.example.saive.R.mipmap.jacket2));
            order1.setItems(items1);
            order1.setShippingAddress("123 Le Loi, District 1, HCMC");
            order1.setPaymentMethod("Momo");
            defaults.add(order1);

            // Order 2: Delivered, 3 items
            AdminOrder order2 = new AdminOrder("ORD-2024-002", "Jane Smith", "T-Shirt, Jeans, Bomber", "2.700.000 ₫", "DELIVERED", "5 days ago");
            List<com.example.saive.models.OrderItem> items2 = new ArrayList<>();
            items2.add(new com.example.saive.models.OrderItem("Classic Cotton T-Shirt", "M", "White", 1, "350.000 ₫", com.example.saive.R.mipmap.tshirt1));
            items2.add(new com.example.saive.models.OrderItem("Straight Fit Jeans", "32", "Blue", 1, "850.000 ₫", com.example.saive.R.mipmap.pant1));
            items2.add(new com.example.saive.models.OrderItem("Minimalist Bomber", "L", "Navy", 1, "1.500.000 ₫", com.example.saive.R.mipmap.jacket3));
            order2.setItems(items2);
            order2.setShippingAddress("456 Nguyen Hue, District 1, HCMC");
            order2.setPaymentMethod("COD");
            defaults.add(order2);

            // Order 3: Pending, 1 item
            AdminOrder order3 = new AdminOrder("ORD-2024-003", "Alex Wilson", "Slim Tailored Pants", "950.000 ₫", "PENDING", "Just now");
            List<com.example.saive.models.OrderItem> items3 = new ArrayList<>();
            items3.add(new com.example.saive.models.OrderItem("Slim Tailored Pants", "30", "Grey", 1, "950.000 ₫", com.example.saive.R.mipmap.pant3));
            order3.setItems(items3);
            order3.setShippingAddress("789 Pasteur, District 3, HCMC");
            order3.setPaymentMethod("Credit Card");
            defaults.add(order3);

            cachedOrders = new ArrayList<>(defaults);
            saveOrders(defaults);
            
            // Update version after successfully initializing defaults
            prefs.edit().putInt(KEY_DATA_VERSION, CURRENT_DATA_VERSION).apply();

            return defaults;
        }
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

    private List<OnReviewChangeListener> reviewListeners = new ArrayList<>();

    public interface OnReviewChangeListener {
        void onReviewsChanged();
    }

    public void addReviewListener(OnReviewChangeListener listener) {
        if (!reviewListeners.contains(listener)) {
            reviewListeners.add(listener);
        }
    }

    public void removeReviewListener(OnReviewChangeListener listener) {
        reviewListeners.remove(listener);
    }

    private void notifyReviewListeners() {
        for (OnReviewChangeListener listener : reviewListeners) {
            listener.onReviewsChanged();
        }
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
        notifyReviewListeners();
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

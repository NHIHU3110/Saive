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
    private String currentCachedUserId; // Thêm để quản lý cache theo user
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
            instance.startListeningReviews();
            instance.startListeningProducts(); // Bắt đầu lắng nghe thay đổi sản phẩm & kho từ Firebase
        }
        return instance;
    }

    // Clear cache if needed (e.g., on logout)
    public void clearCache() {
        cachedCoupons = null;
        cachedProducts = null;
        cachedOrders = null;
        currentCachedUserId = null;
        cachedUsers = null;
        cachedReviews = null;
        cachedFlashSaleProducts = null;
        cachedPaymentCards = null;
    }

    /**
     * Xóa sạch dữ liệu cá nhân của người dùng khỏi Disk (SharedPreferences) và RAM.
     * Gọi khi đăng xuất.
     */
    public void clearAllUserData() {
        // 1. Clear RAM
        clearCache();

        // 2. Clear Disk (SharedPreferences)
        // Lưu ý: Chỉ xóa dữ liệu nhạy cảm/cá nhân. 
        // Giữ lại 'products' và 'coupons' vì đó là dữ liệu chung của hệ thống.
        SharedPreferences.Editor editor = prefs.edit();
        
        // Xóa danh sách thẻ
        editor.remove(KEY_PAYMENT_CARDS);
        
        // Xóa đơn hàng (vì getOrdersKey dùng suffix userId, ta cần tìm tất cả các key liên quan hoặc đơn giản là xóa prefix)
        // Tuy nhiên SharedPreferences không hỗ trợ xóa theo prefix dễ dàng. 
        // Cách an toàn nhất cho demo này là clear các key chính.
        editor.remove(KEY_ORDERS);
        
        // Nếu có các key order dạng orders_U001, orders_U002... 
        // ta nên dùng clear() nếu file này chỉ chứa dữ liệu user, 
        // nhưng file này chứa cả KEY_PRODUCTS, KEY_COUPONS.
        // Vì vậy ta sẽ duyệt qua tất cả keys để xóa những cái bắt đầu bằng KEY_ORDERS.
        java.util.Map<String, ?> allEntries = prefs.getAll();
        for (String key : allEntries.keySet()) {
            if (key.startsWith(KEY_ORDERS)) {
                editor.remove(key);
            }
        }

        editor.apply();
    }

    // --- Coupons ---
    public List<Coupon> getCoupons() {
        if (cachedCoupons != null) return new ArrayList<>(cachedCoupons);
        
        String json = prefs.getString(KEY_COUPONS, null);
        if (json == null) {
            return new ArrayList<>(); // Trả về list trống, fetch từ server sau
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
    private com.google.firebase.database.ValueEventListener productsListener;

    public void startListeningProducts() {
        if (productsListener != null) return;

        com.google.firebase.database.DatabaseReference ref = 
            com.example.saive.admin.connectors.FirebaseConnector.getDatabase().getReference("Products");
        
        productsListener = new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                List<Product> products = new ArrayList<>();
                for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                    try {
                        // Ánh xạ dữ liệu từ Firebase vào model Product (Mobile)
                        // Lưu ý: Trường Stock trong Firebase là Map<String, Map<String, Integer>>
                        String name = child.child("ProductName").getValue(String.class);
                        if (name == null) continue;

                        Product p = new Product(name, String.valueOf(child.child("Price").getValue()), 0, child.child("CategoryId").getValue(String.class));
                        p.setProductId(child.getKey());
                        p.setDescription(child.child("Description").getValue(String.class));

                        // Read tags
                        String tagTypeGroup = child.child("tag_type_group").getValue(String.class);
                        if (tagTypeGroup == null) tagTypeGroup = child.child("TagTypeGroup").getValue(String.class);
                        if (tagTypeGroup != null) p.setTagTypeGroup(tagTypeGroup.trim().toLowerCase());

                        String tagStyle = child.child("tag_style").getValue(String.class);
                        if (tagStyle != null) p.setTagStyle(tagStyle.trim().toLowerCase());

                        String tagType = child.child("tag_type").getValue(String.class);
                        if (tagType != null) p.setTagType(tagType.trim().toLowerCase());

                        java.util.List<String> tagColorList = new java.util.ArrayList<>();
                        com.google.firebase.database.DataSnapshot tagColorSnap = child.child("tag_color");
                        if (tagColorSnap.exists()) {
                            for (com.google.firebase.database.DataSnapshot colorChild : tagColorSnap.getChildren()) {
                                String c = colorChild.getValue(String.class);
                                if (c != null) tagColorList.add(c.trim().toLowerCase());
                            }
                        }
                        if (!tagColorList.isEmpty()) p.setTagColor(tagColorList);
                        
                        // Cập nhật số lượng tổng
                        Integer sq = child.child("StockQuantity").getValue(Integer.class);
                        p.setStockQuantity(sq != null ? sq : 0);
                        
                        // Load variantsStock from Firebase (Variants or Stock node)
                        java.util.Map<String, java.util.Map<String, Integer>> variantsStock = new java.util.HashMap<>();
                        com.google.firebase.database.DataSnapshot variantsSnap = child.child("Variants");
                        if (!variantsSnap.exists()) variantsSnap = child.child("Stock");
                        
                        if (variantsSnap.exists()) {
                            for (com.google.firebase.database.DataSnapshot variantSnap : variantsSnap.getChildren()) {
                                String key = variantSnap.getKey();
                                if (key == null) continue;
                                
                                if (key.contains("_")) {
                                    // Format: {size}_{color} (e.g., M_Black)
                                    String[] parts = key.split("_");
                                    if (parts.length == 2) {
                                        String size = parts[0];
                                        String color = parts[1];
                                        Object s = variantSnap.child("Stock").getValue();
                                        if (s == null) s = variantSnap.getValue();
                                        
                                        if (s instanceof Number) {
                                            java.util.Map<String, Integer> colors = variantsStock.get(size);
                                            if (colors == null) {
                                                colors = new java.util.HashMap<>();
                                                variantsStock.put(size, colors);
                                            }
                                            colors.put(color, ((Number) s).intValue());
                                        }
                                    }
                                } else {
                                    // Legacy Format: {size}/{color}
                                    String size = key;
                                    java.util.Map<String, Integer> colors = variantsStock.get(size);
                                    if (colors == null) {
                                        colors = new java.util.HashMap<>();
                                        variantsStock.put(size, colors);
                                    }
                                    for (com.google.firebase.database.DataSnapshot colorSnap : variantSnap.getChildren()) {
                                        String color = colorSnap.getKey();
                                        Object s = colorSnap.child("Stock").getValue();
                                        if (s == null) s = colorSnap.getValue();
                                        if (s instanceof Number) {
                                            colors.put(color, ((Number) s).intValue());
                                        }
                                    }
                                }
                            }
                        }
                        if (!variantsStock.isEmpty()) p.setVariantsStock(variantsStock);

                        // Ánh xạ danh sách ảnh
                        java.util.List<String> images = new ArrayList<>();
                        com.google.firebase.database.DataSnapshot imgsSnap = child.child("Images");
                        for (com.google.firebase.database.DataSnapshot img : imgsSnap.getChildren()) {
                            images.add(img.getValue(String.class));
                        }
                        p.setImageUrls(images);
                        if (!images.isEmpty()) p.setImageUrl(images.get(0));

                        products.add(p);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                saveProducts(products);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {}
        };
        ref.addValueEventListener(productsListener);
    }

    public List<Product> getProducts() {
        if (cachedProducts != null) return new ArrayList<>(cachedProducts);
        
        String json = prefs.getString(KEY_PRODUCTS, null);
        if (json == null) {
            return new ArrayList<>(); // Trả về list trống, MainActivity sẽ fetch từ server
        }
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        cachedProducts = gson.fromJson(json, type);
        if (cachedProducts != null) {
            // Fix Gson bug: Map<String, Integer> gets deserialized as Map<String, Double>
            // Normalize all stock values back to Integer
            for (Product p : cachedProducts) {
                normalizeVariantsStock(p);
            }
        }
        return cachedProducts != null ? new ArrayList<>(cachedProducts) : new ArrayList<>();
    }

    /**
     * Gson deserializes Map<String, Integer> as Map<String, Double> because of type erasure.
     * This method rebuilds the variantsStock map with proper Integer values.
     */
    private void normalizeVariantsStock(Product p) {
        if (p == null || p.getVariantsStock() == null) return;
        java.util.Map<String, java.util.Map<String, Integer>> normalized = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, java.util.Map<String, Integer>> sizeEntry : p.getVariantsStock().entrySet()) {
            java.util.Map<String, Integer> colorMapNorm = new java.util.HashMap<>();
            if (sizeEntry.getValue() != null) {
                for (java.util.Map.Entry<String, Integer> colorEntry : sizeEntry.getValue().entrySet()) {
                    Object val = colorEntry.getValue();
                    int intVal = 0;
                    if (val instanceof Number) intVal = ((Number) val).intValue();
                    else if (val != null) { try { intVal = Integer.parseInt(val.toString()); } catch (Exception ignored) {} }
                    colorMapNorm.put(colorEntry.getKey(), intVal);
                }
            }
            normalized.put(sizeEntry.getKey(), colorMapNorm);
        }
        p.setVariantsStock(normalized);
    }

    public void saveProducts(List<Product> products) {
        cachedProducts = products != null ? new ArrayList<>(products) : null;
        prefs.edit().putString(KEY_PRODUCTS, gson.toJson(products)).apply();
    }


    // --- Orders ---
    private String getOrdersKey(String userId) {
        return KEY_ORDERS + (userId == null || userId.isEmpty() ? "" : "_" + userId);
    }

    public List<AdminOrder> getOrders(String userId) {
        // Kiểm tra RAM cache trước
        if (cachedOrders != null && userId != null && userId.equals(currentCachedUserId)) {
            return new ArrayList<>(cachedOrders);
        }

        String key = getOrdersKey(userId);
        String json = prefs.getString(key, null);
        
        if (json == null) {
            return new ArrayList<>();
        }
        
        Type type = new TypeToken<ArrayList<AdminOrder>>() {}.getType();
        List<AdminOrder> orders = gson.fromJson(json, type);

        // Cập nhật RAM cache
        if (orders != null) {
            cachedOrders = new ArrayList<>(orders);
            currentCachedUserId = userId;
        }

        return orders != null ? new ArrayList<>(orders) : new ArrayList<>();
    }

    public void saveOrders(List<AdminOrder> orders, String userId) {
        cachedOrders = orders != null ? new ArrayList<>(orders) : null;
        currentCachedUserId = userId;
        prefs.edit().putString(getOrdersKey(userId), gson.toJson(orders)).apply();
    }

    public void addOrder(AdminOrder order, String userId) {
        List<AdminOrder> orders = getOrders(userId);
        orders.add(0, order);
        saveOrders(orders, userId);
    }

    public AdminOrder getOrderById(String orderId, String userId) {
        List<AdminOrder> orders = getOrders(userId);
        for (AdminOrder order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public void updateOrderStatus(String orderId, String newStatus, String userId) {
        List<AdminOrder> orders = getOrders(userId);
        for (AdminOrder order : orders) {
            if (order.getOrderId().equals(orderId)) {
                order.setStatus(newStatus);
                break;
            }
        }
        saveOrders(orders, userId);
        cachedOrders = null; // invalidate cache
    }

    public void updateOrderId(String oldOrderId, String newOrderId, String userId) {
        List<AdminOrder> orders = getOrders(userId);
        for (AdminOrder order : orders) {
            if (order.getOrderId().equals(oldOrderId)) {
                order.setOrderId(newOrderId);
                break;
            }
        }
        saveOrders(orders, userId);
        cachedOrders = null; // invalidate cache
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
        if (userEmail == null) return;
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(userEmail)) {
                user.setBlocked(isBlocked);
                break;
            }
        }
        saveUsers(users);
    }
    
    public boolean isUserBlocked(String email) {
        if (email == null) return false;
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
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
    private com.google.firebase.database.ValueEventListener reviewsListener;

    public void startListeningReviews() {
        if (reviewsListener != null) return;

        com.google.firebase.database.DatabaseReference ref = 
            com.example.saive.admin.connectors.FirebaseConnector.getDatabase().getReference("Reviews");
        
        reviewsListener = new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                List<Review> reviews = new ArrayList<>();
                for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Boolean isApproved = child.child("IsApproved").getValue(Boolean.class);
                        // Chỉ lấy các review đã được duyệt hoặc do user chính mình tạo (nếu muốn)
                        // Ở đây ta lấy tất cả rồi filter sau hoặc chỉ lấy approved
                        if (isApproved != null && isApproved) {
                            Review r = new Review(
                                child.child("ProductName").getValue(String.class),
                                child.child("UserName").getValue(String.class),
                                child.child("Rating").getValue(Float.class),
                                child.child("Comment").getValue(String.class),
                                child.child("Date").getValue(String.class),
                                null // images handled separately if needed
                            );
                            reviews.add(r);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                saveReviews(reviews);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {}
        };
        ref.addValueEventListener(reviewsListener);
    }

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

    public boolean hasPurchasedProduct(String userId, String productName) {
        if (userId == null || userId.isEmpty() || productName == null) return false;
        List<AdminOrder> orders = getOrders(userId);
        for (AdminOrder order : orders) {
            String status = order.getStatus() != null ? order.getStatus().toUpperCase(java.util.Locale.ROOT) : "";
            if (status.equals("COMPLETED") || status.equals("DELIVERED")) {
                if (order.getItems() != null) {
                    for (com.example.saive.models.OrderItem item : order.getItems()) {
                        if (productName.equalsIgnoreCase(item.getName())) {
                            return true;
                        }
                    }
                } else if (productName.equalsIgnoreCase(order.getItemsSummary())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void submitReviewToFirebase(Review review, Runnable onSuccess, Runnable onFailure) {
        // Convert to AdminReview if needed, but since we use direct connector:
        com.google.firebase.database.DatabaseReference ref = com.google.firebase.database.FirebaseDatabase.getInstance("https://saive-403f7-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Reviews");
        
        String reviewId = ref.push().getKey();
        if (reviewId != null) {
            java.util.Map<String, Object> reviewMap = new java.util.HashMap<>();
            reviewMap.put("ProductName", review.getProductName());
            reviewMap.put("UserName", review.getUserName());
            reviewMap.put("Rating", review.getRating());
            reviewMap.put("Comment", review.getComment());
            reviewMap.put("Date", review.getDate());
            reviewMap.put("IsApproved", false);
            
            ref.child(reviewId).setValue(reviewMap)
                    .addOnSuccessListener(aVoid -> {
                        addReview(review); // Add to local cache too
                        if (onSuccess != null) onSuccess.run();
                    })
                    .addOnFailureListener(e -> {
                        if (onFailure != null) onFailure.run();
                    });
        }
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

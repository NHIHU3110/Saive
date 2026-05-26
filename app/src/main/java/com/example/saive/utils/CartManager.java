package com.example.saive.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.saive.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREF_NAME = "saive_cart_prefs";
    private static final String KEY_CART_ITEMS = "cart_items";
    
    private static CartManager instance;
    private List<Product> cartItems;
    private Context context;
    private Gson gson;

    private CartManager(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson();
        this.cartItems = loadCartItems();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    private List<OnCartChangeListener> listeners = new ArrayList<>();

    public void addListener(OnCartChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(OnCartChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (OnCartChangeListener listener : listeners) {
            listener.onCartChanged();
        }
    }

    private void saveCartItems() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = gson.toJson(cartItems);
        prefs.edit().putString(KEY_CART_ITEMS, json).apply();
    }

    private List<Product> loadCartItems() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CART_ITEMS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void addProduct(Product product) {
        boolean exists = false;
        for (Product item : cartItems) {
            if (item.getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                exists = true;
                break;
            }
        }
        if (!exists) {
            product.setQuantity(1);
            cartItems.add(product);
        }
        saveCartItems();
        notifyListeners();
    }

    public void removeProduct(Product product) {
        // Find by name to ensure correct removal after deserialization
        Product itemToRemove = null;
        for (Product item : cartItems) {
            if (item.getName().equals(product.getName())) {
                itemToRemove = item;
                break;
            }
        }
        if (itemToRemove != null) {
            cartItems.remove(itemToRemove);
            saveCartItems();
            notifyListeners();
        }
    }

    public void updateQuantity(Product product, int newQuantity) {
        for (Product item : cartItems) {
            if (item.getName().equals(product.getName())) {
                if (newQuantity <= 0) {
                    removeProduct(item);
                } else {
                    item.setQuantity(newQuantity);
                    saveCartItems();
                    notifyListeners();
                }
                break;
            }
        }
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    public double getTotalPrice() {
        double total = 0;
        for (Product item : cartItems) {
            try {
                String priceStr = item.getPrice().replaceAll("[^\\d]", "");
                if (!priceStr.isEmpty()) {
                    total += Double.parseDouble(priceStr) * item.getQuantity();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (Product item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public void clearCart() {
        cartItems.clear();
        saveCartItems();
        notifyListeners();
    }
}

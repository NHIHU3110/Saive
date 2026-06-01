package com.example.saive.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.FlashSaleGridAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Product;
import com.example.saive.utils.CartManager;

import com.example.saive.utils.DataManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FlashSaleActivity extends BaseActivity {

    private TextView tvHour, tvMinute, tvSecond;
    private RecyclerView rvProducts;
    private CountDownTimer countDownTimer;

    private FlashSaleGridAdapter adapter;
    private List<Product> allProducts;
    private TextView tvCartBadge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_sale);

        initViews();
        setupTimer();
        setupProducts();
        setupCartBadge();
        setupNavigation();
        applyWindowInsets();
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            View bottomNav = findViewById(R.id.bottomNav);
            if (bottomNav != null) {
                bottomNav.setPadding(0, 0, 0, systemBars.bottom);
            }
            return insets;
        });
    }

    private void setupNavigation() {
        View navFavorite = findViewById(R.id.navFavorite);
        if (navFavorite != null) {
            navFavorite.setOnClickListener(v -> navigateToMain("SHOW_FAVORITES"));
        }

        View navWardrobe = findViewById(R.id.navWardrobe);
        if (navWardrobe != null) {
            navWardrobe.setOnClickListener(v -> navigateToMain("SHOW_WARDROBE"));
        }

        View navNotify = findViewById(R.id.navNotify);
        if (navNotify != null) {
            navNotify.setOnClickListener(v -> navigateToMain("SHOW_NOTIFICATIONS"));
        }

        View navProfile = findViewById(R.id.navProfile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        View centerActionButton = findViewById(R.id.centerActionButton);
        if (centerActionButton != null) {
            centerActionButton.setOnClickListener(v -> navigateToMain("SHOW_HOME"));
        }
    }

    private void setupCartBadge() {
        tvCartBadge = findViewById(R.id.tvCartBadge);
        updateCartBadge();
        CartManager.getInstance(this).addListener(this::updateCartBadge);
    }

    private void updateCartBadge() {
        if (tvCartBadge == null) return;
        int count = CartManager.getInstance(this).getItemCount();
        if (count > 0) {
            tvCartBadge.setText(String.valueOf(count));
            tvCartBadge.setVisibility(View.VISIBLE);
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        tvHour = findViewById(R.id.tvHour);
        tvMinute = findViewById(R.id.tvMinute);
        tvSecond = findViewById(R.id.tvSecond);
        rvProducts = findViewById(R.id.rvFlashSaleProducts);

        View topBar = findViewById(R.id.topBar);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), 
                    systemBars.top + (int)(12 * getResources().getDisplayMetrics().density),
                    v.getPaddingRight(), 
                    (int)(16 * getResources().getDisplayMetrics().density));
            return insets;
        });

        View ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                finish();
            });
        }

        View btnCart = findViewById(R.id.btnCart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    private void filterProducts(String query) {
        if (allProducts == null || adapter == null) return;
        
        List<Product> filteredList = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
                product.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.updateList(filteredList);
    }

    private void setupTimer() {
        long endTime = DataManager.getInstance(this).getFlashSaleEndTime();
        long currentTime = System.currentTimeMillis();

        if (endTime > currentTime) {
            long diff = endTime - currentTime;
            countDownTimer = new CountDownTimer(diff, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimerUI(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    if (tvHour != null) tvHour.setText("00");
                    if (tvMinute != null) tvMinute.setText("00");
                    if (tvSecond != null) tvSecond.setText("00");
                }
            }.start();
        } else {
            if (tvHour != null) tvHour.setText("00");
            if (tvMinute != null) tvMinute.setText("00");
            if (tvSecond != null) tvSecond.setText("00");
        }
    }

    private void updateTimerUI(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        if (tvHour != null) tvHour.setText(String.format(Locale.getDefault(), "%02d", hours));
        if (tvMinute != null) tvMinute.setText(String.format(Locale.getDefault(), "%02d", minutes));
        if (tvSecond != null) tvSecond.setText(String.format(Locale.getDefault(), "%02d", seconds));
    }

    private void setupProducts() {
        allProducts = DataManager.getInstance(this).getFlashSaleProducts();
        if (allProducts.isEmpty()) {
            allProducts.add(new Product("NYLON WEATHER", "$30.00 USD", R.mipmap.model1, "Men's Clothing"));
            allProducts.add(new Product("TWILL TEXTILE", "$100.00 USD", R.mipmap.model2, "Men's Clothing"));
            allProducts.add(new Product("STRUCTURED COAT", "$450.00 USD", R.mipmap.jacket1, "Outerwear"));
            allProducts.add(new Product("LINEN SHIRT", "$120.00 USD", R.mipmap.tshirt2, "Shirts"));
            allProducts.add(new Product("MODERN AVIATORS", "$210.00 USD", R.mipmap.sunglass1, "Accessories"));
            allProducts.add(new Product("ARCHIVE PARKA", "$520.00 USD", R.mipmap.jacket2, "Outerwear"));
        }

        adapter = new FlashSaleGridAdapter(allProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
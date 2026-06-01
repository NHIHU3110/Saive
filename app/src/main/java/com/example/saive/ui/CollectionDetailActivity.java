package com.example.saive.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.ProductGridAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Product;
import com.example.saive.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class CollectionDetailActivity extends BaseActivity {

    private ImageView ivHeroLeft, ivHeroRight, ivSectionModel;
    private TextView tvHeadline, tvSubHeadline, tvWhyTitle, tvWhyDesc;
    private RecyclerView rvCollectionProducts;
    private ProductGridAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_detail);

        initViews();
        loadCollectionData();
    }

    private void initViews() {
        ivHeroLeft = findViewById(R.id.ivHeroLeft);
        ivHeroRight = findViewById(R.id.ivHeroRight);
        ivSectionModel = findViewById(R.id.ivSectionModel);
        tvHeadline = findViewById(R.id.tvHeadline);
        tvSubHeadline = findViewById(R.id.tvSubHeadline);
        tvWhyTitle = findViewById(R.id.tvWhyTitle);
        tvWhyDesc = findViewById(R.id.tvWhyDesc);
        rvCollectionProducts = findViewById(R.id.rvCollectionProducts);

        // Pre-set states for animation to avoid "flashing"
        // Reduced translationY to 30f for smoother perceived motion
        View[] animatedViews = {ivHeroLeft, ivHeroRight, tvHeadline, tvSubHeadline, findViewById(R.id.btnShopAll), rvCollectionProducts};
        for (View v : animatedViews) {
            if (v != null) {
                v.setAlpha(0f);
                v.setTranslationY(30f);
            }
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            onBackPressed();
        });

        findViewById(R.id.btnShopAll).setOnClickListener(v -> {
            rvCollectionProducts.getParent().requestChildFocus(rvCollectionProducts, rvCollectionProducts);
        });

        productList = new ArrayList<>();
        productAdapter = new ProductGridAdapter(productList);
        rvCollectionProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvCollectionProducts.setAdapter(productAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    private void loadCollectionData() {
        String title = getIntent().getStringExtra("COLLECTION_TITLE");
        if (title == null) title = "THE SILK STORY";

        String titleUpper = title.toUpperCase();
        com.google.android.material.button.MaterialButton btnShopAll = findViewById(R.id.btnShopAll);
        View heroSection = findViewById(R.id.heroSection);
        View productSection = findViewById(R.id.productSection);

        if (titleUpper.contains("MONOCHROME")) {
            // COLLECTION 1: THE MONOCHROME SERIES - Minimalist Grey/Black
            tvHeadline.setText(R.string.col_monochrome_headline);
            tvSubHeadline.setText(R.string.col_monochrome_subheadline);
            ImageUtils.setSafeImage(ivHeroLeft, R.mipmap.model2);
            ImageUtils.setSafeImage(ivHeroRight, R.mipmap.banner2);
            ImageUtils.setSafeImage(ivSectionModel, R.mipmap.jacket1);
            tvWhyTitle.setText(R.string.col_monochrome_why_title);
            tvWhyDesc.setText(R.string.col_monochrome_why_desc);
            
            if (heroSection != null) heroSection.setBackgroundColor(Color.parseColor("#F2F2F2"));
            if (productSection != null) productSection.setBackgroundColor(Color.parseColor("#F9F9F9"));
            if (btnShopAll != null) {
                btnShopAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.BLACK));
                btnShopAll.setText(R.string.btn_shop_monochrome);
            }
        } else if (titleUpper.contains("AUTUMN") || titleUpper.contains("WINTER")) {
            // COLLECTION 2: THE AUTUMN COLLECTION - Warm Earthy
            tvHeadline.setText(R.string.col_autumn_headline);
            tvSubHeadline.setText(R.string.col_autumn_subheadline);
            ImageUtils.setSafeImage(ivHeroLeft, R.mipmap.atumncollection1);
            ImageUtils.setSafeImage(ivHeroRight, R.mipmap.atumncollection2);
            ImageUtils.setSafeImage(ivSectionModel, R.mipmap.model1);
            tvWhyTitle.setText(R.string.col_autumn_why_title);
            tvWhyDesc.setText(R.string.col_autumn_why_desc);
            
            if (heroSection != null) heroSection.setBackgroundColor(Color.parseColor("#FAF3E0"));
            if (productSection != null) productSection.setBackgroundColor(Color.parseColor("#FFFBF0"));
            if (btnShopAll != null) {
                btnShopAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#810100")));
                btnShopAll.setText(R.string.btn_shop_autumn);
            }
        } else if (titleUpper.contains("ESSENTIALS")) {
            tvHeadline.setText("THE ESSENTIALS");
            tvSubHeadline.setText("CURATED PIECES FOR EVERY DAY");
            ImageUtils.setSafeImage(ivHeroLeft, R.mipmap.model2);
            ImageUtils.setSafeImage(ivHeroRight, R.mipmap.atumncollection1);
            ImageUtils.setSafeImage(ivSectionModel, R.mipmap.jacket3);
            tvWhyTitle.setText("TIMELESS DESIGN");
            tvWhyDesc.setText("Built for longevity and versatility in any wardrobe.");
            
            if (heroSection != null) heroSection.setBackgroundColor(Color.parseColor("#F0EDE3"));
            if (productSection != null) productSection.setBackgroundColor(Color.parseColor("#F9F8F4"));
            if (btnShopAll != null) {
                btnShopAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4A4A4A")));
                btnShopAll.setText("SHOP ESSENTIALS");
            }
        } else if (titleUpper.contains("URBAN ARCHIVE")) {
            tvHeadline.setText("URBAN ARCHIVE");
            tvSubHeadline.setText("LIMITED DROP: STREET ARCHITECTURE");
            ImageUtils.setSafeImage(ivHeroLeft, R.mipmap.banner2);
            ImageUtils.setSafeImage(ivHeroRight, R.mipmap.model1);
            ImageUtils.setSafeImage(ivSectionModel, R.mipmap.pant2);
            tvWhyTitle.setText("STREET HERITAGE");
            tvWhyDesc.setText("Exploring the intersection of function and form.");
            
            if (heroSection != null) heroSection.setBackgroundColor(Color.parseColor("#E5E5E5"));
            if (productSection != null) productSection.setBackgroundColor(Color.parseColor("#EFEFEF"));
            if (btnShopAll != null) {
                btnShopAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1A1A1A")));
                btnShopAll.setText("EXPLORE DROP");
            }
        } else {
            // COLLECTION 3: THE SILK STORY - Heritage Beige
            tvHeadline.setText(R.string.col_silk_headline);
            tvSubHeadline.setText(R.string.col_silk_subheadline);
            ImageUtils.setSafeImage(ivHeroLeft, R.mipmap.banner1);
            ImageUtils.setSafeImage(ivHeroRight, R.mipmap.banner2);
            ImageUtils.setSafeImage(ivSectionModel, R.mipmap.model2);
            tvWhyTitle.setText(R.string.col_silk_why_title);
            tvWhyDesc.setText(R.string.col_silk_why_desc);
            
            if (heroSection != null) heroSection.setBackgroundColor(Color.parseColor("#EDEBDD"));
            if (productSection != null) productSection.setBackgroundColor(Color.parseColor("#F5F4EE"));
            if (btnShopAll != null) {
                btnShopAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#BC8F8F")));
                btnShopAll.setText(R.string.btn_shop_silk);
            }
        }

        productAdapter.setTextColor(Color.BLACK);
        loadMockProducts(titleUpper);
        applyAnimations();
    }

    private void applyAnimations() {
        // Use getDecorView().post() to ensure layout is ready
        getWindow().getDecorView().post(() -> {
            long duration = 700; // 700ms for a more premium, relaxed feel
            // Quintic Out: ultra-smooth deceleration (0.22, 1, 0.36, 1)
            android.view.animation.Interpolator interpolator = new android.view.animation.PathInterpolator(0.22f, 1f, 0.36f, 1f);
            
            // Staggered appearance with 120ms gaps for a more rhythmic flow
            animateView(ivHeroLeft, 50, duration, interpolator);
            animateView(ivHeroRight, 170, duration, interpolator);
            animateView(tvHeadline, 290, duration, interpolator);
            animateView(tvSubHeadline, 410, duration, interpolator);
            animateView(findViewById(R.id.btnShopAll), 530, duration, interpolator);
            animateView(rvCollectionProducts, 650, duration, interpolator);
        });
    }

    private void animateView(View view, long delay, long duration, android.view.animation.Interpolator interpolator) {
        if (view == null) return;
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(duration)
                .setStartDelay(delay)
                .setInterpolator(interpolator)
                .withLayer() 
                .withEndAction(() -> view.setLayerType(View.LAYER_TYPE_NONE, null))
                .start();
    }

    private void loadMockProducts(String titleUpper) {
        productList.clear();
        if (titleUpper.contains("MONOCHROME")) {
            productList.add(new Product(getString(R.string.prod_noir_coat), getString(R.string.price_3450), R.mipmap.model2, "Monochrome"));
            productList.add(new Product(getString(R.string.prod_city_trousers), getString(R.string.price_1150), R.mipmap.pant1, "Monochrome"));
            productList.add(new Product(getString(R.string.prod_min_knit), getString(R.string.price_950), R.mipmap.jacket1, "Monochrome"));
            productList.add(new Product(getString(R.string.prod_archive_tote), getString(R.string.price_1800), R.mipmap.banner2, "Monochrome"));
        } else if (titleUpper.contains("AUTUMN") || titleUpper.contains("WINTER")) {
            productList.add(new Product(getString(R.string.prod_wool_scarf), getString(R.string.price_550), R.mipmap.atumncollection1, "Autumn"));
            productList.add(new Product(getString(R.string.prod_double_blazer), getString(R.string.price_2950), R.mipmap.atumncollection2, "Autumn"));
            productList.add(new Product(getString(R.string.prod_ankle_boots), getString(R.string.price_2250), R.mipmap.model1, "Autumn"));
            productList.add(new Product(getString(R.string.prod_autumn_trench), getString(R.string.price_4200), R.mipmap.atumncollection1, "Autumn"));
        } else if (titleUpper.contains("ESSENTIALS")) {
            productList.add(new Product("Modern T-Shirt", "450.000 ₫", R.mipmap.tshirt1, "Essentials"));
            productList.add(new Product("Raw Denim", "1.200.000 ₫", R.mipmap.pant2, "Essentials"));
            productList.add(new Product("Structured Blazer", "2.500.000 ₫", R.mipmap.jacket3, "Essentials"));
            productList.add(new Product("Canvas Tote", "350.000 ₫", R.mipmap.banner3, "Essentials"));
        } else if (titleUpper.contains("URBAN ARCHIVE")) {
            productList.add(new Product("Tech Parka", "3.200.000 ₫", R.mipmap.jacket2, "Archive"));
            productList.add(new Product("Cargo Trousers", "1.100.000 ₫", R.mipmap.pant2, "Archive"));
            productList.add(new Product("Beanie", "250.000 ₫", R.mipmap.model1, "Archive"));
            productList.add(new Product("Crossbody Bag", "850.000 ₫", R.mipmap.banner2, "Archive"));
        } else {
            productList.add(new Product(getString(R.string.prod_silk_gown), getString(R.string.price_2850), R.mipmap.model2, "Silk"));
            productList.add(new Product(getString(R.string.prod_flowing_pants), getString(R.string.price_1250), R.mipmap.banner2, "Silk"));
            productList.add(new Product(getString(R.string.prod_min_top), getString(R.string.price_850), R.mipmap.banner1, "Silk"));
            productList.add(new Product(getString(R.string.prod_silk_blazer), getString(R.string.price_2100), R.mipmap.model2, "Silk"));
        }
        productAdapter.notifyDataSetChanged();
    }
}

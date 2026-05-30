package com.example.saive.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.saive.R;
import com.example.saive.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    private static final String LANG_PREFS = "language_prefs";
    private static final String LANG_KEY = "selected_language";

    private View heroTextContainer, originSection, materialSection;
    private View tvHeroTitle, tvHeroSubtitle, tvOriginYear, tvOriginText, tvMaterialTitle, svMaterials;
    private TextView tvCartBadge;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(LANG_PREFS, MODE_PRIVATE);
        String lang = prefs.getString(LANG_KEY, "en");
        java.util.Locale locale = new java.util.Locale(lang);
        java.util.Locale.setDefault(locale);
        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);
        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);

        initViews();
        setupCartBadge();

        View rootLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            View searchContainer = findViewById(R.id.searchContainer);
            if (searchContainer != null) {
                int paddingHorizontal = (int) (24 * getResources().getDisplayMetrics().density);
                int paddingVertical = (int) (12 * getResources().getDisplayMetrics().density);
                searchContainer.setPadding(paddingHorizontal,
                        systemBars.top + paddingVertical,
                        paddingHorizontal,
                        paddingVertical);
                searchContainer.bringToFront();
            }

            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        applyAnimations();
    }

    private void initViews() {
        heroTextContainer = findViewById(R.id.heroTextContainer);
        tvHeroTitle = findViewById(R.id.tvHeroTitle);
        tvHeroSubtitle = findViewById(R.id.tvHeroSubtitle);
        originSection = findViewById(R.id.originSection);
        tvOriginYear = findViewById(R.id.tvOriginYear);
        tvOriginText = findViewById(R.id.tvOriginText);
        materialSection = findViewById(R.id.materialSection);
        tvMaterialTitle = findViewById(R.id.tvMaterialTitle);
        svMaterials = findViewById(R.id.svMaterials);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                finish();
            });
        }

        View btnCart = findViewById(R.id.btnCart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(AboutActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        View searchContainer = findViewById(R.id.searchContainer);
        if (searchContainer != null) {
            searchContainer.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(AboutActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Initial states for animation
        View[] animatedViews = {tvHeroTitle, tvHeroSubtitle, tvOriginYear, tvOriginText, tvMaterialTitle, svMaterials};
        for (View v : animatedViews) {
            if (v != null) {
                v.setAlpha(0f);
                v.setTranslationY(40f);
            }
        }
    }

    private void applyAnimations() {
        getWindow().getDecorView().post(() -> {
            long duration = 800;
            android.view.animation.Interpolator interpolator = new android.view.animation.PathInterpolator(0.22f, 1f, 0.36f, 1f);

            animateView(tvHeroTitle, 100, duration, interpolator);
            animateView(tvHeroSubtitle, 250, duration, interpolator);
            animateView(tvOriginYear, 400, duration, interpolator);
            animateView(tvOriginText, 550, duration, interpolator);
            animateView(tvMaterialTitle, 700, duration, interpolator);
            animateView(svMaterials, 850, duration, interpolator);
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

    private void setupCartBadge() {
        tvCartBadge = findViewById(R.id.tvCartBadge);
        updateCartBadge();
        com.example.saive.utils.CartManager.getInstance(this).addListener(this::updateCartBadge);
    }

    private void updateCartBadge() {
        if (tvCartBadge == null) return;
        int count = com.example.saive.utils.CartManager.getInstance(this).getItemCount();
        if (count > 0) {
            tvCartBadge.setText(String.valueOf(count));
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() -> {
                tvCartBadge.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
            }).start();
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
}
package com.example.saive.ui;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.HapticFeedbackConstants;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.PopupMenu;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.saive.adapters.BottomSheetOptionAdapter;
import java.util.Arrays;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.saive.models.Notification;
import com.example.saive.adapters.NotificationAdapter;
import com.example.saive.R;
import com.example.saive.adapters.BannerAdapter;
import com.example.saive.adapters.CategoryAdapter;
import com.example.saive.adapters.EditorialCardAdapter;
import com.example.saive.adapters.FavoriteAdapter;
import com.example.saive.adapters.FlashProductAdapter;
import com.example.saive.adapters.ProductAdapter;
import com.example.saive.adapters.ProductGridAdapter;
import com.example.saive.models.Category;
import com.example.saive.models.EditorialCard;
import com.example.saive.models.Product;
import com.example.saive.base.BaseActivity;
import com.example.saive.utils.CartManager;

import com.example.saive.utils.FavoriteManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ViewPager2 viewPager, bannerViewPager, vpWardrobeBanner;
    private LinearLayout dotIndicatorWardrobe;
    private RecyclerView rvFlashSale, rvNotifications, rvCategories, rvWardrobe;
    private List<Product> productList, flashProductList, wardrobeProductList, fullWardrobeList;
    private List<Category> categoryList;
    private ProductGridAdapter wardrobeAdapter;
    private CategoryAdapter wardrobeCategoryAdapter;
    private List<Integer> bannerList;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private View notificationBadge;
    private View homeScroll, notificationsContainer, wardrobeContainer, favoritesContainer, flashSaleContainer;
    private View emptyStateWardrobe;
    private RecyclerView rvFavorites;
    private TextView tvFavoritesCount;
    private View emptyStateFavorites;
    private FavoriteAdapter favoriteAdapter;
    private List<Product> favoritesList;
    private TextView tvCartBadge;
    private TextView tvHomeHour, tvHomeMinute, tvHomeSecond;
    private CountDownTimer homeCountDownTimer;
    private static final String PREFS_NAME = "notification_prefs";
    private static final String LANG_PREFS = "language_prefs";
    private static final String LANG_KEY = "selected_language";
    private String currentCategory = "All";
    private String currentSortCriteria = null;
    private String currentNotificationSortCriteria = null;

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkIntent(intent);
    }

    private void checkIntent(Intent intent) {
        if (intent == null) return;
        
        boolean hasSectionExtra = intent.getBooleanExtra("SHOW_NOTIFICATIONS", false) ||
                intent.getBooleanExtra("SHOW_WARDROBE", false) ||
                intent.getBooleanExtra("SHOW_FAVORITES", false) ||
                intent.getBooleanExtra("SHOW_HOME", false);

        // Add a small delay to ensure UI is ready and transitions are smoother
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (hasSectionExtra) {
                // Đảm bảo BottomNav và Center Button luôn hiển thị khi điều hướng từ Activity khác về
                View bottomNav = findViewById(R.id.bottomNav);
                if (bottomNav != null) {
                    bottomNav.setAlpha(1f);
                    bottomNav.setTranslationY(0f);
                    bottomNav.setVisibility(View.VISIBLE);
                }
                View centerFab = findViewById(R.id.centerActionButton);
                if (centerFab != null) {
                    centerFab.setAlpha(1f);
                    centerFab.setVisibility(View.VISIBLE);
                    if (centerFab.getScaleX() <= 0.1f) {
                        centerFab.setScaleX(1f);
                        centerFab.setScaleY(1f);
                    }
                }
            }

            if (intent.getBooleanExtra("SHOW_NOTIFICATIONS", false)) {
                if (notificationsContainer != null) {
                    showView(notificationsContainer);
                    View navNotify = findViewById(R.id.navNotify);
                    if (navNotify != null) animateNavIcon(navNotify);
                }
            } else if (intent.getBooleanExtra("SHOW_WARDROBE", false)) {
                if (wardrobeContainer != null) {
                    showView(wardrobeContainer);
                    View navWardrobe = findViewById(R.id.navWardrobe);
                    if (navWardrobe != null) animateNavIcon(navWardrobe);
                }
            } else if (intent.getBooleanExtra("SHOW_FAVORITES", false)) {
                if (favoritesContainer != null) {
                    showView(favoritesContainer);
                    View navFavorite = findViewById(R.id.navFavorite);
                    if (navFavorite != null) animateNavIcon(navFavorite);
                }
            } else if (intent.getBooleanExtra("SHOW_HOME", false)) {
                showView(homeScroll);
                View centerFab = findViewById(R.id.centerActionButton);
                if (centerFab != null) animateNavIcon(centerFab);
            }
        }, 150);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // Thêm hiệu ứng mượt mà khi tắt Splash Screen
        splashScreen.setOnExitAnimationListener(splashScreenView -> {
            final ObjectAnimator fadeOut = ObjectAnimator.ofFloat(
                    splashScreenView.getView(),
                    View.ALPHA,
                    1f,
                    0f
            );
            fadeOut.setInterpolator(new AnticipateInterpolator());
            fadeOut.setDuration(500L);

            fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    splashScreenView.remove();
                }
            });

            fadeOut.start();
        });

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        View rootLayout = findViewById(R.id.rootLayout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            // Push search container down by status bar height
            View searchBar = findViewById(R.id.searchBarWrapper);
            if (searchBar != null) {
                int paddingHorizontal = (int) (24 * getResources().getDisplayMetrics().density);
                int paddingVertical = (int) (12 * getResources().getDisplayMetrics().density);
                searchBar.setPadding(paddingHorizontal, 
                        systemBars.top + paddingVertical,
                        paddingHorizontal, 
                        paddingVertical);
                
                // Ensure search bar is visible on top of everything
                searchBar.bringToFront();
            }

            // Push bottom nav up by navigation bar height
            View bottomNav = findViewById(R.id.bottomNav);
            if (bottomNav != null) {
                bottomNav.setPadding(0, 0, 0, systemBars.bottom);
            }

            return insets;
        });

        initViews();
        setupBannerViewPager();
        setupFlashSale();
        setupWardrobeCategories();
        setupWardrobe();
        setupWardrobeBanners();
        setupViewPager();
        setupNotifications();
        setupFavorites();
        setupNavigation();
        setupCartBadge();
        setupHomeTimer();
        checkIntent(getIntent());

        // Bắt đầu hiệu ứng vào cho UI chính
        startEntryAnimations();
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
            
            // Hiệu ứng nảy nhẹ khi số lượng thay đổi
            tvCartBadge.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() -> {
                tvCartBadge.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
            }).start();
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void startEntryAnimations() {
        View searchBar = findViewById(R.id.searchBarWrapper);
        View mainContent = findViewById(R.id.homeScroll);
        View bottomNav = findViewById(R.id.bottomNav);
        View centerFab = findViewById(R.id.centerActionButton);

        // 1. Thiết lập trạng thái ẩn ban đầu
        if (searchBar != null) {
            searchBar.setAlpha(0f);
            searchBar.setTranslationY(-100f);
        }
        if (mainContent != null) {
            mainContent.setAlpha(0f);
            mainContent.setTranslationY(200f);
        }
        if (bottomNav != null) {
            bottomNav.setAlpha(0f);
            bottomNav.setTranslationY(100f);
        }
        if (centerFab != null) {
            centerFab.setScaleX(0f);
            centerFab.setScaleY(0f);
        }

        // 2. Chạy chuỗi hiệu ứng (Staggered Animations)
        
        // Thanh Search trượt xuống
        if (searchBar != null) {
            searchBar.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(800)
                    .setInterpolator(new DecelerateInterpolator())
                    .setStartDelay(300)
                    .start();
        }

        // Nội dung chính trượt lên chậm rãi
        if (mainContent != null) {
            mainContent.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(1200)
                    .setInterpolator(new DecelerateInterpolator())
                    .setStartDelay(500)
                    .start();
        }

        // Thanh điều hướng và nút trung tâm
        if (bottomNav != null) {
            bottomNav.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(800)
                    .setStartDelay(800)
                    .start();
        }

        if (centerFab != null) {
            centerFab.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(600)
                    .setInterpolator(new OvershootInterpolator())
                    .setStartDelay(1100)
                    .withEndAction(() -> {
                        centerFab.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
                    })
                    .start();
        }
    }

    private void setupWardrobeBanners() {
        vpWardrobeBanner = findViewById(R.id.vpWardrobeBanner);
        dotIndicatorWardrobe = findViewById(R.id.dotIndicatorWardrobe);
        
        if (vpWardrobeBanner == null || dotIndicatorWardrobe == null) return;

        List<com.example.saive.models.WardrobeBanner> banners = new ArrayList<>();
        banners.add(new com.example.saive.models.WardrobeBanner(
                getString(R.string.label_autumn_winter),
                getString(R.string.label_new_collection),
                getString(R.string.label_shop_now),
                R.mipmap.atumncollection1));
        banners.add(new com.example.saive.models.WardrobeBanner(
                "CURATED STYLE",
                "ESSENTIALS",
                "EXPLORE",
                R.mipmap.atumncollection2));
        banners.add(new com.example.saive.models.WardrobeBanner(
                "LIMITED DROP",
                "URBAN ARCHIVE",
                "DISCOVER",
                R.mipmap.banner2));

        com.example.saive.adapters.WardrobeBannerAdapter adapter = new com.example.saive.adapters.WardrobeBannerAdapter(banners, banner -> {
            Intent intent;
            if (banner.getTitle().equals("ESSENTIALS")) {
                intent = new Intent(MainActivity.this, CollectionDetailActivity.class);
                intent.putExtra("COLLECTION_TITLE", "ESSENTIALS");
            } else if (banner.getTitle().equals("URBAN ARCHIVE")) {
                intent = new Intent(MainActivity.this, CollectionDetailActivity.class);
                intent.putExtra("COLLECTION_TITLE", "URBAN ARCHIVE");
            } else {
                // Default or "NEW COLLECTION" -> Collections List
                intent = new Intent(MainActivity.this, CollectionsListActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        vpWardrobeBanner.setAdapter(adapter);

        setupDotIndicator(banners.size());

        vpWardrobeBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
            }
        });
    }

    private void setupDotIndicator(int count) {
        dotIndicatorWardrobe.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (8 * getResources().getDisplayMetrics().density),
                    (int) (8 * getResources().getDisplayMetrics().density)
            );
            params.setMargins(
                    (int) (4 * getResources().getDisplayMetrics().density),
                    0,
                    (int) (4 * getResources().getDisplayMetrics().density),
                    0
            );
            dot.setLayoutParams(params);
            dot.setImageResource(R.drawable.dot_indicator);
            dot.setSelected(i == 0);
            dotIndicatorWardrobe.addView(dot);
        }
    }

    private void updateDots(int position) {
        for (int i = 0; i < dotIndicatorWardrobe.getChildCount(); i++) {
            dotIndicatorWardrobe.getChildAt(i).setSelected(i == position);
        }
    }

    private void setupNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navNotify = findViewById(R.id.navNotify);
        View navWardrobe = findViewById(R.id.navWardrobe);
        View navFavorite = findViewById(R.id.navFavorite);
        View navProfile = findViewById(R.id.navProfile);
        View centerActionButton = findViewById(R.id.centerActionButton);
        View searchContainer = findViewById(R.id.searchContainer);
        View btnCart = findViewById(R.id.btnCart);

        if (searchContainer != null) {
            searchContainer.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showView(homeScroll);
                animateNavIcon(centerActionButton);
            });
        }

        if (navWardrobe != null) {
            navWardrobe.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showView(wardrobeContainer);
                animateNavIcon(navWardrobe);
            });
        }

        if (navNotify != null) {
            navNotify.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showView(notificationsContainer);
                animateNavIcon(navNotify);
            });
        }

        if (navFavorite != null) {
            navFavorite.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showView(favoritesContainer);
                animateNavIcon(navFavorite);
            });
        }

        // The Monochrome Series clicks
        View cardPrimary = findViewById(R.id.cardEditorialPrimary);
        View cardSecondary = findViewById(R.id.cardEditorialSecondary);
        View btnExplore = findViewById(R.id.btnExploreLookbook);

        View.OnClickListener monochromeClickListener = v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            Intent intent = new Intent(MainActivity.this, CollectionsListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        };

        if (cardPrimary != null) cardPrimary.setOnClickListener(monochromeClickListener);
        if (cardSecondary != null) cardSecondary.setOnClickListener(monochromeClickListener);
        if (btnExplore != null) btnExplore.setOnClickListener(monochromeClickListener);

        View tvMaterialTitle = findViewById(R.id.tvMaterialTitle);
        View ivMaterialStory = findViewById(R.id.ivMaterialStory);
        if (ivMaterialStory != null) {
            ivMaterialStory.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(MainActivity.this, CollectionDetailActivity.class);
                intent.putExtra("COLLECTION_TITLE", getString(R.string.material_story_title));
                startActivity(intent);
            });
        }
        
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                navigateToProfile();
            });
        }

        // Sort button
        View btnSortWardrobe = findViewById(R.id.btnSortWardrobe);
        if (btnSortWardrobe != null) {
            btnSortWardrobe.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showSortPopup(v);
            });
        }

        // Notification Settings button
        View btnNotificationSettings = findViewById(R.id.btnNotificationSettings);
        if (btnNotificationSettings != null) {
            btnNotificationSettings.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showNotificationSortPopup(v);
            });
        }

        // Home button (center) also toggles home view
        if (centerActionButton != null) {
            centerActionButton.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showView(homeScroll);
                animateNavIcon(centerActionButton);
            });
        }

        // Set initial active state
        updateBottomNavStyle(R.id.centerActionButton);

        View tvViewFullCuration = findViewById(R.id.tvViewFullCuration);
        if (tvViewFullCuration != null) {
            tvViewFullCuration.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showView(wardrobeContainer);
                animateNavIcon(navWardrobe);
            });
        }

        // Home Navigation Links
        View tvNavShop = findViewById(R.id.tvNavShop);
        View tvNavArchive = findViewById(R.id.tvNavArchive);
        View tvNavAbout = findViewById(R.id.tvNavAbout);

        if (tvNavShop != null) {
            tvNavShop.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showView(wardrobeContainer);
                animateNavIcon(navWardrobe);
            });
        }

        if (tvNavArchive != null) {
            tvNavArchive.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(MainActivity.this, CollectionsListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (tvNavAbout != null) {
            tvNavAbout.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // New Collection Banner click
        View bannerNewCollection = findViewById(R.id.wardrobeBannerContainer);
        if (bannerNewCollection != null) {
            bannerNewCollection.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(MainActivity.this, CollectionsListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        // Flash Sale Container Click
        if (flashSaleContainer != null) {
            flashSaleContainer.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(MainActivity.this, FlashSaleActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    private void showView(View toShow) {
        if (toShow == null) return;

        int activeId = -1;
        if (toShow == homeScroll) activeId = R.id.centerActionButton;
        else if (toShow == notificationsContainer) activeId = R.id.navNotify;
        else if (toShow == wardrobeContainer) activeId = R.id.navWardrobe;
        else if (toShow == favoritesContainer) activeId = R.id.navFavorite;

        updateBottomNavStyle(activeId);

        if (toShow.getVisibility() == View.VISIBLE) return;

        View[] views = {homeScroll, notificationsContainer, wardrobeContainer, favoritesContainer};
        View searchBar = findViewById(R.id.searchBarWrapper);

        for (View v : views) {
            if (v == null) continue;
            if (v == toShow) {
                // Reset and prepare toShow
                v.setAlpha(0f);
                v.setTranslationY(40f); // Subtle slide up
                v.setVisibility(View.VISIBLE);

                // Animate toShow: Smooth slide up and fade in
                v.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(600)
                        .setInterpolator(new DecelerateInterpolator(1.5f))
                        .withEndAction(() -> {
                            // Ensure search bar stays on top after container switch
                            if (searchBar != null) {
                                searchBar.bringToFront();
                            }
                        })
                        .start();
            } else if (v.getVisibility() == View.VISIBLE) {
                // Animate toHide: Subtle fade out
                v.animate()
                        .alpha(0f)
                        .setDuration(400)
                        .withEndAction(() -> {
                            v.setVisibility(View.GONE);
                        })
                        .start();
            }
        }
    }

    private void setupFavorites() {
        if (rvFavorites != null) {
            rvFavorites.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
            
            FavoriteManager favoriteManager = FavoriteManager.getInstance(this);
            favoritesList = favoriteManager.getFavoriteItems();
            
            favoriteAdapter = new FavoriteAdapter(favoritesList, position -> {
                Product product = favoritesList.get(position);
                favoriteManager.removeFavorite(product);
                // The listener will trigger update if we add it, but for now we can update manually or rely on manager
            });
            
            favoriteManager.addListener(() -> {
                favoritesList.clear();
                favoritesList.addAll(favoriteManager.getFavoriteItems());
                favoriteAdapter.notifyDataSetChanged();
                updateFavoritesUI();
            });

            rvFavorites.setAdapter(favoriteAdapter);
            updateFavoritesUI();
        }
    }

    private void updateFavoritesUI() {
        if (tvFavoritesCount != null) {
            tvFavoritesCount.setText(getString(R.string.favorites_items_count, favoritesList.size()));
        }
        
        if (favoritesList.isEmpty()) {
            emptyStateFavorites.setVisibility(View.VISIBLE);
            rvFavorites.setVisibility(View.GONE);
        } else {
            emptyStateFavorites.setVisibility(View.GONE);
            rvFavorites.setVisibility(View.VISIBLE);
        }
    }

    private void animateNavIcon(View view) {
        if (view == null) return;
        // Đảm bảo view hiển thị trước khi chạy hiệu ứng (fix lỗi Shared Element Transition)
        view.setVisibility(View.VISIBLE);
        view.setAlpha(1.0f);

        view.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(300)
                            .start();
                })
                .start();
    }

    private void setupWardrobe() {
        rvWardrobe = findViewById(R.id.rvWardrobe);
        fullWardrobeList = new ArrayList<>();
        wardrobeProductList = new ArrayList<>();
        
        // Organized Wardrobe Data by Categories with new product images
        fullWardrobeList.add(new Product("Structured Wool Coat", "1.200.000 ₫", R.mipmap.jacket1, getString(R.string.cat_jacket)));
        fullWardrobeList.add(new Product("Archive Parka", "2.100.000 ₫", R.mipmap.jacket2, getString(R.string.cat_jacket)));
        fullWardrobeList.add(new Product("Minimalist Bomber", "1.500.000 ₫", R.mipmap.jacket3, getString(R.string.cat_jacket)));

        fullWardrobeList.add(new Product("Classic Cotton T-Shirt", "350.000 ₫", R.mipmap.tshirt1, getString(R.string.cat_tshirt)));
        fullWardrobeList.add(new Product("Oversized Tee", "450.000 ₫", R.mipmap.tshirt2, getString(R.string.cat_tshirt)));
        fullWardrobeList.add(new Product("Graphic Art Shirt", "550.000 ₫", R.mipmap.tshirt3, getString(R.string.cat_tshirt)));

        fullWardrobeList.add(new Product("Straight Fit Jeans", "850.000 ₫", R.mipmap.pant1, getString(R.string.cat_jeans)));
        fullWardrobeList.add(new Product("Raw Denim Trousers", "1.100.000 ₫", R.mipmap.pant2, getString(R.string.cat_jeans)));
        fullWardrobeList.add(new Product("Slim Tailored Pants", "950.000 ₫", R.mipmap.pant3, getString(R.string.cat_jeans)));

        fullWardrobeList.add(new Product("Signature Aviators", "450.000 ₫", R.mipmap.sunglass1, getString(R.string.cat_sunglasses)));
        fullWardrobeList.add(new Product("Modern Square Frames", "400.000 ₫", R.mipmap.sunglass2, getString(R.string.cat_sunglasses)));
        fullWardrobeList.add(new Product("Vintage Round Glasses", "500.000 ₫", R.mipmap.sunglass3, getString(R.string.cat_sunglasses)));

        wardrobeProductList.addAll(fullWardrobeList);

        wardrobeAdapter = new ProductGridAdapter(wardrobeProductList);
        rvWardrobe.setLayoutManager(new GridLayoutManager(this, 2));
        rvWardrobe.setAdapter(wardrobeAdapter);
    }

    private void setupNotifications() {
        rvNotifications = findViewById(R.id.rvNotifications);
        View emptyState = findViewById(R.id.emptyStateNotify);
        
        notificationList = new ArrayList<>();
        
        // Mock Data based on the prompt
        notificationList.add(new Notification(
                "drop_1",
                getString(R.string.notify_drop_title),
                getString(R.string.notify_drop_desc),
                getString(R.string.notify_drop_action),
                "2h",
                R.mipmap.atumncollection1,
                Color.parseColor("#F0EDE3"),
                false,
                ContextCompat.getColor(this, R.color.colorMaroon),
                Notification.Type.DROP,
                System.currentTimeMillis() - 2 * 3600 * 1000 // 2h ago
        ));

        notificationList.add(new Notification(
                "order_1",
                getString(R.string.notify_order_title),
                getString(R.string.notify_order_desc),
                getString(R.string.notify_order_action),
                "1d",
                R.mipmap.atumncollection2,
                Color.parseColor("#FAF8F3"),
                false,
                ContextCompat.getColor(this, R.color.colorMaroon),
                Notification.Type.ORDER,
                System.currentTimeMillis() - 24 * 3600 * 1000 // 1d ago
        ));

        notificationList.add(new Notification(
                "capsule_1",
                getString(R.string.notify_capsule_title),
                getString(R.string.notify_capsule_desc),
                getString(R.string.notify_capsule_action),
                "2d",
                R.mipmap.saive_logo,
                Color.parseColor("#F5EFE6"),
                true,
                ContextCompat.getColor(this, R.color.colorSand),
                Notification.Type.CAPSULE,
                System.currentTimeMillis() - 2 * 24 * 3600 * 1000 // 2d ago
        ));

        notificationList.add(new Notification(
                "reminder_1",
                getString(R.string.notify_reminder_title),
                getString(R.string.notify_reminder_desc),
                getString(R.string.notify_reminder_action),
                "1w",
                R.mipmap.saive_logo,
                Color.parseColor("#EDEBDD"),
                true,
                ContextCompat.getColor(this, R.color.colorSand),
                Notification.Type.REMINDER,
                System.currentTimeMillis() - 7 * 24 * 3600 * 1000 // 1w ago
        ));

        if (notificationList.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            
            notificationAdapter = new NotificationAdapter(notificationList, this::updateNotificationBadge);
            rvNotifications.setLayoutManager(new LinearLayoutManager(this));
            rvNotifications.setAdapter(notificationAdapter);
        }
    }

    private void setupWardrobeCategories() {
        rvCategories = findViewById(R.id.rvCategories);
        if (rvCategories == null) return;

        currentCategory = getString(R.string.cat_all);
        categoryList = new ArrayList<>();
        categoryList.add(new Category(getString(R.string.cat_all), R.drawable.ic_all));
        categoryList.add(new Category(getString(R.string.cat_tshirt), R.mipmap.tshirticon));
        categoryList.add(new Category(getString(R.string.cat_jeans), R.mipmap.panticon));
        categoryList.add(new Category(getString(R.string.cat_jacket), R.mipmap.jacketicon));
        categoryList.add(new Category(getString(R.string.cat_sunglasses), R.mipmap.sunglassicon));

        wardrobeCategoryAdapter = new CategoryAdapter(categoryList, category -> {
            filterWardrobe(category.getName());
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(wardrobeCategoryAdapter);
    }

    private void filterWardrobe(String categoryName) {
        currentCategory = categoryName;
        if (categoryName.equals(getString(R.string.cat_all))) {
            wardrobeProductList.clear();
            wardrobeProductList.addAll(fullWardrobeList);
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product p : fullWardrobeList) {
                if (p.getCategory() != null && p.getCategory().equalsIgnoreCase(categoryName)) {
                    filtered.add(p);
                }
            }
            wardrobeProductList.clear();
            wardrobeProductList.addAll(filtered);
        }
        
        if (emptyStateWardrobe != null) {
            if (wardrobeProductList.isEmpty()) {
                emptyStateWardrobe.setVisibility(View.VISIBLE);
                rvWardrobe.setVisibility(View.GONE);
            } else {
                emptyStateWardrobe.setVisibility(View.GONE);
                rvWardrobe.setVisibility(View.VISIBLE);
            }
        }

        if (wardrobeAdapter != null) {
            wardrobeAdapter.notifyDataSetChanged();
        }
    }

    private void setupProductGrid() {
        // Method removed as product grid is no longer on Home
    }

    private void filterProducts(String category) {
        // Logic moved to wardrobe filter if needed
    }

    private void setupFlashSale() {
        rvFlashSale = findViewById(R.id.rvFlashSale);
        flashProductList = new ArrayList<>();
        
        // Thêm các sản phẩm Flash Sale với hình ảnh đa dạng
        flashProductList.add(new Product("Archived Wool Coat", "$320.00", R.mipmap.jacket1, getString(R.string.cat_jacket)));
        flashProductList.add(new Product("Vintage Linen Shirt", "$120.00", R.mipmap.tshirt2, getString(R.string.cat_tshirt)));
        flashProductList.add(new Product("Urban Shades", "$140.00", R.mipmap.sunglass4, getString(R.string.cat_sunglasses)));
        flashProductList.add(new Product("Classic Chinos", "$95.00", R.mipmap.pant2, getString(R.string.cat_jeans)));
        flashProductList.add(new Product("Retro Frames", "$150.00", R.mipmap.sunglass5, getString(R.string.cat_sunglasses)));

        FlashProductAdapter adapter = new FlashProductAdapter(flashProductList);
        // Thiết lập LinearLayoutManager nằm ngang
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvFlashSale.setLayoutManager(layoutManager);
        rvFlashSale.setAdapter(adapter);
        
        // Thêm hiệu ứng mượt mà khi cuộn
        rvFlashSale.setHasFixedSize(true);
        rvFlashSale.setNestedScrollingEnabled(false);
    }

    private void setupBannerViewPager() {
        bannerViewPager = findViewById(R.id.viewPagerBanner);
        bannerList = new ArrayList<>();
        bannerList.add(R.mipmap.banner1);
        bannerList.add(R.mipmap.banner2);
        bannerList.add(R.mipmap.banner3);
        bannerList.add(R.mipmap.model1);
        bannerList.add(R.mipmap.model2);

        BannerAdapter bannerAdapter = new BannerAdapter(bannerList);
        bannerViewPager.setAdapter(bannerAdapter);

        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = bannerViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerList.size();
                bannerViewPager.setCurrentItem(nextItem, true);
                bannerHandler.postDelayed(this, 5000);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
        if (homeCountDownTimer != null) {
            homeCountDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bannerHandler.postDelayed(bannerRunnable, 5000);
        updateNotificationBadge();
        updateCartBadge(); // Cập nhật badge khi quay lại từ màn hình khác
        setupHomeTimer(); // Restart timer to sync or ensure it's running
        
        // Refresh wardrobe adapter when returning to MainActivity
        if (wardrobeAdapter != null) {
            wardrobeAdapter.notifyDataSetChanged();
        }

        updateBottomNavFromVisibleContainer();
    }

    private void updateBottomNavFromVisibleContainer() {
        if (notificationsContainer != null && notificationsContainer.getVisibility() == View.VISIBLE) {
            updateBottomNavStyle(R.id.navNotify);
        } else if (wardrobeContainer != null && wardrobeContainer.getVisibility() == View.VISIBLE) {
            updateBottomNavStyle(R.id.navWardrobe);
        } else if (favoritesContainer != null && favoritesContainer.getVisibility() == View.VISIBLE) {
            updateBottomNavStyle(R.id.navFavorite);
        } else if (homeScroll != null && homeScroll.getVisibility() == View.VISIBLE) {
            updateBottomNavStyle(R.id.centerActionButton);
        }
    }

    private void updateNotificationBadge() {
        if (notificationBadge == null) return;
        
        boolean hasUnread = false;
        if (notificationList != null) {
            android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
            for (Notification n : notificationList) {
                if (!prefs.getBoolean("read_" + n.getId(), n.isRead())) {
                    hasUnread = true;
                    break;
                }
            }
        }
        notificationBadge.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPagerProducts);
        notificationBadge = findViewById(R.id.notificationBadge);
        homeScroll = findViewById(R.id.homeScroll);
        notificationsContainer = findViewById(R.id.notificationsContainer);
        wardrobeContainer = findViewById(R.id.wardrobeContainer);
        favoritesContainer = findViewById(R.id.favoritesContainer);
        flashSaleContainer = findViewById(R.id.flashSaleContainer);
        emptyStateWardrobe = findViewById(R.id.emptyStateWardrobe);
        rvFavorites = findViewById(R.id.rvFavorites);
        tvFavoritesCount = findViewById(R.id.tvFavoritesCount);
        emptyStateFavorites = findViewById(R.id.emptyStateFavorites);
        
        tvHomeHour = findViewById(R.id.tvHomeHour);
        tvHomeMinute = findViewById(R.id.tvHomeMinute);
        tvHomeSecond = findViewById(R.id.tvHomeSecond);

        productList = new ArrayList<>();
        // Organized Home Products
        productList.add(new Product("Structured Wool Coat", "$450.00", R.mipmap.jacket1, getString(R.string.cat_jacket)));
        productList.add(new Product("Linen Overshirt", "$180.00", R.mipmap.tshirt1, getString(R.string.cat_tshirt)));
        productList.add(new Product("Tailored Trousers", "$220.00", R.mipmap.pant1, getString(R.string.cat_jeans)));
        productList.add(new Product("Modern Aviators", "$210.00", R.mipmap.sunglass1, getString(R.string.cat_sunglasses)));
        productList.add(new Product("Archive Jacket", "$520.00", R.mipmap.jacket2, getString(R.string.cat_jacket)));
    }

    private void setupViewPager() {
        ProductAdapter adapter = new ProductAdapter(productList);
        viewPager.setAdapter(adapter);

        // Circular/Carousel Effect
        viewPager.setOffscreenPageLimit(3);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);

        // Standard horizontal carousel padding
        View recyclerView = viewPager.getChildAt(0);
        if (recyclerView instanceof RecyclerView) {
            recyclerView.setPadding(100, 0, 100, 0);
            ((RecyclerView) recyclerView).setClipToPadding(false);
        }

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(24));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float absPos = Math.abs(position);
                
                // Scale effect
                page.setScaleY(0.85f + (1 - absPos) * 0.15f);
                page.setScaleX(0.85f + (1 - absPos) * 0.15f);
                
                // Circular/Curve translation
                // As position goes from -1 to 1, we want the Y to dip in the middle or rise?
                // For a "circular" drag, we can offset Y based on position
                page.setTranslationY(absPos * 100); 
                
                // Rotation for circular feel
                page.setRotation(position * -10f);
                
                page.setAlpha(0.5f + (1 - absPos) * 0.5f);
            }
        });

        viewPager.setPageTransformer(compositePageTransformer);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        setupEditorialStack();
    }

    private void setupEditorialStack() {
        RecyclerView rvEditorial = findViewById(R.id.rvEditorialStack);
        List<EditorialCard> editorialCards = new ArrayList<>();
        
        editorialCards.add(new EditorialCard(
                getString(R.string.editorial_title_3),
                getString(R.string.editorial_story_3),
                getString(R.string.editorial_material_3),
                R.mipmap.banner3,
                getString(R.string.explore_piece)
        ));
        editorialCards.add(new EditorialCard(
                getString(R.string.editorial_title_2),
                getString(R.string.editorial_story_2),
                getString(R.string.editorial_material_2),
                R.mipmap.model2,
                getString(R.string.explore_piece)
        ));
        editorialCards.add(new EditorialCard(
                getString(R.string.editorial_title_1),
                getString(R.string.editorial_story_1),
                getString(R.string.editorial_material_1),
                R.mipmap.model1,
                getString(R.string.explore_piece)
        ));

        EditorialCardAdapter editorialAdapter = new EditorialCardAdapter(editorialCards, card -> {
            Intent intent;
            if (card.getTitle().equals(getString(R.string.editorial_title_3))) {
                // SAIVE STORY -> About
                intent = new Intent(this, AboutActivity.class);
            } else if (card.getTitle().equals(getString(R.string.editorial_title_2))) {
                // EVERYDAY BEAUTY -> Silk Story
                intent = new Intent(this, CollectionDetailActivity.class);
                intent.putExtra("COLLECTION_TITLE", "THE SILK STORY");
            } else {
                // TIMELESS CRAFT -> Monochrome
                intent = new Intent(this, CollectionDetailActivity.class);
                intent.putExtra("COLLECTION_TITLE", "THE MONOCHROME SERIES");
            }
            startActivity(intent);
            return null;
        });
        rvEditorial.setLayoutManager(new LinearLayoutManager(this));
        rvEditorial.setAdapter(editorialAdapter);
    }

    private void showNotificationSortPopup(View v) {
        List<String> sortOptions = Arrays.asList(
                getString(R.string.sort_latest),
                getString(R.string.sort_oldest)
        );

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView tvSheetTitle = sheetView.findViewById(R.id.tvSheetTitle);
        if (tvSheetTitle != null) {
            tvSheetTitle.setText(getString(R.string.notify_title)); // Or a specific sort string if you have one
        }

        RecyclerView rvOptions = sheetView.findViewById(R.id.rvSheetOptions);
        rvOptions.setLayoutManager(new LinearLayoutManager(this));

        BottomSheetOptionAdapter adapter = new BottomSheetOptionAdapter(sortOptions, currentNotificationSortCriteria, option -> {
            currentNotificationSortCriteria = option;
            sortNotifications(option);
            bottomSheetDialog.dismiss();
        });
        rvOptions.setAdapter(adapter);

        bottomSheetDialog.show();
    }

    private void sortNotifications(String criteria) {
        if (notificationList == null || notificationList.isEmpty()) return;

        java.util.Collections.sort(notificationList, (n1, n2) -> {
            if (criteria.equals(getString(R.string.sort_latest))) {
                return Long.compare(n2.getTimestamp(), n1.getTimestamp());
            } else if (criteria.equals(getString(R.string.sort_oldest))) {
                return Long.compare(n1.getTimestamp(), n2.getTimestamp());
            }
            return 0;
        });

        if (notificationAdapter != null) {
            notificationAdapter.notifyDataSetChanged();
        }
    }

    private void showSortPopup(View v) {
        List<String> sortOptions = Arrays.asList(
                getString(R.string.sort_price_low_high),
                getString(R.string.sort_price_high_low),
                getString(R.string.sort_alphabetical_az),
                getString(R.string.sort_alphabetical_za),
                getString(R.string.sort_latest),
                getString(R.string.sort_oldest)
        );

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        RecyclerView rvOptions = sheetView.findViewById(R.id.rvSheetOptions);
        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        
        BottomSheetOptionAdapter adapter = new BottomSheetOptionAdapter(sortOptions, currentSortCriteria, option -> {
            currentSortCriteria = option;
            sortWardrobe(option);
            bottomSheetDialog.dismiss();
        });
        rvOptions.setAdapter(adapter);

        bottomSheetDialog.show();
    }

    private void sortWardrobe(String criteria) {
        java.util.Collections.sort(wardrobeProductList, (p1, p2) -> {
            if (criteria.equals(getString(R.string.sort_price_low_high))) {
                return Double.compare(parsePrice(p1.getPrice()), parsePrice(p2.getPrice()));
            } else if (criteria.equals(getString(R.string.sort_price_high_low))) {
                return Double.compare(parsePrice(p2.getPrice()), parsePrice(p1.getPrice()));
            } else if (criteria.equals(getString(R.string.sort_alphabetical_az))) {
                return p1.getName().compareToIgnoreCase(p2.getName());
            } else if (criteria.equals(getString(R.string.sort_alphabetical_za))) {
                return p2.getName().compareToIgnoreCase(p1.getName());
            } else if (criteria.equals(getString(R.string.sort_latest))) {
                return Long.compare(p2.getTimestamp(), p1.getTimestamp());
            } else if (criteria.equals(getString(R.string.sort_oldest))) {
                return Long.compare(p1.getTimestamp(), p2.getTimestamp());
            }
            return 0;
        });
        if (wardrobeAdapter != null) {
            wardrobeAdapter.notifyDataSetChanged();
        }
    }

    private void setupHomeTimer() {
        if (homeCountDownTimer != null) {
            homeCountDownTimer.cancel();
        }

        // Mocking endsAt for 24 hours from now to match FlashSaleActivity
        long diff = java.util.concurrent.TimeUnit.HOURS.toMillis(24);

        homeCountDownTimer = new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateHomeTimerUI(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (tvHomeHour != null) tvHomeHour.setText("00");
                if (tvHomeMinute != null) tvHomeMinute.setText("00");
                if (tvHomeSecond != null) tvHomeSecond.setText("00");
            }
        }.start();
    }

    private void updateHomeTimerUI(long millis) {
        long hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        if (tvHomeHour != null) tvHomeHour.setText(String.format(java.util.Locale.getDefault(), "%02d", hours));
        if (tvHomeMinute != null) tvHomeMinute.setText(String.format(java.util.Locale.getDefault(), "%02d", minutes));
        if (tvHomeSecond != null) tvHomeSecond.setText(String.format(java.util.Locale.getDefault(), "%02d", seconds));
    }

    private void updateBottomNavStyle(int activeId) {
        int[] navIds = {R.id.navFavorite, R.id.navWardrobe, R.id.navNotify, R.id.navProfile};
        for (int id : navIds) {
            View navItem = findViewById(id);
            if (navItem != null) {
                float alpha = (id == activeId) ? 1.0f : 0.7f;
                navItem.setAlpha(alpha);

                TextView tv = findTextView(navItem);
                if (tv != null) {
                    tv.setTypeface(null, (id == activeId) ? Typeface.BOLD : Typeface.NORMAL);
                }
            }
        }

        View centerActionButton = findViewById(R.id.centerActionButton);
        if (centerActionButton != null) {
            centerActionButton.setAlpha((activeId == R.id.centerActionButton) ? 1.0f : 0.7f);
        }
    }

    private TextView findTextView(View view) {
        if (view instanceof TextView) {
            return (TextView) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                TextView res = findTextView(group.getChildAt(i));
                if (res != null) return res;
            }
        }
        return null;
    }

    private double parsePrice(String price) {
        if (price == null || price.isEmpty()) return 0;
        try {
            // Remove currency symbols and whitespace
            String cleanPrice = price.replaceAll("[^0-9.,]", "").trim();

            // Check if it's the 1.200.000 format (VN)
            if (cleanPrice.contains(".") && cleanPrice.indexOf(".") != cleanPrice.lastIndexOf(".")) {
                cleanPrice = cleanPrice.replace(".", "");
            }
            // Check if it's 1.200.000,00 format
            else if (cleanPrice.contains(".") && cleanPrice.contains(",")) {
                cleanPrice = cleanPrice.replace(".", "").replace(",", ".");
            }
            // Check if it's 1200000,00 format
            else if (cleanPrice.contains(",") && cleanPrice.length() - cleanPrice.lastIndexOf(",") <= 3) {
                cleanPrice = cleanPrice.replace(",", ".");
            }
            // Check if it's 1,200,000 format
            else if (cleanPrice.contains(",") && cleanPrice.indexOf(",") != cleanPrice.lastIndexOf(",")) {
                cleanPrice = cleanPrice.replace(",", "");
            }

            if (cleanPrice.isEmpty()) return 0;
            return Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            return 0;
        }
    }
}

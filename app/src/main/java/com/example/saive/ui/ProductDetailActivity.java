package com.example.saive.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.saive.utils.FavoriteManager;
import com.example.saive.utils.ToastUtils;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Product;
import com.example.saive.utils.CartManager;
import com.example.saive.utils.PriceFormatter;
import com.example.saive.utils.ImageUtils;

import com.example.saive.adapters.FlashProductAdapter;
import com.example.saive.adapters.ReviewAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import com.example.saive.models.Review;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDetailActivity extends BaseActivity {

    private ImageView ivHero, ivWardrobeIcon, btnBack;
    private ImageButton btnFavorite, btnShare;
    private View btnCart;
    private LottieAnimationView lottieFavorite;
    private TextView tvProductName, tvPrice, tvDescription, tvWardrobeAction, btnWriteReview, btnSeeMore, tvCartBadge, tvQuantity;
    private View btnAddToWardrobe, sizeSelectionContainer, colorSelectionContainer;
    private ImageButton btnDecrease, btnIncrease;
    private RecyclerView rvCompleteLook, rvReviews;
    private List<View> sizeViews, colorViews;
    
    private Product currentProduct;
    private boolean isAddedToWardrobe = false;
    private int selectedQuantity = 1;
    private List<Review> reviewList;
    private ReviewAdapter reviewAdapter;
    private CartManager.OnCartChangeListener cartChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        View rootLayout = findViewById(android.R.id.content);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());

            View searchContainer = findViewById(R.id.searchContainer);
            if (searchContainer != null) {
                int paddingHorizontal = (int) (24 * getResources().getDisplayMetrics().density);
                searchContainer.setPadding(paddingHorizontal,
                        systemBars.top,
                        paddingHorizontal,
                        (int) (12 * getResources().getDisplayMetrics().density));
                searchContainer.bringToFront();
            }

            return insets;
        });

        initViews();
        setupData();
        setupListeners();
        setupReviews();
        setupCompleteTheLook();
        updateWardrobeUI();
        updateCartBadge();
        setupCartObserver();
    }

    private void setupCartObserver() {
        cartChangeListener = this::updateCartBadge;
        CartManager.getInstance(this).addListener(cartChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartChangeListener != null) {
            CartManager.getInstance(this).removeListener(cartChangeListener);
        }
    }

    private void updateCartBadge() {
        int count = CartManager.getInstance(this).getItemCount();
        if (tvCartBadge != null) {
            if (count > 0) {
                tvCartBadge.setText(String.valueOf(count));
                tvCartBadge.setVisibility(View.VISIBLE);
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        }
    }

    private void initViews() {
        ivHero = findViewById(R.id.ivHero);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        lottieFavorite = findViewById(R.id.lottieFavorite);
        btnShare = findViewById(R.id.btnShare);
        btnCart = findViewById(R.id.btnCart);
        tvCartBadge = findViewById(R.id.tvCartBadge);
        
        tvProductName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        btnSeeMore = findViewById(R.id.btnSeeMore);
        btnWriteReview = findViewById(R.id.btnWriteReview);
        
        tvQuantity = findViewById(R.id.tvQuantity);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        
        rvCompleteLook = findViewById(R.id.rvCompleteLook);
        rvReviews = findViewById(R.id.rvReviews);
        
        btnAddToWardrobe = findViewById(R.id.btnAddToWardrobe);
        ivWardrobeIcon = findViewById(R.id.ivWardrobeIcon);
        tvWardrobeAction = findViewById(R.id.tvWardrobeAction);

        sizeSelectionContainer = findViewById(R.id.sizeSelectionContainer);
        colorSelectionContainer = findViewById(R.id.colorSelectionContainer);

        sizeViews = new ArrayList<>();
        sizeViews.add(findViewById(R.id.sizeXS));
        sizeViews.add(findViewById(R.id.sizeS));
        sizeViews.add(findViewById(R.id.sizeM));
        sizeViews.add(findViewById(R.id.sizeL));
        sizeViews.add(findViewById(R.id.sizeXL));

        colorViews = new ArrayList<>();
        colorViews.add(findViewById(R.id.colorBlack));
        colorViews.add(findViewById(R.id.colorTortoise));
        colorViews.add(findViewById(R.id.colorGold));
        colorViews.add(findViewById(R.id.colorSilver));
    }

    private void setupData() {
        currentProduct = (Product) getIntent().getSerializableExtra("PRODUCT");
        if (currentProduct == null) {
            // Check lowercase version if uppercase fails
            currentProduct = (Product) getIntent().getSerializableExtra("product");
        }

        if (currentProduct == null) {
            // Fallback mock if no product passed
            currentProduct = new Product("Amor Mystique", "1.200.000 ₫", R.mipmap.model1, "Perfume");
        }
        
        tvProductName.setText(currentProduct.getName());
        tvPrice.setText(PriceFormatter.formatPrice(currentProduct.getPrice()));
        tvDescription.setText(currentProduct.getDescription());
        ImageUtils.setSafeImage(ivHero, currentProduct.getImageResId());

        // Toggle Size/Color selection based on category
        boolean isGlasses = currentProduct.getCategory() != null && currentProduct.getCategory().toLowerCase().contains("glasses");
        if (sizeSelectionContainer != null) sizeSelectionContainer.setVisibility(isGlasses ? View.GONE : View.VISIBLE);
        if (colorSelectionContainer != null) colorSelectionContainer.setVisibility(isGlasses ? View.VISIBLE : View.GONE);

        // Check if already in wardrobe
        isAddedToWardrobe = CartManager.getInstance(this).getCartItems().contains(currentProduct);
    }

    private void setupReviews() {
        reviewList = new ArrayList<>();
        // Dữ liệu mẫu
        reviewList.add(new Review("Nhi Huynh", 5.0f, "Chất liệu tuyệt vời, mặc rất thoải mái và sang trọng.", "24 Oct 2023", Arrays.asList("res://mipmap/model1")));
        reviewList.add(new Review("Alex Dang", 4.5f, "Phom dáng đẹp, tuy nhiên màu sắc thực tế hơi đậm hơn ảnh một chút.", "15 Oct 2023", null));

        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                finish();
            });
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = getString(R.string.share_message_format, currentProduct.getName(), currentProduct.getPrice());
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.btn_share)));
            });
        }

        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }

        if (btnWriteReview != null) {
            boolean hasPurchased = checkProductPurchased();
            btnWriteReview.setVisibility(hasPurchased ? View.VISIBLE : View.GONE);
            btnWriteReview.setOnClickListener(v -> showWriteReviewDialog());
        }

        if (btnFavorite != null && currentProduct != null) {
            boolean isFavorite = FavoriteManager.getInstance(this).isFavorite(currentProduct);
            btnFavorite.setSelected(isFavorite);
            btnFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_heart_thin);
            btnFavorite.setColorFilter(ContextCompat.getColor(this, R.color.colorMaroon));

            btnFavorite.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                FavoriteManager favoriteManager = FavoriteManager.getInstance(this);
                boolean newState = !favoriteManager.isFavorite(currentProduct);
                
                if (newState) {
                    favoriteManager.addFavorite(currentProduct);
                    btnFavorite.setVisibility(View.INVISIBLE);
                    lottieFavorite.setVisibility(View.VISIBLE);
                    lottieFavorite.playAnimation();
                    lottieFavorite.addAnimatorUpdateListener(animation -> {
                        if (animation.getAnimatedFraction() >= 1f) {
                            lottieFavorite.setVisibility(View.GONE);
                            btnFavorite.setVisibility(View.VISIBLE);
                            btnFavorite.setSelected(true);
                            btnFavorite.setImageResource(R.drawable.ic_favorite);
                            btnFavorite.setColorFilter(ContextCompat.getColor(this, R.color.colorMaroon));
                        }
                    });
                } else {
                    favoriteManager.removeFavorite(currentProduct);
                    btnFavorite.setSelected(false);
                    btnFavorite.setImageResource(R.drawable.ic_heart_thin);
                    btnFavorite.setColorFilter(ContextCompat.getColor(this, R.color.colorMaroon));
                }
                
                ToastUtils.showCustomToast(this, newState ? getString(R.string.toast_added_favorites) : getString(R.string.toast_removed_favorites));
            });
        }

        if (btnDecrease != null) {
            btnDecrease.setOnClickListener(v -> {
                if (selectedQuantity > 1) {
                    selectedQuantity--;
                    tvQuantity.setText(String.valueOf(selectedQuantity));
                }
            });
        }

        if (btnIncrease != null) {
            btnIncrease.setOnClickListener(v -> {
                selectedQuantity++;
                tvQuantity.setText(String.valueOf(selectedQuantity));
            });
        }

        if (btnAddToWardrobe != null) {
            btnAddToWardrobe.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                
                if (currentProduct != null) {
                    if (!isAddedToWardrobe) {
                        // Capture selected variant
                        boolean isGlasses = currentProduct.getCategory() != null && currentProduct.getCategory().toLowerCase().contains("glasses");
                        if (isGlasses) {
                            for (View cv : colorViews) {
                                if (cv != null && cv.isSelected()) {
                                    currentProduct.setSelectedColor(cv.getTag().toString());
                                    break;
                                }
                            }
                        } else {
                            for (View sv : sizeViews) {
                                if (sv != null && sv.isSelected()) {
                                    currentProduct.setSelectedSize(((TextView)sv).getText().toString());
                                    break;
                                }
                            }
                        }

                        CartManager.getInstance(this).addProduct(currentProduct, selectedQuantity);
                        isAddedToWardrobe = true;
                        updateWardrobeUI();
                        ToastUtils.showCustomToast(this, getString(R.string.added_to_wardrobe));
                    } else {
                        // Optionally remove or just show message
                        ToastUtils.showCustomToast(this, getString(R.string.toast_already_in_wardrobe));
                    }
                }
                
                // Press effect
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                }).start();
            });
        }

        for (View sizeView : sizeViews) {
            if (sizeView != null) {
                sizeView.setOnClickListener(v -> {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    for (View sv : sizeViews) {
                        if (sv != null) {
                            sv.setSelected(false);
                            ((TextView)sv).setTextColor(ContextCompat.getColor(this, R.color.colorNoirBlack));
                        }
                    }
                    v.setSelected(true);
                    ((TextView)v).setTextColor(ContextCompat.getColor(this, R.color.white));
                });
            }
        }

        for (View colorView : colorViews) {
            if (colorView != null) {
                colorView.setOnClickListener(v -> {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    for (View cv : colorViews) {
                        if (cv != null) cv.setSelected(false);
                    }
                    v.setSelected(true);
                });
            }
        }

        ivHero.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        });

        if (btnSeeMore != null) {
            btnSeeMore.setOnClickListener(v -> {
                if (tvDescription.getMaxLines() == 4) {
                    tvDescription.setMaxLines(Integer.MAX_VALUE);
                    btnSeeMore.setText(R.string.label_see_less);
                } else {
                    tvDescription.setMaxLines(4);
                    btnSeeMore.setText(R.string.label_see_more);
                }
            });
        }
    }

    private boolean checkProductPurchased() {
        if (currentProduct == null) return false;
        
        List<com.example.saive.models.AdminOrder> orders = com.example.saive.utils.DataManager.getInstance(this).getOrders();
        String productName = currentProduct.getName();
        
        for (com.example.saive.models.AdminOrder order : orders) {
            if (order.getItemsSummary() != null && order.getItemsSummary().contains(productName)) {
                // In a real app, we might also check if status is "Delivered" or "Completed"
                return true;
            }
        }
        return false;
    }

    private void showWriteReviewDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_write_review, null);
        dialog.setContentView(view);

        View btnSubmit = view.findViewById(R.id.btnSubmitReview);
        View btnAddPhoto = view.findViewById(R.id.btnAddPhoto);
        RatingBar ratingBarInput = view.findViewById(R.id.ratingBarInput);
        EditText etComment = view.findViewById(R.id.etComment);

        if (btnAddPhoto != null) {
            btnAddPhoto.setOnClickListener(v -> {
                ToastUtils.showCustomToast(this, getString(R.string.toast_photo_feature_later));
            });
        }

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                
                String comment = etComment.getText().toString().trim();
                float rating = ratingBarInput.getRating();
                
                if (comment.isEmpty()) {
                    ToastUtils.showCustomToast(this, getString(R.string.toast_input_comment));
                    return;
                }

                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                Review newReview = new Review(getString(R.string.review_user_you), rating, comment, currentDate, new ArrayList<>());
                
                reviewList.add(0, newReview);
                reviewAdapter.notifyItemInserted(0);
                rvReviews.scrollToPosition(0);

                ToastUtils.showCustomToast(this, getString(R.string.toast_review_thanks));
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void setupCompleteTheLook() {
        if (rvCompleteLook != null) {
            rvCompleteLook.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            List<Product> suggestions = new ArrayList<>();
            suggestions.add(new Product(getString(R.string.suggestion_trousers), "1.200.000 ₫", R.mipmap.banner3, "Pants"));
            suggestions.add(new Product(getString(R.string.suggestion_loafers), "2.100.000 ₫", R.mipmap.model2, "Shoes"));
            suggestions.add(new Product(getString(R.string.suggestion_scarf), "450.000 ₫", R.mipmap.model1, "Accessories"));
            
            FlashProductAdapter adapter = new FlashProductAdapter(suggestions);
            adapter.setTextColor(ContextCompat.getColor(this, R.color.colorNoirBlack));
            rvCompleteLook.setAdapter(adapter);
        }
    }

    private void updateWardrobeUI() {
        if (isAddedToWardrobe) {
            tvWardrobeAction.setText(R.string.added_to_wardrobe);
            tvWardrobeAction.setTextColor(ContextCompat.getColor(this, R.color.colorMaroon));
            ivWardrobeIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorMaroon));
            btnAddToWardrobe.setBackgroundResource(R.drawable.bg_rounded_white_8dp);
            btnAddToWardrobe.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorCotton)));
            btnAddToWardrobe.setElevation(0);
        } else {
            tvWardrobeAction.setText(R.string.add_to_wardrobe);
            tvWardrobeAction.setTextColor(ContextCompat.getColor(this, R.color.white));
            ivWardrobeIcon.setColorFilter(ContextCompat.getColor(this, R.color.white));
            btnAddToWardrobe.setBackgroundResource(R.drawable.bg_rounded_maroon_12dp);
            btnAddToWardrobe.setBackgroundTintList(null);
            btnAddToWardrobe.setElevation(8 * getResources().getDisplayMetrics().density);
        }
    }
}

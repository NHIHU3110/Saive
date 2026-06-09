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
import com.example.saive.utils.DataManager;

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

import java.util.Arrays;
import java.util.List;

public class ProductDetailActivity extends BaseActivity {

    private ImageView ivHero, ivWardrobeIcon, btnBack;
    private ImageButton btnFavorite, btnShare;
    private View btnCart;
    private LottieAnimationView lottieFavorite;
    private TextView tvProductName, tvPrice, tvOriginalPrice, tvDescription, tvWardrobeAction, btnWriteReview, btnSeeMore, tvCartBadge, tvQuantity, tvSizeStockStatus, tvColorStockStatus;
    private View btnAddToWardrobe, sizeSelectionContainer, colorSelectionContainer, btnSizeGuide;
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
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvDescription = findViewById(R.id.tvDescription);
        btnSeeMore = findViewById(R.id.btnSeeMore);
        btnWriteReview = findViewById(R.id.btnWriteReview);
        
        tvQuantity = findViewById(R.id.tvQuantity);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        
        rvCompleteLook = findViewById(R.id.rvCompleteLook);
        rvReviews = findViewById(R.id.rvReviews);
        
        btnAddToWardrobe = findViewById(R.id.btnAddToWardrobe);
        btnSizeGuide = findViewById(R.id.btnSizeGuide);
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

        tvSizeStockStatus = findViewById(R.id.tvSizeStockStatus);
        tvColorStockStatus = findViewById(R.id.tvColorStockStatus);
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
        
        // Reset selections when opening detail to force user to choose
        currentProduct.setSelectedSize(null);
        currentProduct.setSelectedColor(null);
        
        tvProductName.setText(currentProduct.getName());
        tvPrice.setText(PriceFormatter.formatPrice(currentProduct.getPrice()));
        
        if (currentProduct.getOriginalPrice() != null) {
            tvOriginalPrice.setText(PriceFormatter.formatPrice(currentProduct.getOriginalPrice()));
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
        }

        tvDescription.setText(currentProduct.getDescription());
        ImageUtils.setSafeImage(ivHero, currentProduct.getImageResId());

        // Toggle Size/Color selection based on category
        String category = currentProduct.getCategory() != null ? currentProduct.getCategory().toLowerCase() : "";
        boolean isGlasses = category.contains("glasses");
        boolean isPerfume = category.contains("perfume");

        if (sizeSelectionContainer != null) {
            sizeSelectionContainer.setVisibility((isGlasses || isPerfume) ? View.GONE : View.VISIBLE);
        }
        if (btnSizeGuide != null) {
            btnSizeGuide.setVisibility((isGlasses || isPerfume) ? View.GONE : View.VISIBLE);
        }
        if (colorSelectionContainer != null) {
            colorSelectionContainer.setVisibility(isPerfume ? View.GONE : View.VISIBLE);
        }

        // Check if already in wardrobe
        updateWardrobeStatus();
    }

    private void setupReviews() {
        if (currentProduct == null) return;
        
        String currentProductName = currentProduct.getName();
        reviewList = new ArrayList<>();
        
        // Lấy tất cả reviews từ DataManager
        List<Review> allReviews = DataManager.getInstance(this).getReviews();
        
        // Lọc reviews cho sản phẩm hiện tại
        for (Review r : allReviews) {
            if (currentProductName.equals(r.getProductName())) {
                reviewList.add(r);
            }
        }

        // Nếu không có review nào, thêm dữ liệu mẫu (cho sản phẩm cụ thể này)
        if (reviewList.isEmpty()) {
            reviewList.add(new Review(currentProductName, "Nhi Huynh", 5.0f, "Chất liệu tuyệt vời, mặc rất thoải mái và sang trọng.", "24 Oct 2023", java.util.Arrays.asList("res://mipmap/model1")));
            reviewList.add(new Review(currentProductName, "Alex Dang", 4.5f, "Phom dáng đẹp, tuy nhiên màu sắc thực tế hơi đậm hơn ảnh một chút.", "15 Oct 2023", null));
        }

        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }

    private void setupListeners() {
        if (btnSizeGuide != null) {
            btnSizeGuide.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showSizeGuideDialog();
            });
        }

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
            // Tạm thời ẩn nút viết đánh giá theo yêu cầu
            btnWriteReview.setVisibility(View.GONE);
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
            });
        }

        if (btnIncrease != null) {
            btnIncrease.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                selectedQuantity++;
                tvQuantity.setText(String.valueOf(selectedQuantity));
            });
        }

        if (btnDecrease != null) {
            btnDecrease.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (selectedQuantity > 1) {
                    selectedQuantity--;
                    tvQuantity.setText(String.valueOf(selectedQuantity));
                }
            });
        }

        if (btnAddToWardrobe != null) {
            btnAddToWardrobe.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                toggleWardrobe();
            });
        }

        if (btnSeeMore != null) {
            btnSeeMore.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                // Implementation for see more
            });
        }

        setupSelectionListeners(sizeViews, true);
        setupSelectionListeners(colorViews, false);
    }

    private boolean checkProductPurchased() {
        // Logic to check if user purchased this product
        return true; // Simplified for now
    }

    private void showWriteReviewDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.TransparentBottomSheetDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.layout_write_review, null);
        bottomSheetDialog.setContentView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBarInput);
        EditText etReview = dialogView.findViewById(R.id.etComment);
        View btnSubmit = dialogView.findViewById(R.id.btnSubmitReview);

        btnSubmit.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            float rating = ratingBar.getRating();
            String reviewText = etReview.getText().toString().trim();
            
            if (reviewText.isEmpty()) {
                ToastUtils.showCustomToast(this, "Please enter your review");
                return;
            }

            Review newReview = new Review(
                currentProduct.getName(),
                "Me", // In real app, get user name
                rating,
                reviewText,
                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date()),
                null
            );

            DataManager.getInstance(this).addReview(newReview);
            reviewList.add(0, newReview);
            reviewAdapter.notifyItemInserted(0);
            rvReviews.scrollToPosition(0);

            bottomSheetDialog.dismiss();
            ToastUtils.showCustomToast(this, "Review submitted successfully!");
        });

        bottomSheetDialog.show();
    }

    private void setupSelectionListeners(List<View> views, boolean isSize) {
        for (View view : views) {
            if (view != null) {
                view.setOnClickListener(v -> {
                    // Hiệu ứng rung nhẹ
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                    
                    // Reset trạng thái các nút khác
                    for (View other : views) {
                        other.setSelected(false);
                    }
                    
                    // Kích hoạt trạng thái chọn cho nút hiện tại
                    v.setSelected(true);
                    
                    if (isSize && v instanceof TextView) {
                        TextView tv = (TextView) v;
                        String size = tv.getText().toString();
                        currentProduct.setSelectedSize(size);
                        updateStockStatus(size, true);
                    } else if (!isSize && v.getTag() != null) {
                        String color = v.getTag().toString();
                        currentProduct.setSelectedColor(color);
                        updateStockStatus(color, false);
                    }
                    
                    updateWardrobeStatus();
                });
            }
        }
    }

    private void updateStockStatus(String selection, boolean isSize) {
        if (isSize) {
            if ("XL".equals(selection)) {
                tvSizeStockStatus.setText(R.string.inventory_low_stock);
                tvSizeStockStatus.setVisibility(View.VISIBLE);
            } else if ("XS".equals(selection)) {
                tvSizeStockStatus.setText(R.string.inventory_out_of_stock);
                tvSizeStockStatus.setVisibility(View.VISIBLE);
            } else {
                tvSizeStockStatus.setVisibility(View.GONE);
            }
        } else {
            if ("Gold".equals(selection)) {
                tvColorStockStatus.setText(R.string.inventory_low_stock);
                tvColorStockStatus.setVisibility(View.VISIBLE);
            } else if ("Silver".equals(selection)) {
                tvColorStockStatus.setText(R.string.inventory_out_of_stock);
                tvColorStockStatus.setVisibility(View.VISIBLE);
            } else {
                tvColorStockStatus.setVisibility(View.GONE);
            }
        }
    }

    private void updateWardrobeStatus() {
        isAddedToWardrobe = CartManager.getInstance(this).isProductInCart(currentProduct);
        updateWardrobeUI();
    }

    private void toggleWardrobe() {
        // Kiểm tra chọn size nếu container đang hiển thị
        if (sizeSelectionContainer.getVisibility() == View.VISIBLE && currentProduct.getSelectedSize() == null) {
            ToastUtils.showCustomToast(this, "Vui lòng chọn kích thước (Size)");
            return;
        }
        // Kiểm tra chọn màu nếu container đang hiển thị
        if (colorSelectionContainer.getVisibility() == View.VISIBLE && currentProduct.getSelectedColor() == null) {
            ToastUtils.showCustomToast(this, "Vui lòng chọn màu sắc (Color)");
            return;
        }

        CartManager cartManager = CartManager.getInstance(this);
        
        // Block if out of stock (Mock logic)
        String selectedSize = currentProduct.getSelectedSize();
        String selectedColor = currentProduct.getSelectedColor();
        
        if ("XS".equals(selectedSize) || "Silver".equals(selectedColor)) {
            ToastUtils.showCustomToast(this, getString(R.string.inventory_out_of_stock));
            return;
        }

        cartManager.addProduct(currentProduct, selectedQuantity);
        ToastUtils.showCustomToast(this, "Added to Wardrobe");
        
        updateWardrobeStatus();
    }

    private void updateWardrobeUI() {
        // Luôn hiển thị trạng thái ADD TO BAG để khuyến khích mua thêm
        btnAddToWardrobe.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white)));
        ivWardrobeIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorMaroon));
        tvWardrobeAction.setText(R.string.btn_add_to_wardrobe);
        tvWardrobeAction.setTextColor(ContextCompat.getColor(this, R.color.colorMaroon));
    }

    private void setupCompleteTheLook() {
        List<Product> products = new ArrayList<>();
        // Sản phẩm đang giảm giá (có giá gốc)
        products.add(new Product("L'Amour Luxe", "1.450.000 ₫", "2.100.000 ₫", R.mipmap.model2, "Perfume"));
        products.add(new Product("Elite Essence", "1.850.000 ₫", R.mipmap.model1, "Perfume"));
        products.add(new Product("Mystic Bloom", "990.000 ₫", "1.500.000 ₫", R.mipmap.model2, "Perfume"));

        FlashProductAdapter adapter = new FlashProductAdapter(products);
        
        rvCompleteLook.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCompleteLook.setAdapter(adapter);
    }
}

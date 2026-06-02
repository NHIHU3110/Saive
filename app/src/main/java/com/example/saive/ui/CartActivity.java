package com.example.saive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.adapters.CartAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Product;
import com.example.saive.utils.CartManager;
import com.example.saive.utils.DialogUtils;
import com.example.saive.utils.ToastUtils;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class CartActivity extends BaseActivity {

    private RecyclerView rvCartItems;
    private View emptyStateCart;
    private TextView tvSubtotal, tvTotalPrice, tvDiscountValue;
    private View layoutDiscount;
    private EditText etCoupon;
    private View btnApplyCoupon;
    private CartAdapter adapter;
    private CartManager cartManager;

    private double currentDiscountRate = 0;
    private String appliedCouponCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        updateStatusBar();

        setContentView(R.layout.activity_cart);

        initViews();
        setupCartList();

        handleIntent(getIntent());
    }

    private void updateStatusBar() {
        boolean isDarkMode = (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) 
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        
        // Setup status bar for full-bleed maroon header
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        
        // In Dark Mode, colorMaroon becomes light beige, so we need dark icons
        if (isDarkMode) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("COUPON_CODE")) {
            String code = intent.getStringExtra("COUPON_CODE");
            if (code != null) {
                etCoupon.setText(code);
                applyCoupon();
            }
        }
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);
        emptyStateCart = findViewById(R.id.emptyStateCart);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvDiscountValue = findViewById(R.id.tvDiscountValue);
        layoutDiscount = findViewById(R.id.layoutDiscount);
        etCoupon = findViewById(R.id.etCoupon);
        btnApplyCoupon = findViewById(R.id.btnApplyCoupon);
        View btnBack = findViewById(R.id.btnBack);
        View btnCheckout = findViewById(R.id.btnCheckout);

        // Navigation Bar Items
        View navFavorite = findViewById(R.id.navFavorite);
        View navWardrobe = findViewById(R.id.navWardrobe);
        View navHome = findViewById(R.id.navHome);
        View navNotify = findViewById(R.id.navNotify);
        View navProfile = findViewById(R.id.navProfile);
        View centerActionButton = findViewById(R.id.centerActionButton);

        btnBack.setOnClickListener(v -> finish());

        // Navigation Listeners
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.putExtra("SHOW_FAVORITES", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        navWardrobe.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.putExtra("SHOW_WARDROBE", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        View.OnClickListener homeListener = v -> {
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.putExtra("SHOW_HOME", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        };
        navHome.setOnClickListener(homeListener);
        centerActionButton.setOnClickListener(homeListener);

        navNotify.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.putExtra("SHOW_NOTIFICATIONS", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartManager.getCartItems().isEmpty()) {
                ToastUtils.showCustomToast(this, "Giỏ hàng đang trống");
                return;
            }
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("total_price", tvTotalPrice.getText().toString());
            intent.putExtra("discount_rate", currentDiscountRate);
            intent.putExtra("coupon_code", appliedCouponCode);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnApplyCoupon.setOnClickListener(v -> {
            applyCoupon();
        });

        // Add click listener to etCoupon or a container to open CouponActivity
        etCoupon.setFocusable(false);
        etCoupon.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CouponActivity.class);
            startActivityForResult(intent, 1001);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String code = data.getStringExtra("COUPON_CODE");
            if (code != null) {
                etCoupon.setText(code);
                applyCoupon();
            }
        }
    }

    private void applyCoupon() {
        String code = etCoupon.getText().toString().trim().toUpperCase();
        if (code.isEmpty()) {
            ToastUtils.showCustomToast(this, "Vui lòng nhập mã giảm giá");
            return;
        }

        // Logic kiểm tra mã giảm giá
        if (code.equals("WELCOME20")) {
            currentDiscountRate = 0.20;
            appliedCouponCode = code;
            ToastUtils.showCustomToast(this, "Đã áp dụng mã giảm giá 20%");
        } else if (code.equals("SILK15")) {
            currentDiscountRate = 0.15;
            appliedCouponCode = code;
            ToastUtils.showCustomToast(this, "Đã áp dụng mã giảm giá 15%");
        } else if (code.equals("REWARD10")) {
            currentDiscountRate = 0.10;
            appliedCouponCode = code;
            ToastUtils.showCustomToast(this, "Đã áp dụng mã giảm giá 10%");
        } else if (code.equals("ARCHIVE25")) {
            currentDiscountRate = 0.25;
            appliedCouponCode = code;
            ToastUtils.showCustomToast(this, "Đã áp dụng mã giảm giá 25%");
        } else if (code.equals("SAIVE_S24_EXTRA")) {
            currentDiscountRate = 0.15; // Giảm 15%
            appliedCouponCode = code;
            ToastUtils.showCustomToast(this, "Đã áp dụng mã giảm giá 15%");
        } else if (code.equals("SAIVE10")) {
            currentDiscountRate = 0.10; // Giảm 10%
            appliedCouponCode = code;
            ToastUtils.showCustomToast(this, "Đã áp dụng mã giảm giá 10%");
        } else if (code.equals("WELCOME5")) {
            currentDiscountRate = 0.05; // Giảm 5%
            appliedCouponCode = code;
            ToastUtils.showCustomToast(this, "Đã áp dụng mã giảm giá 5%");
        } else {
            currentDiscountRate = 0;
            appliedCouponCode = "";
            ToastUtils.showCustomToast(this, "Mã giảm giá không hợp lệ");
        }
        
        updateTotal();
    }

    private void setupCartList() {
        cartManager = CartManager.getInstance(this);
        List<Product> cartItems = cartManager.getCartItems();

        if (cartItems.isEmpty()) {
            checkEmptyState();
        } else {
            rvCartItems.setVisibility(View.VISIBLE);
            emptyStateCart.setVisibility(View.GONE);

            adapter = new CartAdapter(cartItems, new CartAdapter.OnCartChangeListener() {
                @Override
                public void onRemove(int position) {
                    DialogUtils.showCustomAlertDialog(
                            CartActivity.this,
                            "Xóa sản phẩm",
                            "Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?",
                            "Xóa",
                            "Hủy",
                            () -> {
                                Product product = cartItems.get(position);
                                cartManager.removeProduct(product);
                                adapter.notifyItemRemoved(position);
                                updateTotal();
                                checkEmptyState();
                            }
                    );
                }

                @Override
                public void onQuantityChanged() {
                    updateTotal();
                }

                @Override
                public void onVariantClick(int position, Product product) {
                    showVariantSelectionDialog(position, product);
                }
            });

            rvCartItems.setLayoutManager(new LinearLayoutManager(this));
            rvCartItems.setAdapter(adapter);
            updateTotal();
        }
    }

    private void showVariantSelectionDialog(int position, Product product) {
        boolean isGlasses = product.getCategory() != null && product.getCategory().toLowerCase().contains("glasses");
        String[] options = isGlasses ? new String[]{"Black", "Tortoise", "Gold", "Silver"} : new String[]{"XS", "S", "M", "L", "XL"};
        String title = isGlasses ? "Chọn màu sắc" : "Chọn kích cỡ";
        String currentSelection = isGlasses ? product.getSelectedColor() : product.getSelectedSize();

        View dialogView = getLayoutInflater().inflate(R.layout.layout_variant_selection, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        LinearLayout optionsContainer = dialogView.findViewById(R.id.optionsContainer);
        tvTitle.setText(title);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Base_Theme_Saive).setView(dialogView).create();

        for (String option : options) {
            TextView btnOption = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (48 * getResources().getDisplayMetrics().density),
                    (int) (48 * getResources().getDisplayMetrics().density));
            params.setMargins(0, 0, (int) (12 * getResources().getDisplayMetrics().density), 0);
            btnOption.setLayoutParams(params);
            btnOption.setGravity(android.view.Gravity.CENTER);
            btnOption.setText(option);
            btnOption.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            btnOption.setTextAppearance(R.style.TextAppearance_Saive_Nav);
            btnOption.setBackgroundResource(R.drawable.bg_variant_selector);
            
            if (option.equals(currentSelection)) {
                btnOption.setSelected(true);
                btnOption.setTextColor(getResources().getColor(R.color.white));
            } else {
                btnOption.setSelected(false);
                btnOption.setTextColor(getResources().getColor(R.color.colorNoirBlack));
            }

            btnOption.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (isGlasses) {
                    product.setSelectedColor(option);
                } else {
                    product.setSelectedSize(option);
                }
                adapter.notifyItemChanged(position);
                cartManager.updateQuantity(product, product.getQuantity());
                dialog.dismiss();
            });

            optionsContainer.addView(btnOption);
        }

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }

    private void updateTotal() {
        double subtotal = cartManager.getTotalPrice();
        double discountAmount = subtotal * currentDiscountRate;
        double total = subtotal - discountAmount;

        tvSubtotal.setText(formatCurrency(subtotal));
        
        if (currentDiscountRate > 0) {
            layoutDiscount.setVisibility(View.VISIBLE);
            tvDiscountValue.setText("-" + formatCurrency(discountAmount));
        } else {
            layoutDiscount.setVisibility(View.GONE);
        }
        
        tvTotalPrice.setText(formatCurrency(total));
    }

    private String formatCurrency(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(amount) + " ₫";
    }

    private void checkEmptyState() {
        if (cartManager.getCartItems().isEmpty()) {
            rvCartItems.setVisibility(View.GONE);
            emptyStateCart.setVisibility(View.VISIBLE);
            tvSubtotal.setText("0 ₫");
            tvTotalPrice.setText("0 ₫");
            layoutDiscount.setVisibility(View.GONE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
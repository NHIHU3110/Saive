package com.example.saive.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.adapters.CouponAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Coupon;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class CouponActivity extends BaseActivity {

    private RecyclerView rvCoupons;
    private CouponAdapter adapter;
    private List<Coupon> couponList;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
            getWindow().getDecorView().setSystemUiVisibility(
                getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        etSearch = findViewById(R.id.etSearch);

        rvCoupons = findViewById(R.id.rvCoupons);
        rvCoupons.setLayoutManager(new LinearLayoutManager(this));

        loadCoupons();

        adapter = new CouponAdapter(couponList, this::showCouponDetail);
        rvCoupons.setAdapter(adapter);

        setupSearch();
    }

    private void showCouponDetail(Coupon coupon) {
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_coupon_detail, null);

        TextView tvTitle = view.findViewById(R.id.tvDetailTitle);
        TextView tvDiscount = view.findViewById(R.id.tvDetailDiscount);
        TextView tvCode = view.findViewById(R.id.tvDetailCode);
        MaterialButton btnCopy = view.findViewById(R.id.btnCopyDetail);

        tvTitle.setText(coupon.getTitle());
        tvDiscount.setText("Get " + coupon.getDiscount() + " OFF on your order");
        tvCode.setText(coupon.getCode());

        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Coupon Code", coupon.getCode());
            clipboard.setPrimaryClip(clip);
            
            showCustomToast("Đã sao chép mã " + coupon.getCode());
            
            if (getCallingActivity() != null) {
                // Trả kết quả về cho CartActivity nếu được gọi bằng startActivityForResult
                Intent resultIntent = new Intent();
                resultIntent.putExtra("COUPON_CODE", coupon.getCode());
                setResult(RESULT_OK, resultIntent);
            } else {
                // Chuyển trực tiếp sang CartActivity nếu mở từ Profile
                Intent intent = new Intent(this, CartActivity.class);
                intent.putExtra("COUPON_CODE", coupon.getCode());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            
            dialog.dismiss();
            finish();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                return true;
            }
            return false;
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadCoupons() {
        couponList = new ArrayList<>();
        couponList.add(new Coupon("SAIVE WELCOME", "On your first archive access.", "20%", "Dec 31, 2024", "WELCOME20"));
        couponList.add(new Coupon("SILK STORY", "Exclusive for Silk series.", "15%", "Nov 15, 2024", "SILK15"));
        couponList.add(new Coupon("SEASONAL REWARD", "For orders over $200.", "10%", "Oct 20, 2024", "REWARD10"));
        couponList.add(new Coupon("ARCHIVE ACCESS", "Limited time archive discount.", "25%", "Dec 01, 2024", "ARCHIVE25"));
    }
}

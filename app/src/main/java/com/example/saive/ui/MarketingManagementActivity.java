package com.example.saive.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.adapters.AdminCouponAdapter;
import com.example.saive.models.Coupon;
import java.util.ArrayList;
import java.util.List;

import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.example.saive.base.BaseActivity;
import java.util.Calendar;

import com.example.saive.utils.DataManager;

public class MarketingManagementActivity extends BaseActivity {

    private List<Coupon> couponList;
    private AdminCouponAdapter adapter;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_management);
        dataManager = DataManager.getInstance(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddCoupon).setOnClickListener(v -> showAddVoucherDialog());

        RecyclerView rvCoupons = findViewById(R.id.rvCoupons);
        rvCoupons.setLayoutManager(new LinearLayoutManager(this));

        couponList = dataManager.getCoupons();

        adapter = new AdminCouponAdapter(couponList);
        rvCoupons.setAdapter(adapter);
    }

    private void showAddVoucherDialog() {
        // ... (existing code for dialog)
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.SaiveDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_voucher, null);
        builder.setView(view);

        EditText etExpiry = view.findViewById(R.id.etVoucherExpiry);
        etExpiry.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                etExpiry.setText(date);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btnSaveVoucher).setOnClickListener(v -> {
            String name = ((EditText) view.findViewById(R.id.etVoucherName)).getText().toString();
            String code = ((EditText) view.findViewById(R.id.etVoucherCode)).getText().toString();
            String discount = ((EditText) view.findViewById(R.id.etVoucherDiscount)).getText().toString();
            String expiry = etExpiry.getText().toString();
            String desc = ((EditText) view.findViewById(R.id.etVoucherDesc)).getText().toString();

            if (!name.isEmpty() && !code.isEmpty()) {
                Coupon newCoupon = new Coupon(name, desc, discount, expiry, code, "Active", 0);
                couponList.add(0, newCoupon);
                dataManager.saveCoupons(couponList);
                adapter.notifyItemInserted(0);
                rvCouponsScrollToTop();
                dialog.dismiss();
                Toast.makeText(this, "Voucher created", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void rvCouponsScrollToTop() {
        RecyclerView rvCoupons = findViewById(R.id.rvCoupons);
        rvCoupons.scrollToPosition(0);
    }
}

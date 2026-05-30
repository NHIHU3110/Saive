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

public class MarketingManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_management);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddCoupon).setOnClickListener(v -> 
            Toast.makeText(this, "Tính năng tạo mã giảm giá mới", Toast.LENGTH_SHORT).show()
        );

        RecyclerView rvCoupons = findViewById(R.id.rvCoupons);
        rvCoupons.setLayoutManager(new LinearLayoutManager(this));

        List<Coupon> couponList = new ArrayList<>();
        couponList.add(new Coupon("Silk Sale", "20% OFF on Silk Collection", "20%", "2024-12-31", "SILK20"));
        couponList.add(new Coupon("Welcome", "10% OFF for new users", "10%", "2025-01-01", "WELCOME10"));
        couponList.add(new Coupon("Black Friday", "50% OFF everything", "50%", "2024-11-30", "BLACK50"));

        AdminCouponAdapter adapter = new AdminCouponAdapter(couponList);
        rvCoupons.setAdapter(adapter);
    }
}

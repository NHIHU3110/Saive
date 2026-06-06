package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;

import android.widget.TextView;
import com.example.saive.models.AdminOrder;
import com.example.saive.utils.DataManager;

public class OrderTrackingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        String orderId = getIntent().getStringExtra("orderId");
        if (orderId != null) {
            AdminOrder order = DataManager.getInstance(this).getOrderById(orderId);
            if (order != null) {
                TextView tvOrderId = findViewById(R.id.orderId);
                TextView tvPaymentInfo = findViewById(R.id.paymentInfo);
                TextView tvProductName = findViewById(R.id.productName);

                tvOrderId.setText("Order " + order.getOrderId());
                tvProductName.setText(order.getItemsSummary());
                tvPaymentInfo.setText("Total: " + order.getTotalAmount());
            }
        }
    }
}

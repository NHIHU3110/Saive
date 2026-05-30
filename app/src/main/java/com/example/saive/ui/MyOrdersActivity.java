package com.example.saive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.adapters.UserOrderAdapter;
import com.example.saive.models.AdminOrder;
import com.example.saive.utils.DataManager;
import java.util.List;

public class MyOrdersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rvOrders = findViewById(R.id.rvMyOrders);
        View emptyState = findViewById(R.id.emptyState);

        List<AdminOrder> orderList = DataManager.getInstance(this).getOrders();
        
        if (orderList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            rvOrders.setLayoutManager(new LinearLayoutManager(this));
            rvOrders.setAdapter(new UserOrderAdapter(orderList));
        }
    }
}

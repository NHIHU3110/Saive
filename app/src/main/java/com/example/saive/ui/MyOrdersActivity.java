package com.example.saive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.adapters.UserOrderAdapter;
import com.example.saive.models.AdminOrder;
import com.example.saive.utils.DataManager;
import com.google.android.material.tabs.TabLayout;
import java.util.List;
import java.util.stream.Collectors;

public class MyOrdersActivity extends BaseActivity {

    private RecyclerView rvOrders;
    private View emptyState;
    private TabLayout tabLayout;
    private List<AdminOrder> allOrders;
    private UserOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
            getWindow().getDecorView().setSystemUiVisibility(0);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvOrders = findViewById(R.id.rvMyOrders);
        emptyState = findViewById(R.id.emptyState);
        tabLayout = findViewById(R.id.tabLayout);

        setupTabs();
        loadOrders();
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_active));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_completed));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_cancelled));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterOrders(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
        // No-op
    }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
        // No-op
    }
        });
    }

    private void loadOrders() {
        allOrders = DataManager.getInstance(this).getOrders();
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        filterOrders(0); // Show "Active" by default
    }

    private void filterOrders(int tabPosition) {
        List<AdminOrder> filteredList;
        String statusFilter;

        switch (tabPosition) {
            case 1:
                statusFilter = "COMPLETED";
                break;
            case 2:
                statusFilter = "CANCELLED";
                break;
            default:
                statusFilter = "PENDING"; // Active covers PENDING and SHIPPED usually
                break;
        }

        filteredList = allOrders.stream()
                .filter(o -> {
                    String s = o.getStatus() != null ? o.getStatus().toUpperCase(java.util.Locale.ROOT) : "PENDING";
                    if (tabPosition == 0) return s.equals("PENDING") || s.equals("SHIPPED");
                    return s.equals(statusFilter);
                })
                .collect(Collectors.toList());

        updateUI(filteredList);
    }

    private void updateUI(List<AdminOrder> orders) {
        if (orders.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            adapter = new UserOrderAdapter(orders, new UserOrderAdapter.OnOrderClickListener() {
                @Override
                public void onOrderClick(AdminOrder order) {
                    Intent intent = new Intent(MyOrdersActivity.this, OrderTrackingActivity.class);
                    intent.putExtra("orderId", order.getOrderId());
                    startActivity(intent);
                }

                @Override
                public void onActionClick(AdminOrder order) {
                    String s = order.getStatus() != null ? order.getStatus().toUpperCase(java.util.Locale.ROOT) : "PENDING";
                    if (s.equals("COMPLETED")) {
                        ReviewBottomSheetFragment fragment = ReviewBottomSheetFragment.newInstance(order);
                        fragment.show(getSupportFragmentManager(), "ReviewBottomSheet");
                    } else {
                        Intent intent = new Intent(MyOrdersActivity.this, OrderTrackingActivity.class);
                        intent.putExtra("orderId", order.getOrderId());
                        startActivity(intent);
                    }
                }
            });
            rvOrders.setAdapter(adapter);
        }
    }
}
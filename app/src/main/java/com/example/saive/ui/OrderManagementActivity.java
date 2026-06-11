package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.adapters.AdminOrderAdapter;
import com.example.saive.models.AdminOrder;
import com.example.saive.models.OrderItem;
import java.util.ArrayList;
import java.util.List;

import com.example.saive.base.BaseActivity;
import com.example.saive.utils.DataManager;

public class OrderManagementActivity extends BaseActivity {

    private RecyclerView rvOrders;
    private AdminOrderAdapter adapter;
    private List<AdminOrder> orderList;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);
        dataManager = DataManager.getInstance(this);

        rvOrders = findViewById(R.id.rvOrders);
        
        setupHeader();
        setupOrderList();
    }

    private void setupHeader() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnFilter).setOnClickListener(v -> 
            Toast.makeText(this, "Tính năng lọc đơn hàng đang được phát triển", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupOrderList() {
        orderList = dataManager.getOrders();
        if (orderList.isEmpty()) {
            AdminOrder order1 = new AdminOrder("#SA-9012", "Lê Minh Trí", "1 Item: Silk Blazer", "1.250.000 ₫", "PENDING", "5m ago", R.mipmap.model2, "M", 1, "Credit Card", "456 CMT8, District 3, HCMC");
            List<OrderItem> items1 = new ArrayList<>();
            items1.add(new OrderItem("Silk Blazer", "M", 1, "1.250.000 ₫", R.mipmap.model2));
            order1.setItems(items1);
            orderList.add(order1);

            AdminOrder order2 = new AdminOrder("#SA-9011", "Nguyễn An", "3 Items: Wool Scarf, T-Shirt...", "840.000 ₫", "SHIPPED", "1h ago", R.mipmap.model1, "L", 3, "COD", "789 Phan Xich Long, PN, HCMC");
            List<OrderItem> items2 = new ArrayList<>();
            items2.add(new OrderItem("Wool Scarf", "L", 1, "400.000 ₫", R.mipmap.model1));
            items2.add(new OrderItem("T-Shirt", "L", 2, "440.000 ₫", R.mipmap.model2));
            order2.setItems(items2);
            orderList.add(order2);

            AdminOrder order3 = new AdminOrder("#SA-9010", "Trần Thị B", "2 Items: Sun Glasses, Jacket", "2.100.000 ₫", "COMPLETED", "3h ago", R.mipmap.model1, "L", 1, "Momo", "123 Le Loi, District 1, HCMC, Vietnam");
            List<OrderItem> items3 = new ArrayList<>();
            items3.add(new OrderItem("Sun Glasses", "One Size", 1, "600.000 ₫", R.mipmap.sunglass1));
            items3.add(new OrderItem("Jacket", "L", 1, "1.500.000 ₫", R.mipmap.jacket1));
            order3.setItems(items3);
            orderList.add(order3);

            dataManager.saveOrders(orderList);
        }

        adapter = new AdminOrderAdapter(orderList, this::showOrderDetails);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }

    private void showOrderDetails(AdminOrder order) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_order_details, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvId = dialogView.findViewById(R.id.tvOrderDetailsId);
        TextView tvCustomer = dialogView.findViewById(R.id.tvOrderDetailsCustomer);
        TextView tvStatus = dialogView.findViewById(R.id.tvOrderDetailsStatus);
        
        tvId.setText(order.getOrderId());
        tvCustomer.setText(order.getCustomerName());
        tvStatus.setText(order.getStatus());

        TextView tvShippingAddress = dialogView.findViewById(R.id.tvOrderDetailsAddress);
        if (tvShippingAddress != null) {
            tvShippingAddress.setText(order.getShippingAddress());
        }
        
        TextView tvPaymentMethod = dialogView.findViewById(R.id.tvOrderDetailsPayment);
        if (tvPaymentMethod != null) {
            tvPaymentMethod.setText(order.getPaymentMethod());
        }

        android.widget.LinearLayout itemsContainer = dialogView.findViewById(R.id.itemsContainer);
        TextView tvSeeMore = dialogView.findViewById(R.id.tvSeeMore);

        if (itemsContainer != null && order.getItems() != null && !order.getItems().isEmpty()) {
            itemsContainer.removeAllViews();
            List<OrderItem> items = order.getItems();
            for (int i = 0; i < items.size(); i++) {
                OrderItem item = items.get(i);
                View itemView = getLayoutInflater().inflate(R.layout.item_order_detail, itemsContainer, false);
                
                android.widget.ImageView ivItem = itemView.findViewById(R.id.ivItemImage);
                TextView tvName = itemView.findViewById(R.id.tvItemName);
                TextView tvAttributes = itemView.findViewById(R.id.tvItemAttributes);
                TextView tvPrice = itemView.findViewById(R.id.tvItemPrice);

                tvName.setText(item.getName());
                tvPrice.setText(item.getPrice());
                tvAttributes.setText(getString(R.string.format_order_attributes, item.getSize(), item.getQuantity()));
                ivItem.setImageResource(item.getImageResId() != 0 ? item.getImageResId() : R.mipmap.model1);

                if (i > 0) {
                    itemView.setVisibility(View.GONE);
                }
                itemsContainer.addView(itemView);
            }

            if (items.size() > 1) {
                tvSeeMore.setVisibility(View.VISIBLE);
                tvSeeMore.setText(getString(R.string.format_order_see_more, items.size() - 1));
                tvSeeMore.setOnClickListener(v -> {
                    tvSeeMore.setVisibility(View.GONE);
                    for (int i = 1; i < itemsContainer.getChildCount(); i++) {
                        itemsContainer.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                });
            } else {
                tvSeeMore.setVisibility(View.GONE);
            }
        } else {
            // Fallback for legacy orders
            tvSeeMore.setVisibility(View.GONE);
            if (itemsContainer != null) {
                itemsContainer.removeAllViews();
                View itemView = getLayoutInflater().inflate(R.layout.item_order_detail, itemsContainer, false);
                android.widget.ImageView ivItem = itemView.findViewById(R.id.ivItemImage);
                TextView tvName = itemView.findViewById(R.id.tvItemName);
                TextView tvAttributes = itemView.findViewById(R.id.tvItemAttributes);
                TextView tvPrice = itemView.findViewById(R.id.tvItemPrice);

                tvName.setText(order.getItemsSummary());
                tvPrice.setText(order.getTotalAmount());
                tvAttributes.setText(getString(R.string.format_order_attributes, order.getSize(), order.getQuantity()));
                ivItem.setImageResource(order.getProductImageResId() != 0 ? order.getProductImageResId() : R.mipmap.model1);
                itemsContainer.addView(itemView);
            }
        }

        dialogView.findViewById(R.id.btnMarkShipped).setOnClickListener(v -> {
            order.setStatus("SHIPPED");
            dataManager.updateOrderStatus(order.getOrderId(), "SHIPPED");
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Order " + order.getOrderId() + " marked as SHIPPED", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btnExportInvoice).setOnClickListener(v -> {
            Toast.makeText(this, "Generating PDF Invoice...", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}
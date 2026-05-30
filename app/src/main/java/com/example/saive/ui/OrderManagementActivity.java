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
            orderList.add(new AdminOrder("#SA-9012", "Lê Minh Trí", "1 Item: Silk Blazer", "1.250.000 ₫", "PENDING", "5m ago"));
            orderList.add(new AdminOrder("#SA-9011", "Nguyễn An", "3 Items: Wool Scarf, T-Shirt...", "840.000 ₫", "SHIPPED", "1h ago"));
            orderList.add(new AdminOrder("#SA-9010", "Trần Thị B", "2 Items: Sun Glasses, Jacket", "2.100.000 ₫", "COMPLETED", "3h ago"));
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
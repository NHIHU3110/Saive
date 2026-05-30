package com.example.saive.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.adapters.AdminOrderAdapter;
import com.example.saive.models.AdminOrder;
import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private AdminOrderAdapter adapter;
    private List<AdminOrder> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

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
        orderList = new ArrayList<>();
        orderList.add(new AdminOrder("#SA-9012", "Lê Minh Trí", "1 Item: Silk Blazer", "1.250.000 ₫", "PENDING", "5m ago"));
        orderList.add(new AdminOrder("#SA-9011", "Nguyễn An", "3 Items: Wool Scarf, T-Shirt...", "840.000 ₫", "SHIPPED", "1h ago"));
        orderList.add(new AdminOrder("#SA-9010", "Trần Thị B", "2 Items: Sun Glasses, Jacket", "2.100.000 ₫", "COMPLETED", "3h ago"));
        orderList.add(new AdminOrder("#SA-9009", "Phạm Văn C", "1 Item: Evening Gown", "3.500.000 ₫", "CANCELLED", "Yesterday"));

        adapter = new AdminOrderAdapter(orderList);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }
}
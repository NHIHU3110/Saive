package com.example.saive.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.InventoryAdapter;
import com.example.saive.models.Product;

import java.util.ArrayList;
import java.util.List;

public class InventoryManagementActivity extends AppCompatActivity {

    private RecyclerView rvInventory;
    private InventoryAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_management);

        setupHeader();
        setupList();
    }

    private void setupHeader() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddProduct).setOnClickListener(v -> 
            Toast.makeText(this, "Tính năng thêm sản phẩm", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupList() {
        rvInventory = findViewById(R.id.rvInventory);
        productList = new ArrayList<>();
        
        // Mock data
        productList.add(new Product("Structured Wool Coat", "1.200.000 ₫", R.mipmap.jacket1, "Jacket"));
        productList.add(new Product("Archive Parka", "2.100.000 ₫", R.mipmap.jacket2, "Jacket"));
        productList.add(new Product("Minimalist Bomber", "1.500.000 ₫", R.mipmap.jacket3, "Jacket"));
        productList.add(new Product("Classic Cotton T-Shirt", "350.000 ₫", R.mipmap.tshirt1, "T-Shirt"));

        adapter = new InventoryAdapter(productList, product -> {
            Toast.makeText(this, "Chỉnh sửa: " + product.getName(), Toast.LENGTH_SHORT).show();
        });

        rvInventory.setLayoutManager(new LinearLayoutManager(this));
        rvInventory.setAdapter(adapter);
    }
}
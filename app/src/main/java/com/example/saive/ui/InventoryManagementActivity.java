package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.InventoryAdapter;
import com.example.saive.adapters.BottomSheetOptionAdapter;
import com.example.saive.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.saive.base.BaseActivity;
public class InventoryManagementActivity extends BaseActivity {

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
        findViewById(R.id.btnAddProduct).setOnClickListener(v -> showAddProductDialog());
    }

    @android.annotation.SuppressLint("InflateParams")
    private void showAddProductDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        View btnSelectCategory = dialogView.findViewById(R.id.btnSelectCategory);
        TextView tvSelectedCategory = dialogView.findViewById(R.id.tvSelectedCategory);
        List<String> categories = Arrays.asList("Silk", "Linen", "Cotton", "Wool", "Denim", "Leather");

        btnSelectCategory.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
            View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null);
            bottomSheetDialog.setContentView(sheetView);

            TextView tvTitle = sheetView.findViewById(R.id.tvSheetTitle);
            tvTitle.setText(R.string.admin_select_category);

            RecyclerView rvOptions = sheetView.findViewById(R.id.rvSheetOptions);
            rvOptions.setLayoutManager(new LinearLayoutManager(this));

            BottomSheetOptionAdapter adapter = new BottomSheetOptionAdapter(categories, tvSelectedCategory.getText().toString(), option -> {
                tvSelectedCategory.setText(option);
                tvSelectedCategory.setTextColor(getResources().getColor(R.color.colorNoirBlack));
                bottomSheetDialog.dismiss();
            });
            rvOptions.setAdapter(adapter);
            bottomSheetDialog.show();
        });

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View btnSave = dialogView.findViewById(R.id.btnSaveProduct);
        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
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
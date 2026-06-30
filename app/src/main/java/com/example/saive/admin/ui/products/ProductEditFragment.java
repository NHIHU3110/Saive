package com.example.saive.admin.ui.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.saive.R;
import com.example.saive.admin.data.model.AdminProduct;
import com.example.saive.databinding.AdminFragmentProductEditBinding;
import com.example.saive.utils.ImageUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductEditFragment extends Fragment {
    private AdminFragmentProductEditBinding binding;
    private ProductsViewModel viewModel;
    private String productId;
    private java.util.Map<String, java.util.Map<String, Integer>> variantsStock = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AdminFragmentProductEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductsViewModel.class);

        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }

        setupToolbar();
        setupObservers();

        if (productId != null) {
            binding.toolbar.setTitle(R.string.address_edit_title);
            // In a real app, load product detail. Here we might need a ProductDetailViewModel 
            // but for simplicity we use the list viewModel if it had a getProductById
            // Let's assume we can fetch it.
            fetchProductData(productId);
        } else {
            binding.toolbar.setTitle(R.string.address_add_title);
            binding.btnDelete.setVisibility(View.GONE);
        }

        binding.btnSave.setOnClickListener(v -> saveProduct());
        binding.btnDelete.setOnClickListener(v -> deleteProduct());
        binding.btnAddVariant.setVisibility(View.VISIBLE);
        binding.btnAddVariant.setOnClickListener(v -> showAddVariantDialog());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }

    private void fetchProductData(String id) {
        // Simple mock/fetch logic
        com.example.saive.admin.data.repository.ProductRepository repo = new com.example.saive.admin.data.repository.ProductRepository(requireContext());
        repo.getProductById(id, new androidx.lifecycle.MutableLiveData<AdminProduct>() {{
            observe(getViewLifecycleOwner(), product -> {
                if (product != null) bindProductData(product);
            });
        }}, new androidx.lifecycle.MutableLiveData<>());
    }

    private void bindProductData(AdminProduct product) {
        binding.etName.setText(product.getProductName());
        binding.etCategory.setText(product.getCategoryId());
        binding.etOriginalPrice.setText(String.valueOf(product.getOriginalPrice()));
        binding.etSalePrice.setText(String.valueOf(product.getPrice()));
        binding.etStock.setText(String.valueOf(product.getStockQuantity()));
        binding.swActive.setChecked(product.isActive());
        binding.swFeatured.setChecked(product.isFeatured());
        ImageUtils.setSafeImage(binding.ivProduct, product.getFirstImage(), R.drawable.model1);
        
        this.variantsStock = product.getVariantsStock() != null ? product.getVariantsStock() : new HashMap<>();
        updateVariantsUI();
    }

    private void updateVariantsUI() {
        binding.layoutVariants.removeAllViews();
        
        if (variantsStock == null) return;

        for (Map.Entry<String, Map<String, Integer>> sizeEntry : variantsStock.entrySet()) {
            String size = sizeEntry.getKey();
            for (Map.Entry<String, Integer> colorEntry : sizeEntry.getValue().entrySet()) {
                String color = colorEntry.getKey();
                Object stockObj = colorEntry.getValue();
                int stock = (stockObj instanceof Number) ? ((Number) stockObj).intValue() : 0;
                addVariantRow(size, color, stock);
            }
        }
    }

    private void addVariantRow(String size, String color, int stock) {
        View row = getLayoutInflater().inflate(R.layout.admin_item_variant_stock, binding.layoutVariants, false);
        ((android.widget.TextView) row.findViewById(R.id.tvVariantName)).setText(size + " / " + color);
        android.widget.EditText etStock = row.findViewById(R.id.etVariantStock);
        etStock.setText(String.valueOf(stock));
        
        etStock.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                try {
                    int newStock = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    if (variantsStock == null) variantsStock = new HashMap<>();
                    if (!variantsStock.containsKey(size)) {
                        variantsStock.put(size, new HashMap<>());
                    }
                    variantsStock.get(size).put(color, newStock);
                    updateTotalStock();
                } catch (NumberFormatException ignored) {}
            }
        });

        View btnRemove = row.findViewById(R.id.btnRemoveVariant);
        btnRemove.setVisibility(View.VISIBLE);
        btnRemove.setOnClickListener(v -> {
            if (variantsStock != null && variantsStock.containsKey(size)) {
                variantsStock.get(size).remove(color);
                if (variantsStock.get(size).isEmpty()) {
                    variantsStock.remove(size);
                }
                updateVariantsUI();
                updateTotalStock();
            }
        });

        binding.layoutVariants.addView(row);
    }

    private void updateTotalStock() {
        int total = 0;
        for (Map<String, Integer> colors : variantsStock.values()) {
            for (Object stockObj : colors.values()) {
                if (stockObj instanceof Number) {
                    total += ((Number) stockObj).intValue();
                }
            }
        }
        binding.etStock.setText(String.valueOf(total));
    }

    private void showAddVariantDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.admin_dialog_add_variant, null);
        android.widget.EditText etSize = dialogView.findViewById(R.id.etSize);
        android.widget.EditText etColor = dialogView.findViewById(R.id.etColor);
        android.widget.EditText etStock = dialogView.findViewById(R.id.etStock);

        new AlertDialog.Builder(requireContext())
                .setTitle("Thêm biến thể mới")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String size = etSize.getText().toString().trim();
                    String color = etColor.getText().toString().trim();
                    String stockStr = etStock.getText().toString().trim();
                    
                    if (size.isEmpty() || color.isEmpty() || stockStr.isEmpty()) return;
                    
                    int stock = Integer.parseInt(stockStr);
                    if (!variantsStock.containsKey(size)) {
                        variantsStock.put(size, new HashMap<>());
                    }
                    variantsStock.get(size).put(color, stock);
                    updateVariantsUI();
                    updateTotalStock();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveProduct() {
        Map<String, Object> updates = new HashMap<>();
        String name = binding.etName.getText().toString();
        String category = binding.etCategory.getText().toString();

        updates.put("ProductName", name);
        updates.put("CategoryId", category);
        updates.put("category", category); // Mobile app uses 'category'

        try {
            updates.put("OriginalPrice", Double.parseDouble(binding.etOriginalPrice.getText().toString()));
            updates.put("Price", Double.parseDouble(binding.etSalePrice.getText().toString()));
            updates.put("StockQuantity", Integer.parseInt(binding.etStock.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Vui lòng nhập đúng định dạng số", Toast.LENGTH_SHORT).show();
            return;
        }
        updates.put("IsActive", binding.swActive.isChecked());
        updates.put("IsFeatured", binding.swFeatured.isChecked());
        
        // Normalize variantsStock keys before saving
        Map<String, Map<String, Integer>> normalizedStock = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> sizeEntry : variantsStock.entrySet()) {
            String sizeKey = sizeEntry.getKey().toUpperCase().trim();
            Map<String, Integer> colorMap = sizeEntry.getValue();
            Map<String, Integer> normalizedColorMap = new HashMap<>();
            
            for (Map.Entry<String, Integer> colorEntry : colorMap.entrySet()) {
                String color = colorEntry.getKey().trim();
                String normalizedColor = color.isEmpty() ? "" : 
                    color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase();
                normalizedColorMap.put(normalizedColor, colorEntry.getValue());
            }
            normalizedStock.put(sizeKey, normalizedColorMap);
        }
        updates.put("Stock", normalizedStock);

        // Derive tag_color from normalizedStock
        List<String> derivedColors = new ArrayList<>();
        for (Map<String, Integer> colors : normalizedStock.values()) {
            for (String color : colors.keySet()) {
                if (!derivedColors.contains(color)) {
                    derivedColors.add(color);
                }
            }
        }
        if (!derivedColors.isEmpty()) {
            updates.put("tag_color", derivedColors);
        }

        // Auto-assign tag_type_group based on category for better Mobile UI rendering
        String categoryLower = category.toLowerCase();
        String typeGroup = "accessory";
        if (categoryLower.contains("shirt") || categoryLower.contains("top") || categoryLower.contains("áo")) typeGroup = "top";
        else if (categoryLower.contains("pant") || categoryLower.contains("jean") || categoryLower.contains("quần")) typeGroup = "bottom";
        else if (categoryLower.contains("dress") || categoryLower.contains("váy")) typeGroup = "dress";
        else if (categoryLower.contains("shoe") || categoryLower.contains("giày")) typeGroup = "shoes";
        else if (categoryLower.contains("bag") || categoryLower.contains("túi")) typeGroup = "bag";
        
        updates.put("tag_type_group", typeGroup);
        updates.put("tag_type", category);

        com.example.saive.admin.data.repository.ProductRepository repo = new com.example.saive.admin.data.repository.ProductRepository(requireContext());
        if (productId != null) {
            repo.updateProduct(productId, updates, new androidx.lifecycle.MutableLiveData<>(), new androidx.lifecycle.MutableLiveData<>());
            Toast.makeText(getContext(), R.string.toast_update_success, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack();
        } else {
            updates.put("CreatedAt", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).format(new java.util.Date()));
            updates.put("NumBuy", 0);
            updates.put("Rating", 5.0);
            
            repo.addProduct(updates, new androidx.lifecycle.MutableLiveData<String>() {{
                observe(getViewLifecycleOwner(), id -> {
                    Toast.makeText(getContext(), "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                });
            }}, new androidx.lifecycle.MutableLiveData<String>() {{
                observe(getViewLifecycleOwner(), err -> {
                    Toast.makeText(getContext(), "Lỗi: " + err, Toast.LENGTH_SHORT).show();
                });
            }});
        }
    }

    private void deleteProduct() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton(R.string.dialog_delete_confirm, (dialog, which) -> {
                    com.example.saive.admin.data.repository.ProductRepository repo = new com.example.saive.admin.data.repository.ProductRepository(requireContext());
                    repo.deleteProduct(productId, new androidx.lifecycle.MutableLiveData<>(), new androidx.lifecycle.MutableLiveData<>());
                    Toast.makeText(getContext(), "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    private void setupObservers() {
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
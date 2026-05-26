package com.example.saive.adapters;

import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.saive.utils.ToastUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.models.Product;
import com.example.saive.ui.ProductDetailActivity;
import com.example.saive.utils.CartManager;

import java.util.List;

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ViewHolder> {

    private List<Product> products;
    private Integer textColor = null; // Dùng Integer để tránh xung đột với Color.WHITE (-1)

    public ProductGridAdapter(List<Product> products) {
        this.products = products;
    }

    public void setTextColor(int color) {
        this.textColor = color;
        notifyDataSetChanged();
    }

    public void updateData(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        
        try {
            holder.ivProduct.setImageResource(product.getImageResId());
        } catch (Exception e) {
            holder.ivProduct.setImageResource(R.drawable.ic_cart);
        }

        holder.tvName.setText(product.getName().toUpperCase());
        holder.tvPrice.setText(product.getPrice());

        if (textColor != null) {
            holder.tvName.setTextColor(textColor);
            holder.tvPrice.setTextColor(textColor);
            holder.tvPrice.setAlpha(1.0f); // Đảm bảo giá tiền luôn nổi bật nhất
            holder.btnAddToCart.setColorFilter(textColor);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT", product);
            v.getContext().startActivity(intent);
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            CartManager.getInstance(v.getContext()).addProduct(product);
            ToastUtils.showCustomToast(v.getContext(), "Added to wardrobe");
        });

        holder.btnFavorite.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            ToastUtils.showCustomToast(v.getContext(), "Saved to favorites");
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice;
        ImageButton btnAddToCart, btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}

package com.example.saive.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;
import com.example.saive.R;
import com.example.saive.models.Product;
import com.example.saive.ui.ProductDetailActivity;

import java.util.ArrayList;
import com.example.saive.utils.ToastUtils;
import java.util.List;

public class FlashSaleGridAdapter extends RecyclerView.Adapter<FlashSaleGridAdapter.ViewHolder> {

    private List<Product> productList;

    public FlashSaleGridAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flash_sale_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName().toUpperCase());
        holder.tvPrice.setText(product.getPrice());
        
        // Discount badge mock logic (could be added to Product model later)
        if (position % 2 == 0) {
            holder.tvDiscount.setText("-40%");
        } else {
            holder.tvDiscount.setText("-25%");
        }
        holder.tvDiscount.setVisibility(View.VISIBLE);

        try {
            holder.ivProduct.setImageResource(product.getImageResId());
        } catch (Exception e) {
            holder.ivProduct.setImageResource(R.mipmap.model1);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT", product);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvDiscount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvDiscount = itemView.findViewById(R.id.tvDiscountBadge);
        }
    }
}

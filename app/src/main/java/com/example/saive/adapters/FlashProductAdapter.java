package com.example.saive.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.saive.utils.ToastUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.models.Product;
import com.example.saive.ui.ProductDetailActivity;
import com.example.saive.utils.CartManager;

import java.util.List;

public class FlashProductAdapter extends RecyclerView.Adapter<FlashProductAdapter.ViewHolder> {

    private List<Product> productList;

    public FlashProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flash_sale_product, parent, false);
        // Set width for horizontal scrolling
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = (int) (parent.getContext().getResources().getDisplayMetrics().widthPixels * 0.45);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        
        try {
            holder.ivProduct.setImageResource(product.getImageResId());
        } catch (Exception e) {
            holder.ivProduct.setImageResource(R.drawable.ic_cart);
        }

        holder.tvName.setText(product.getName().toUpperCase());
        holder.tvPrice.setText(product.getPrice());

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
        TextView tvName, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}

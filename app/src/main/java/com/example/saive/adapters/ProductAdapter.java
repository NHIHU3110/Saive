package com.example.saive.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import com.example.saive.utils.ToastUtils;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.models.Product;
import com.example.saive.ui.ProductDetailActivity;
import com.example.saive.utils.CartManager;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> products;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        
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

        holder.btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance(v.getContext()).addProduct(product);
            ToastUtils.showCustomToast(v.getContext(), "Added to cart");
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice;
        ImageButton btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}

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

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<Product> favoritesList;
    private OnFavoriteRemoveListener removeListener;

    public interface OnFavoriteRemoveListener {
        void onRemove(int position);
    }

    public FavoriteAdapter(List<Product> favoritesList, OnFavoriteRemoveListener removeListener) {
        this.favoritesList = favoritesList;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Product product = favoritesList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvCategory.setText(product.getCategory());
        
        try {
            holder.ivProduct.setImageResource(product.getImageResId());
        } catch (Exception e) {
            holder.ivProduct.setImageResource(R.drawable.ic_cart); // Ảnh mặc định nếu ID sai
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT", product);
            v.getContext().startActivity(intent);
        });

        holder.btnRemoveFavorite.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            if (removeListener != null) {
                removeListener.onRemove(holder.getAdapterPosition());
            }
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            CartManager.getInstance(v.getContext()).addProduct(product);
            ToastUtils.showCustomToast(v.getContext(), "Added to wardrobe");
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvCategory;
        ImageButton btnRemoveFavorite, btnAddToCart;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}

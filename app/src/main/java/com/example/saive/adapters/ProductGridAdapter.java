package com.example.saive.adapters;

import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.saive.utils.FavoriteManager;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.models.Product;
import com.example.saive.ui.ProductDetailActivity;
import com.example.saive.utils.CartManager;
import com.example.saive.utils.PriceFormatter;
import com.example.saive.utils.ImageUtils;
import com.example.saive.utils.ToastUtils;

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
        
        ImageUtils.setSafeImage(holder.ivProduct, product.getImageResId());

        holder.tvName.setText(product.getName().toUpperCase());
        holder.tvPrice.setText(PriceFormatter.formatPrice(product.getPrice()));

        // Local quantity state for the item card
        final int[] itemQuantity = {1};
        holder.tvQuantity.setText("1");

        if (textColor != null) {
            holder.tvName.setTextColor(textColor);
            holder.tvPrice.setTextColor(textColor);
            holder.tvPrice.setAlpha(1.0f); // Đảm bảo giá tiền luôn nổi bật nhất
            holder.btnAddToCart.setColorFilter(textColor);
            holder.btnDecrease.setColorFilter(textColor);
            holder.btnIncrease.setColorFilter(textColor);
            holder.tvQuantity.setTextColor(textColor);
            holder.tvName.setAlpha(1.0f);
            holder.tvPrice.setAlpha(0.7f);
        } else {
            // Default colors if not set
            holder.tvName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorNoirBlack));
            holder.tvPrice.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorGrayText));
            holder.btnAddToCart.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorMaroon));
            holder.btnDecrease.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorNoirBlack));
            holder.btnIncrease.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorNoirBlack));
            holder.tvQuantity.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorNoirBlack));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT", product);
            v.getContext().startActivity(intent);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (itemQuantity[0] > 1) {
                itemQuantity[0]--;
                holder.tvQuantity.setText(String.valueOf(itemQuantity[0]));
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            itemQuantity[0]++;
            holder.tvQuantity.setText(String.valueOf(itemQuantity[0]));
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            CartManager.getInstance(v.getContext()).addProduct(product, itemQuantity[0]);
            String toastMsg = v.getContext().getString(R.string.toast_added_to_wardrobe, itemQuantity[0]);
            ToastUtils.showCustomToast(v.getContext(), toastMsg);
            
            // Reset quantity after adding
            itemQuantity[0] = 1;
            holder.tvQuantity.setText("1");
        });

        boolean isFavorite = FavoriteManager.getInstance(holder.itemView.getContext()).isFavorite(product);
        updateFavoriteIcon(holder.btnFavorite, isFavorite);

        holder.btnFavorite.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            FavoriteManager favoriteManager = FavoriteManager.getInstance(v.getContext());
            boolean newState = !favoriteManager.isFavorite(product);
            if (newState) {
                favoriteManager.addFavorite(product);
                ToastUtils.showCustomToast(v.getContext(), v.getContext().getString(R.string.toast_added_favorites));
            } else {
                favoriteManager.removeFavorite(product);
                ToastUtils.showCustomToast(v.getContext(), v.getContext().getString(R.string.toast_removed_favorites));
            }
            updateFavoriteIcon(holder.btnFavorite, newState);
        });
    }

    private void updateFavoriteIcon(ImageButton btn, boolean isFavorite) {
        if (isFavorite) {
            btn.setImageResource(R.drawable.ic_favorite); // Assume ic_favorite is filled heart
            btn.setColorFilter(ContextCompat.getColor(btn.getContext(), R.color.colorMaroon));
        } else {
            btn.setImageResource(R.drawable.ic_favorite); // In some apps they use different icons, 
                                                           // but based on prompt "white to red", we use same icon with tint
            btn.setColorFilter(ContextCompat.getColor(btn.getContext(), R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnAddToCart, btnFavorite, btnDecrease, btnIncrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }
    }
}

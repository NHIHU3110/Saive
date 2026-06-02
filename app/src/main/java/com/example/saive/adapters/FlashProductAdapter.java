package com.example.saive.adapters;

import android.content.Intent;
import android.view.HapticFeedbackConstants;
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
import com.example.saive.utils.ImageUtils;
import com.example.saive.utils.FavoriteManager;
import androidx.core.content.ContextCompat;

import java.util.List;

public class FlashProductAdapter extends RecyclerView.Adapter<FlashProductAdapter.ViewHolder> {

    private List<Product> productList;
    private int textColor = -1;

    public FlashProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void setTextColor(int color) {
        this.textColor = color;
        notifyDataSetChanged();
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
        
        ImageUtils.setSafeImage(holder.ivProduct, product.getImageResId());

        holder.tvName.setText(product.getName().toUpperCase());
        holder.tvPrice.setText(product.getPrice());

        if (textColor != -1) {
            holder.tvName.setTextColor(textColor);
            holder.tvPrice.setTextColor(textColor);
            holder.tvQuantity.setTextColor(textColor);
            holder.btnDecrease.setColorFilter(textColor);
            holder.btnIncrease.setColorFilter(textColor);
            holder.btnAddToCart.setColorFilter(textColor);
        }

        // Local quantity state for the item card
        final int[] itemQuantity = {1};
        holder.tvQuantity.setText("1");

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
            ToastUtils.showCustomToast(v.getContext(), "Added " + itemQuantity[0] + " to wardrobe");
            
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT", product);
            v.getContext().startActivity(intent);
        });
    }

    private void updateFavoriteIcon(ImageButton btn, boolean isFavorite) {
        if (isFavorite) {
            btn.setColorFilter(ContextCompat.getColor(btn.getContext(), R.color.colorMaroon));
        } else {
            btn.setColorFilter(ContextCompat.getColor(btn.getContext(), R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnFavorite, btnDecrease, btnIncrease, btnAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}

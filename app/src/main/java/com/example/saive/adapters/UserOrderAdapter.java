package com.example.saive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.models.AdminOrder;
import com.example.saive.utils.ImageUtils;
import java.util.List;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.OrderViewHolder> {

    private List<AdminOrder> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(AdminOrder order);
        void onActionClick(AdminOrder order);
    }

    public UserOrderAdapter(List<AdminOrder> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        AdminOrder order = orderList.get(position);
        holder.tvTotal.setText(order.getTotalAmount());
        
        // Set action button text based on status
        String status = order.getStatus() != null ? order.getStatus().toUpperCase() : "PENDING";
        if (status.equals("COMPLETED")) {
            holder.btnAction.setText(R.string.btn_leave_review);
        } else if (status.equals("CANCELLED")) {
            holder.btnAction.setText(R.string.btn_reorder);
        } else {
            holder.btnAction.setText(R.string.btn_track_order);
        }

        // Populate items
        holder.itemsContainer.removeAllViews();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            List<com.example.saive.models.OrderItem> items = order.getItems();
            LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
            
            for (int i = 0; i < items.size(); i++) {
                com.example.saive.models.OrderItem item = items.get(i);
                View itemView = inflater.inflate(R.layout.item_order_detail, holder.itemsContainer, false);
                
                ImageView ivItem = itemView.findViewById(R.id.ivItemImage);
                TextView tvName = itemView.findViewById(R.id.tvItemName);
                TextView tvAttributes = itemView.findViewById(R.id.tvItemAttributes);
                TextView tvPrice = itemView.findViewById(R.id.tvItemPrice);

                tvName.setText(item.getName());
                tvPrice.setText(item.getPrice());
                tvAttributes.setText("Size: " + item.getSize() + " | Qty: " + item.getQuantity());
                ivItem.setImageResource(item.getImageResId() != 0 ? item.getImageResId() : R.mipmap.model1);

                if (i > 0) {
                    itemView.setVisibility(View.GONE);
                }
                holder.itemsContainer.addView(itemView);
            }

            if (items.size() > 1) {
                holder.tvSeeMore.setVisibility(View.VISIBLE);
                holder.tvSeeMore.setText("Xem thêm (+" + (items.size() - 1) + " sản phẩm)");
                holder.tvSeeMore.setOnClickListener(v -> {
                    holder.tvSeeMore.setVisibility(View.GONE);
                    for (int i = 1; i < holder.itemsContainer.getChildCount(); i++) {
                        holder.itemsContainer.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                });
            } else {
                holder.tvSeeMore.setVisibility(View.GONE);
            }
        } else {
            // Fallback for legacy orders
            holder.tvSeeMore.setVisibility(View.GONE);
            View itemView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_order_detail, holder.itemsContainer, false);
            ImageView ivItem = itemView.findViewById(R.id.ivItemImage);
            TextView tvName = itemView.findViewById(R.id.tvItemName);
            TextView tvAttributes = itemView.findViewById(R.id.tvItemAttributes);
            TextView tvPrice = itemView.findViewById(R.id.tvItemPrice);

            tvName.setText(order.getItemsSummary());
            tvPrice.setText(order.getTotalAmount());
            tvAttributes.setText("Size: " + order.getSize() + " | Qty: " + order.getQuantity());
            ivItem.setImageResource(order.getProductImageResId() != 0 ? order.getProductImageResId() : R.mipmap.model1);
            holder.itemsContainer.addView(itemView);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });

        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActionClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTotal, tvSeeMore;
        android.widget.LinearLayout itemsContainer;
        Button btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTotal = itemView.findViewById(R.id.tvUserOrderTotal);
            tvSeeMore = itemView.findViewById(R.id.tvSeeMore);
            itemsContainer = itemView.findViewById(R.id.itemsContainer);
            btnAction = itemView.findViewById(R.id.btnOrderAction);
        }
    }
}
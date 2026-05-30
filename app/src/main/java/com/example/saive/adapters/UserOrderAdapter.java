package com.example.saive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.models.AdminOrder;
import java.util.List;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.OrderViewHolder> {

    private List<AdminOrder> orderList;

    public UserOrderAdapter(List<AdminOrder> orderList) {
        this.orderList = orderList;
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
        holder.tvId.setText(order.getOrderId());
        holder.tvDate.setText(order.getTimeAgo());
        holder.tvItems.setText(order.getItemsSummary());
        holder.tvTotal.setText(order.getTotalAmount());
        holder.tvStatus.setText(order.getStatus());

        if ("PENDING".equals(order.getStatus())) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#810100"));
        } else if ("SHIPPED".equals(order.getStatus())) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
        } else {
            holder.tvStatus.setTextColor(android.graphics.Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvDate, tvItems, tvTotal, tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvUserOrderId);
            tvDate = itemView.findViewById(R.id.tvUserOrderDate);
            tvItems = itemView.findViewById(R.id.tvUserOrderItems);
            tvTotal = itemView.findViewById(R.id.tvUserOrderTotal);
            tvStatus = itemView.findViewById(R.id.tvUserOrderStatus);
        }
    }
}
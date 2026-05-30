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

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    private List<AdminOrder> orderList;

    public AdminOrderAdapter(List<AdminOrder> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminOrder order = orderList.get(position);
        holder.tvOrderId.setText(order.getOrderId());
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvOrderItems.setText(order.getItemsSummary());
        holder.tvOrderTotal.setText(order.getTotalAmount());
        holder.tvStatus.setText(order.getStatus());
        
        // Optional: Change status background/text color based on status string
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvOrderItems, tvOrderTotal, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}
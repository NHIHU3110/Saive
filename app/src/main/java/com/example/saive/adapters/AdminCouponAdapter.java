package com.example.saive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.models.Coupon;

import java.util.List;

public class AdminCouponAdapter extends RecyclerView.Adapter<AdminCouponAdapter.CouponViewHolder> {

    private List<Coupon> couponList;

    public AdminCouponAdapter(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_admin, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);
        holder.tvCouponCode.setText(coupon.getCode());
        holder.tvCouponDesc.setText(coupon.getDescription());
        holder.tvCouponExpiry.setText(holder.itemView.getContext().getString(R.string.expires_format, coupon.getExpiryDate()));
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView tvCouponCode, tvCouponDesc, tvCouponExpiry;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCouponCode = itemView.findViewById(R.id.tvCouponCode);
            tvCouponDesc = itemView.findViewById(R.id.tvCouponDesc);
            tvCouponExpiry = itemView.findViewById(R.id.tvCouponExpiry);
        }
    }
}

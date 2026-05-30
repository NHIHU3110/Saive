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
        holder.tvUsageCount.setText(holder.itemView.getContext().getString(R.string.admin_voucher_used, coupon.getUsageCount()));
        
        holder.tvVoucherStatus.setText(coupon.getStatus());
        if ("Active".equalsIgnoreCase(coupon.getStatus())) {
            holder.tvVoucherStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(holder.itemView.getContext().getColor(R.color.colorMaroon)));
        } else {
            holder.tvVoucherStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(holder.itemView.getContext().getColor(R.color.colorSand)));
        }
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView tvCouponCode, tvCouponDesc, tvCouponExpiry, tvUsageCount, tvVoucherStatus;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCouponCode = itemView.findViewById(R.id.tvCouponCode);
            tvCouponDesc = itemView.findViewById(R.id.tvCouponDesc);
            tvCouponExpiry = itemView.findViewById(R.id.tvCouponExpiry);
            tvUsageCount = itemView.findViewById(R.id.tvUsageCount);
            tvVoucherStatus = itemView.findViewById(R.id.tvVoucherStatus);
        }
    }
}

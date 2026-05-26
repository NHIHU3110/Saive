package com.example.saive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.models.WardrobeBanner;
import java.util.List;

public class WardrobeBannerAdapter extends RecyclerView.Adapter<WardrobeBannerAdapter.ViewHolder> {

    private List<WardrobeBanner> banners;

    public WardrobeBannerAdapter(List<WardrobeBanner> banners) {
        this.banners = banners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_wardrobe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WardrobeBanner banner = banners.get(position);
        holder.ivBannerImage.setImageResource(banner.getImageResId());
        holder.tvBannerCaption.setText(banner.getCaption());
        holder.tvBannerTitle.setText(banner.getTitle());
        holder.tvBannerAction.setText(banner.getAction());
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBannerImage;
        TextView tvBannerCaption, tvBannerTitle, tvBannerAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBannerImage = itemView.findViewById(R.id.ivBannerImage);
            tvBannerCaption = itemView.findViewById(R.id.tvBannerCaption);
            tvBannerTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvBannerAction = itemView.findViewById(R.id.tvBannerAction);
        }
    }
}
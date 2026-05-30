package com.example.saive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.models.Review;
import java.util.List;

public class AdminReviewAdapter extends RecyclerView.Adapter<AdminReviewAdapter.ViewHolder> {

    private List<Review> reviewList;
    private OnReviewActionListener listener;

    public interface OnReviewActionListener {
        void onApprove(Review review, int position);
        void onReject(Review review, int position);
    }

    public AdminReviewAdapter(List<Review> reviewList, OnReviewActionListener listener) {
        this.reviewList = reviewList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.tvUserName.setText(review.getUserName());
        holder.tvDate.setText(review.getDate());
        holder.ratingBar.setRating(review.getRating());
        holder.tvComment.setText(review.getComment());

        if (review.isApproved()) {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setText("HIDE");
        } else {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setText("REJECT");
        }

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(review, position));
        holder.btnReject.setOnClickListener(v -> listener.onReject(review, position));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDate, tvComment;
        RatingBar ratingBar;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvComment = itemView.findViewById(R.id.tvComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
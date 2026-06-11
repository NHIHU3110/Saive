package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.adapters.AdminReviewAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Review;
import com.example.saive.utils.DataManager;
import java.util.List;

@android.annotation.SuppressLint("NotifyDataSetChanged")
public class ReviewManagementActivity extends BaseActivity {

    private RecyclerView rvReviews;
    private AdminReviewAdapter adapter;
    private List<Review> reviewList;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_management);

        initViews();
        loadReviews();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        rvReviews = findViewById(R.id.rvReviews);
        tvEmpty = findViewById(R.id.tvEmpty);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadReviews() {
        reviewList = DataManager.getInstance(this).getReviews();
        
        // Add sample data if empty for demo purposes
        if (reviewList.isEmpty()) {
            reviewList.add(new Review("Classic Trench Coat", "Alice Johnson", 5.0f, "Absolutely stunning design! The quality is top-notch.", "12 Oct 2023", null));
            reviewList.add(new Review("Modern Blazer", "Mark Spencer", 4.0f, "Great fit, but the delivery was a bit slow.", "15 Oct 2023", null));
            reviewList.add(new Review("Silk Dress", "Sophie Chen", 2.0f, "Color doesn't match the picture well.", "20 Oct 2023", null));
            DataManager.getInstance(this).saveReviews(reviewList);
        }

        adapter = new AdminReviewAdapter(reviewList, new AdminReviewAdapter.OnReviewActionListener() {
            @Override
            public void onApprove(Review review, int position) {
                review.setApproved(true);
                DataManager.getInstance(ReviewManagementActivity.this).saveReviews(reviewList);
                adapter.notifyItemChanged(position);
                Toast.makeText(ReviewManagementActivity.this, "Review Approved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReject(Review review, int position) {
                if (review.isApproved()) {
                    review.setApproved(false);
                    Toast.makeText(ReviewManagementActivity.this, "Review Hidden", Toast.LENGTH_SHORT).show();
                } else {
                    reviewList.remove(position);
                    Toast.makeText(ReviewManagementActivity.this, "Review Rejected", Toast.LENGTH_SHORT).show();
                }
                DataManager.getInstance(ReviewManagementActivity.this).saveReviews(reviewList);
                adapter.notifyDataSetChanged();
                checkEmptyState();
            }
        });

        rvReviews.setAdapter(adapter);
        checkEmptyState();
    }

    private void checkEmptyState() {
        if (reviewList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvReviews.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvReviews.setVisibility(View.VISIBLE);
        }
    }
}
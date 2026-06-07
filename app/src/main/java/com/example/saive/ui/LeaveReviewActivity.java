package com.example.saive.ui;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Review;
import com.example.saive.utils.DataManager;
import com.example.saive.utils.ToastUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LeaveReviewActivity extends BaseActivity {

    private String productName;
    private String orderPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(android.graphics.Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        productName = getIntent().getStringExtra("productName");
        orderPrice = getIntent().getStringExtra("orderPrice");

        setupUI();
    }

    private void setupUI() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        TextView tvName = findViewById(R.id.tvProductName);
        TextView tvPrice = findViewById(R.id.tvProductPrice);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText etComment = findViewById(R.id.etComment);

        if (productName != null) tvName.setText(productName);
        if (orderPrice != null) tvPrice.setText(orderPrice);

        findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            
            float rating = ratingBar.getRating();
            String comment = etComment.getText().toString().trim();

            if (rating == 0) {
                ToastUtils.showCustomToast(this, "Please select a rating");
                return;
            }

            if (comment.isEmpty()) {
                ToastUtils.showCustomToast(this, "Please enter your review");
                return;
            }

            // Save the review
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            Review newReview = new Review(productName, "You", rating, comment, currentDate, new ArrayList<>());
            
            DataManager.getInstance(this).addReview(newReview);

            ToastUtils.showCustomToast(this, "Review submitted successfully!");
            finish();
        });

        findViewById(R.id.btnAddPhoto).setOnClickListener(v -> {
            ToastUtils.showCustomToast(this, "Photo upload feature coming soon!");
        });
    }
}

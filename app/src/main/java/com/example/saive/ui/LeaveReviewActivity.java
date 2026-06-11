package com.example.saive.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.SelectedPhotoAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Review;
import com.example.saive.utils.DataManager;
import com.example.saive.utils.ToastUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@android.annotation.SuppressLint("NotifyDataSetChanged")
public class LeaveReviewActivity extends BaseActivity {

    private String productName;
    private String orderPrice;
    private RecyclerView rvSelectedPhotos;
    private SelectedPhotoAdapter photoAdapter;
    private List<Uri> selectedPhotoUris = new ArrayList<>();

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}

                    selectedPhotoUris.add(uri);
                    updatePhotoList();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);



        productName = getIntent().getStringExtra("productName");
        orderPrice = getIntent().getStringExtra("orderPrice");

        setupUI();
        setupPhotoRecyclerView();
    }

    private void setupPhotoRecyclerView() {
        rvSelectedPhotos = findViewById(R.id.rvSelectedPhotos);
        rvSelectedPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photoAdapter = new SelectedPhotoAdapter(selectedPhotoUris, position -> {
            selectedPhotoUris.remove(position);
            updatePhotoList();
        });
        rvSelectedPhotos.setAdapter(photoAdapter);
    }

    private void updatePhotoList() {
        if (selectedPhotoUris.isEmpty()) {
            rvSelectedPhotos.setVisibility(View.GONE);
        } else {
            rvSelectedPhotos.setVisibility(View.VISIBLE);
            photoAdapter.notifyDataSetChanged();
        }
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
            List<String> imageUrls = new ArrayList<>();
            for (Uri uri : selectedPhotoUris) {
                imageUrls.add(uri.toString());
            }

            Review newReview = new Review(productName, "You", rating, comment, currentDate, imageUrls);
            
            DataManager.getInstance(this).addReview(newReview);

            ToastUtils.showCustomToast(this, getString(R.string.toast_review_success));
            finish();
        });

        findViewById(R.id.btnAddPhoto).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            pickImageLauncher.launch("image/*");
        });
    }
}

package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.adapters.BottomSheetOptionAdapter;
import com.example.saive.base.BaseActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import java.util.Arrays;
import java.util.List;

public class NotificationManagementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_management);

        setupWindowInsets();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnCreateNotification).setOnClickListener(v -> {
            showCreateNotificationDialog();
        });
    }

    private void showCreateNotificationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_notification, null);
        builder.setView(dialogView);

        View btnSelectCategory = dialogView.findViewById(R.id.btnSelectNotifyCategory);
        TextView tvSelectedCategory = dialogView.findViewById(R.id.tvSelectedNotifyCategory);
        List<String> categories = Arrays.asList("Promotions", "Orders", "System", "Update");

        btnSelectCategory.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
            View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null);
            bottomSheetDialog.setContentView(sheetView);

            TextView tvTitle = sheetView.findViewById(R.id.tvSheetTitle);
            tvTitle.setText("SELECT CATEGORY");

            RecyclerView rvOptions = sheetView.findViewById(R.id.rvSheetOptions);
            rvOptions.setLayoutManager(new LinearLayoutManager(this));

            BottomSheetOptionAdapter adapter = new BottomSheetOptionAdapter(categories, tvSelectedCategory.getText().toString(), option -> {
                tvSelectedCategory.setText(option);
                tvSelectedCategory.setTextColor(getResources().getColor(R.color.colorNoirBlack));
                bottomSheetDialog.dismiss();
            });
            rvOptions.setAdapter(adapter);
            bottomSheetDialog.show();
        });

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View btnSend = dialogView.findViewById(R.id.btnSendNotification);
        btnSend.setOnClickListener(v -> {
            String category = tvSelectedCategory.getText().toString();
            if (category.equals("Select Category")) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Notification broadcasted successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }
}
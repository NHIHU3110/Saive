package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saive.R;
import com.example.saive.adapters.BottomSheetOptionAdapter;
import com.example.saive.adapters.NotificationAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Notification;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationManagementActivity extends BaseActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<Notification> allNotifications = new ArrayList<>();
    private List<Notification> displayedNotifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_management);

        setupWindowInsets();
        initSampleData();
        setupRecyclerView();
        setupCategoryTabs();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnCreateNotification).setOnClickListener(v -> {
            showCreateNotificationDialog();
        });
    }

    private void initSampleData() {
        // Sample data for demonstration
        allNotifications.add(new Notification("1", "Summer Collection Launch", "The new summer linen collection is live.", "VIEW NOW", "10:30 AM", R.drawable.ic_heart_thin, getResources().getColor(R.color.colorMaroon), false, getResources().getColor(R.color.colorMaroon), Notification.Type.DROP));
        allNotifications.add(new Notification("2", "Order Delivered", "Your order #SV9928 has been delivered.", "TRACK ORDER", "Yesterday", R.drawable.ic_cart, getResources().getColor(R.color.colorSand), true, getResources().getColor(R.color.colorSand), Notification.Type.ORDER));
        allNotifications.add(new Notification("3", "System Alert", "App update available with new admin features.", "UPDATE", "2h ago", R.drawable.ic_menu, getResources().getColor(R.color.colorLinen), false, getResources().getColor(R.color.colorNoirBlack), Notification.Type.REMINDER));
        displayedNotifications.addAll(allNotifications);
    }

    private void setupRecyclerView() {
        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(displayedNotifications, () -> {
            // Callback for notification clicks if needed
        });
        rvNotifications.setAdapter(adapter);
    }

    private void setupCategoryTabs() {
        View scrollChild = findViewById(R.id.categoryTabsScroll);
        if (scrollChild instanceof android.widget.HorizontalScrollView) {
            LinearLayout tabsContainer = (LinearLayout) ((android.widget.HorizontalScrollView) scrollChild).getChildAt(0);
            for (int i = 0; i < tabsContainer.getChildCount(); i++) {
                View tab = tabsContainer.getChildAt(i);
                if (tab instanceof TextView) {
                    TextView tvTab = (TextView) tab;
                    tvTab.setOnClickListener(v -> {
                        updateTabStyles(tabsContainer, tvTab);
                        filterNotifications(tvTab.getText().toString());
                    });
                }
            }
        }
    }

    private void updateTabStyles(LinearLayout container, TextView selectedTab) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                if (tv == selectedTab) {
                    tv.setBackgroundResource(R.drawable.bg_rounded_black_12dp);
                    tv.setTextColor(getResources().getColor(R.color.white));
                } else {
                    tv.setBackgroundResource(R.drawable.bg_material_card);
                    tv.setTextColor(getResources().getColor(R.color.colorNoirBlack));
                }
            }
        }
    }

    private void filterNotifications(String category) {
        displayedNotifications.clear();
        if (category.equalsIgnoreCase("All")) {
            displayedNotifications.addAll(allNotifications);
        } else {
            Notification.Type targetType;
            if (category.equalsIgnoreCase("Promotions")) {
                targetType = Notification.Type.DROP;
            } else if (category.equalsIgnoreCase("Orders")) {
                targetType = Notification.Type.ORDER;
            } else {
                targetType = Notification.Type.REMINDER; // For System/Update
            }

            for (Notification n : allNotifications) {
                if (n.getType() == targetType) {
                    displayedNotifications.add(n);
                }
            }
        }
        adapter.notifyDataSetChanged();
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
            tvTitle.setText(R.string.label_select_category);

            RecyclerView rvOptions = sheetView.findViewById(R.id.rvSheetOptions);
            rvOptions.setLayoutManager(new LinearLayoutManager(this));

            List<String> translatedCategories = Arrays.asList(
                getString(R.string.cat_promotions),
                getString(R.string.cat_orders),
                getString(R.string.cat_system),
                getString(R.string.cat_update)
            );

            BottomSheetOptionAdapter sheetAdapter = new BottomSheetOptionAdapter(translatedCategories, tvSelectedCategory.getText().toString(), option -> {
                tvSelectedCategory.setText(option);
                tvSelectedCategory.setTextColor(getResources().getColor(R.color.colorNoirBlack));
                bottomSheetDialog.dismiss();
            });
            rvOptions.setAdapter(sheetAdapter);
            bottomSheetDialog.show();
        });

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View btnSend = dialogView.findViewById(R.id.btnSendNotification);
        btnSend.setOnClickListener(v -> {
            String category = tvSelectedCategory.getText().toString();
            if (category.equals(getString(R.string.label_select_category)) || category.equals("Select Category")) {
                Toast.makeText(this, R.string.error_select_category, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.toast_notification_broadcasted, Toast.LENGTH_SHORT).show();
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

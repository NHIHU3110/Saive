package com.example.saive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.saive.base.BaseActivity;
import com.example.saive.R;
import com.google.android.material.navigation.NavigationView;
import android.content.SharedPreferences;

public class AdminActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupWindowInsets();
        
        findViewById(R.id.btnMenu).setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        findViewById(R.id.btnPushNotification).setOnClickListener(v -> {
            showBroadcastDialog();
        });
    }

    private void showBroadcastDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_broadcast, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText etTitle = dialogView.findViewById(R.id.etNotifyTitle);
        EditText etMessage = dialogView.findViewById(R.id.etNotifyMessage);
        View btnSend = dialogView.findViewById(R.id.btnSendBroadcast);

        btnSend.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String message = etMessage.getText().toString();
            if (!title.isEmpty() && !message.isEmpty()) {
                Toast.makeText(this, "Broadcast sent: " + title, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminHeader), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Đã ở dashboard
        } else if (id == R.id.nav_inventory) {
            startActivity(new Intent(this, InventoryManagementActivity.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(this, OrderManagementActivity.class));
        } else if (id == R.id.nav_flash_sale) {
            startActivity(new Intent(this, FlashSaleManagementActivity.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(this, UserManagementActivity.class));
        } else if (id == R.id.nav_marketing) {
            startActivity(new Intent(this, MarketingManagementActivity.class));
        } else if (id == R.id.nav_reviews) {
            startActivity(new Intent(this, ReviewManagementActivity.class));
        } else if (id == R.id.nav_language) {
            showLanguageDialog();
        } else if (id == R.id.nav_logout) {
            logoutAdmin();
        } else {
            Toast.makeText(this, "Tính năng " + item.getTitle() + " đang phát triển", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLanguageDialog() {
        String[] languages = {getString(R.string.lang_en), getString(R.string.lang_vi), getString(R.string.lang_zh)};
        String[] codes = {"en", "vi", "zh"};

        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.menu_language)
                .setItems(languages, (dialog, which) -> {
                    updateLanguage(codes[which]);
                })
                .show();
    }

    private void updateLanguage(String langCode) {
        SharedPreferences.Editor editor = getSharedPreferences(LANG_PREFS, MODE_PRIVATE).edit();
        editor.putString(LANG_KEY, langCode);
        editor.apply();

        // Restart activity to apply changes
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void logoutAdmin() {
        // Quay về LoginActivity và xóa task cũ
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Không cho back ra ngoài trừ khi logout
            Toast.makeText(this, R.string.admin_logout_confirm, Toast.LENGTH_SHORT).show();
            // super.onBackPressed();
        }
    }
}
package com.example.saive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;

public class AdminLoginActivity extends BaseActivity {

    private EditText etEmail, etPassword;
    private View loadingOverlay;
    private int logoClickCount = 0;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        View btnLoginAdmin = findViewById(R.id.btnLoginAdmin);
        
        if (btnLoginAdmin != null) {
            btnLoginAdmin.setOnClickListener(v -> {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.equals("admin@saive.com") && password.equals("admin123")) {
                    performAdminLogin();
                } else {
                    Toast.makeText(this, "Invalid Admin Credentials", Toast.LENGTH_SHORT).show();
                }
            });
        }

        View ivLogo = findViewById(R.id.ivLogo);
        if (ivLogo != null) {
            ivLogo.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime > 2000) {
                    logoClickCount = 0;
                }
                lastClickTime = currentTime;
                logoClickCount++;

                if (logoClickCount == 5) {
                    logoClickCount = 0;
                    Intent intent = new Intent(AdminLoginActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void performAdminLogin() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
        
        // 1.5s delay as requested
        new Handler().postDelayed(() -> {
            if (loadingOverlay != null) {
                loadingOverlay.setVisibility(View.GONE);
            }
            Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1500);
    }
}
package com.example.saive.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.splashscreen.SplashScreen;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.example.saive.utils.DataManager;
import com.example.saive.utils.ImageUtils;

public class LoginActivity extends BaseActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private int logoClickCount = 0;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        View ivLogoView = findViewById(R.id.ivLogo);
        if (ivLogoView instanceof ImageView) {
            ImageUtils.setSafeImage((ImageView) ivLogoView, R.mipmap.saive_logo);
        }

        if (ivLogoView != null) {
            ivLogoView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 500) {
                    logoClickCount++;
                } else {
                    logoClickCount = 1;
                }
                lastClickTime = currentTime;

                if (logoClickCount >= 5) {
                    logoClickCount = 0;
                    Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, "Admin Mode Unlocked", Toast.LENGTH_SHORT).show();
                }
            });
        }

        ImageView ivFbIcon = findViewById(R.id.ivFbIcon);
        ImageView ivGgIcon = findViewById(R.id.ivGgIcon);
        ImageUtils.setSafeImage(ivFbIcon, R.mipmap.fbicon);
        ImageUtils.setSafeImage(ivGgIcon, R.mipmap.ggicon);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(androidx.core.content.ContextCompat.getColor(this, R.color.colorAuthBg));
            boolean isDarkMode = (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                    == android.content.res.Configuration.UI_MODE_NIGHT_YES;
            if (isDarkMode) {
                getWindow().getDecorView().setSystemUiVisibility(
                        getWindow().getDecorView().getSystemUiVisibility() | android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            } else {
                getWindow().getDecorView().setSystemUiVisibility(
                        getWindow().getDecorView().getSystemUiVisibility() & ~android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            }
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        View btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignUpLink = findViewById(R.id.tvSignUpLink);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        TextView tvContinueAsGuest = findViewById(R.id.tvContinueAsGuest);

        boolean returnResult = getIntent().getBooleanExtra("return_result", false);
        if (returnResult && tvContinueAsGuest != null) {
            tvContinueAsGuest.setVisibility(View.VISIBLE);
            tvContinueAsGuest.setOnClickListener(v -> {
                setResult(RESULT_OK);
                finish();
            });
        }

        loadSavedCredentials();

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                if (validateInput()) {
                    performLogin();
                }
            });
        }

        if (tvSignUpLink != null) {
            tvSignUpLink.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            });
        }

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                showCustomToast("Forgot password clicked");
            });
        }
    }

    private boolean validateInput() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return false;
        }

        return true;
    }

    private void loadSavedCredentials() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean("remember_me", false);
        if (rememberMe) {
            String savedEmail = prefs.getString("saved_email", "");
            String savedPassword = prefs.getString("saved_password", "");
            etEmail.setText(savedEmail);
            etPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);
        }
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        DataManager dataManager = DataManager.getInstance(this);
        if (dataManager.isUserBlocked(email)) {
            showCustomToast("Tài khoản bị khóa. Vui lòng liên hệ Admin.");
            return;
        }

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("user_email", "user@gmail.com");
        String savedPassword = prefs.getString("user_password", "user123");

        if (email.equals(savedEmail) && password.equals(savedPassword)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_logged_in", true);
            editor.putString("saved_email", email);
            editor.putString("saved_password", password);

            if (cbRememberMe.isChecked()) {
                editor.putBoolean("remember_me", true);
            } else {
                editor.putBoolean("remember_me", false);
            }

            editor.apply();
            showCustomToast("Login Successful");

            boolean returnResult = getIntent().getBooleanExtra("return_result", false);
            if (returnResult) {
                // Được gọi từ Checkout → trả kết quả về
                setResult(RESULT_OK);
                finish();
            } else {
                // Vào login trực tiếp từ Profile button → về MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        }
    }
}
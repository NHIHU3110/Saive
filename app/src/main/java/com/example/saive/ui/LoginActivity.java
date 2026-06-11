package com.example.saive.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.splashscreen.SplashScreen;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.example.saive.databinding.ActivityLoginBinding;
import com.example.saive.utils.DataManager;
import com.example.saive.utils.ImageUtils;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private int logoClickCount = 0;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageUtils.setSafeImage(binding.ivLogo, R.mipmap.saive_logo);

        binding.ivLogo.setOnClickListener(v -> {
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
                Toast.makeText(this, getString(R.string.login_admin_unlocked), Toast.LENGTH_SHORT).show();
            }
        });

        ImageUtils.setSafeImage(binding.ivFbIcon, R.mipmap.fbicon);
        ImageUtils.setSafeImage(binding.ivGgIcon, R.mipmap.ggicon);

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

        boolean returnResult = getIntent().getBooleanExtra("return_result", false);
        if (returnResult) {
            binding.tvContinueAsGuest.setVisibility(View.VISIBLE);
            binding.tvContinueAsGuest.setOnClickListener(v -> {
                setResult(RESULT_OK);
                finish();
            });
        }

        loadSavedCredentials();

        binding.btnLogin.setOnClickListener(v -> {
            if (validateInput()) {
                performLogin();
            }
        });

        binding.tvSignUpLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            showCustomToast(getString(R.string.login_forgot_pwd_clicked));
        });
    }

    private boolean validateInput() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError(getString(R.string.login_error_email_req));
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError(getString(R.string.login_error_email_invalid));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError(getString(R.string.login_error_pwd_req));
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
            binding.etEmail.setText(savedEmail);
            binding.etPassword.setText(savedPassword);
            binding.cbRememberMe.setChecked(true);
        }
    }

    private void performLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        DataManager dataManager = DataManager.getInstance(this);
        if (dataManager.isUserBlocked(email)) {
            showCustomToast(getString(R.string.login_error_acc_blocked));
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

            if (binding.cbRememberMe.isChecked()) {
                editor.putBoolean("remember_me", true);
            } else {
                editor.putBoolean("remember_me", false);
            }

            editor.apply();
            showCustomToast(getString(R.string.login_toast_success));

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
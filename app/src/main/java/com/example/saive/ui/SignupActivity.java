package com.example.saive.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;


import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.example.saive.databinding.ActivitySignupBinding;
import com.example.saive.utils.ImageUtils;

public class SignupActivity extends BaseActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageUtils.setSafeImage(binding.ivLogo, R.mipmap.saive_logo);
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

        binding.btnSignup.setOnClickListener(v -> {
            if (validateInput()) {
                performSignup();
            }
        });

        binding.tvTermsLink.setOnClickListener(v -> showTermsPopup());

        binding.tvLoginLink.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean validateInput() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.etName.setError(getString(R.string.error_name_required));
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError(getString(R.string.error_email_required));
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError(getString(R.string.error_invalid_email));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError(getString(R.string.error_password_required));
            return false;
        }

        if (password.length() < 6) {
            binding.etPassword.setError(getString(R.string.error_password_short));
            return false;
        }

        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError(getString(R.string.error_passwords_not_match));
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            binding.etPhone.setError(getString(R.string.error_phone_required));
            return false;
        }

        if (!binding.cbTerms.isChecked()) {
            showCustomToast(getString(R.string.error_agree_terms));
            return false;
        }

        return true;
    }

    private void showTermsPopup() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        
        com.example.saive.databinding.DialogTermsBinding dialogBinding = 
                com.example.saive.databinding.DialogTermsBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialogBinding.btnCloseTerms.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void performSignup() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String name = binding.etName.getText().toString().trim();

        // Lưu thông tin để có thể đăng nhập ở màn hình Login
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_email", email);
        editor.putString("user_password", password);
        editor.putString("user_name", name);
        editor.apply();

        showCustomToast(getString(R.string.toast_signup_success));
        
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
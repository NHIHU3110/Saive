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

import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.example.saive.utils.ImageUtils;

public class SignupActivity extends BaseActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword, etPhone;
    private CheckBox cbTerms;
    private TextView tvTermsLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        ImageView ivFbIcon = findViewById(R.id.ivFbIcon);
        ImageView ivGgIcon = findViewById(R.id.ivGgIcon);
        
        ImageUtils.setSafeImage(ivLogo, R.mipmap.saive_logo);
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

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        cbTerms = findViewById(R.id.cbTerms);
        tvTermsLink = findViewById(R.id.tvTermsLink);
        View btnSignup = findViewById(R.id.btnSignup);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> {
                if (validateInput()) {
                    performSignup();
                }
            });
        }

        if (tvTermsLink != null) {
            tvTermsLink.setOnClickListener(v -> showTermsPopup());
        }

        if (tvLoginLink != null) {
            tvLoginLink.setOnClickListener(v -> {
                finish();
            });
        }
    }

    private boolean validateInput() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.error_name_required));
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_email_required));
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_invalid_email));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_password_required));
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError(getString(R.string.error_password_short));
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.error_passwords_not_match));
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError(getString(R.string.error_phone_required));
            return false;
        }

        if (!cbTerms.isChecked()) {
            showCustomToast(getString(R.string.error_agree_terms));
            return false;
        }

        return true;
    }

    private void showTermsPopup() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_terms, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        View btnClose = dialogView.findViewById(R.id.btnCloseTerms);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private void performSignup() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();

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
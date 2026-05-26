package com.example.saive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;

public class SignupActivity extends BaseActivity {

    private EditText etName, etEmail, etPassword, etPhone;
    private CheckBox cbTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
            getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility() & ~android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        cbTerms = findViewById(R.id.cbTerms);
        View btnSignup = findViewById(R.id.btnSignup);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> {
                if (validateInput()) {
                    performSignup();
                }
            });
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
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return false;
        }

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

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            return false;
        }

        if (!cbTerms.isChecked()) {
            showCustomToast("Please agree to the Terms & Conditions");
            return false;
        }

        return true;
    }

    private void performSignup() {
        // TODO: Implement actual signup logic (Firebase/API)
        showCustomToast("Signing up...");
        
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
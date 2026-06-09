package com.example.saive.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends BaseActivity {

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Bind views
        tilCurrentPassword  = findViewById(R.id.tilCurrentPassword);
        tilNewPassword      = findViewById(R.id.tilNewPassword);
        tilConfirmPassword  = findViewById(R.id.tilConfirmPassword);
        etCurrentPassword   = findViewById(R.id.etCurrentPassword);
        etNewPassword       = findViewById(R.id.etNewPassword);
        etConfirmPassword   = findViewById(R.id.etConfirmPassword);

        ImageButton btnBack           = findViewById(R.id.btnBack);
        MaterialButton btnChangePassword = findViewById(R.id.btnChangePassword);
        TextView tvForgotPassword  = findViewById(R.id.tvForgotPassword);

        // Back
        btnBack.setOnClickListener(v -> finish());

        // Forgot Password → navigate to ForgotPasswordActivity
        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        // Change Password
        btnChangePassword.setOnClickListener(v -> {
            // Clear errors
            tilCurrentPassword.setError(null);
            tilNewPassword.setError(null);
            tilConfirmPassword.setError(null);

            String currentPass = etCurrentPassword.getText() != null
                    ? etCurrentPassword.getText().toString().trim() : "";
            String newPass = etNewPassword.getText() != null
                    ? etNewPassword.getText().toString().trim() : "";
            String confirmPass = etConfirmPassword.getText() != null
                    ? etConfirmPassword.getText().toString().trim() : "";

            // --- Validate current password ---
            if (TextUtils.isEmpty(currentPass)) {
                tilCurrentPassword.setError(getString(R.string.error_current_password_required));
                return;
            }

            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            String savedPassword = prefs.getString("user_password", "");

            if (!savedPassword.isEmpty() && !currentPass.equals(savedPassword)) {
                tilCurrentPassword.setError(getString(R.string.error_current_password_incorrect));
                return;
            }

            // --- Validate new password ---
            if (TextUtils.isEmpty(newPass)) {
                tilNewPassword.setError(getString(R.string.error_new_password_required));
                return;
            }
            if (newPass.length() < 6) {
                tilNewPassword.setError(getString(R.string.error_new_password_min));
                return;
            }
            if (newPass.equals(currentPass)) {
                tilNewPassword.setError(getString(R.string.error_new_password_same));
                return;
            }

            // --- Validate confirm password ---
            if (TextUtils.isEmpty(confirmPass)) {
                tilConfirmPassword.setError(getString(R.string.error_confirm_password_required));
                return;
            }
            if (!newPass.equals(confirmPass)) {
                tilConfirmPassword.setError(getString(R.string.error_passwords_not_match));
                return;
            }

            // --- Save new password ---
            prefs.edit().putString("user_password", newPass).apply();

            showCustomToast(getString(R.string.toast_password_changed));
            finish();
        });
    }
}
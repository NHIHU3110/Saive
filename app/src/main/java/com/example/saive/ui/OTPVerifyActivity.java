package com.example.saive.ui;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class OTPVerifyActivity extends BaseActivity {

    private TextInputEditText etOtp;
    private TextInputLayout tilOtp;
    private String correctOtp;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        correctOtp = getIntent().getStringExtra("otp");
        email = getIntent().getStringExtra("email");

        tilOtp = findViewById(R.id.tilOtp);
        etOtp = findViewById(R.id.etOtp);
        View btnVerify = findViewById(R.id.btnVerify);
        TextView tvEmailHint = findViewById(R.id.tvEmailHint);
        TextView tvResend = findViewById(R.id.tvResend);

        // Cập nhật hint hiển thị email nhận OTP
        if (email != null && !email.isEmpty()) {
            tvEmailHint.setText(getString(R.string.label_detail_verify_otp) + "\n" + email);
        }

        btnVerify.setOnClickListener(v -> {
            String entered = etOtp.getText() != null
                    ? etOtp.getText().toString().trim() : "";

            if (TextUtils.isEmpty(entered)) {
                tilOtp.setError("Please enter OTP");
                return;
            }
            if (entered.length() < 6) {
                tilOtp.setError("OTP must be 6 digits");
                return;
            }
            if (!entered.equals(correctOtp)) {
                tilOtp.setError("Invalid OTP");
                return;
            }

            tilOtp.setError(null);
            Intent intent = new Intent(OTPVerifyActivity.this, ResetPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        tvResend.setOnClickListener(v -> {
            String newOtp = String.valueOf((int)(Math.random() * 900000) + 100000);
            correctOtp = newOtp;
            tilOtp.setError(null);
            etOtp.setText("");
            showCustomToast("New OTP: " + newOtp);
        });
    }
}
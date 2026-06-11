package com.example.saive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.saive.R;
import com.google.android.material.card.MaterialCardView;

public class BankTransferActivity extends AppCompatActivity {

    private TextView tvExpiry, tvRegenerate;
    private MaterialCardView tvConfirm;
    private CountDownTimer countDownTimer;
    private static final long QR_DURATION_MS = 4 * 60 * 1000 + 57 * 1000; // 4:57

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bank_transfer);

        setupWindowInsets();
        initViews();
        startCountdown();
        setupListeners();
    }

    private void initViews() {
        tvExpiry = findViewById(R.id.tvExpiry);
        tvConfirm = findViewById(R.id.tvConfirm);
        tvRegenerate = findViewById(R.id.tvRegenerate);
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(QR_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvExpiry.setText(getString(R.string.desc_qr_code_expired_format, minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvExpiry.setText(getString(R.string.desc_qr_code_expired));
            }
        }.start();
    }

    private void setupListeners() {
        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Regenerate QR
        tvRegenerate.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            startCountdown();
        });

        // Confirm Payment
        tvConfirm.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            Intent intent = new Intent(BankTransferActivity.this, PaymentSuccessActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

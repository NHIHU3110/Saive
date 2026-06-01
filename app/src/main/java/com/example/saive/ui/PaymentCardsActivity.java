package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;

public class PaymentCardsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_cards);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Demo: chưa có data → hiện empty state
        View emptyState = findViewById(R.id.emptyState);
        View rvPaymentCards = findViewById(R.id.rvPaymentCards);
        emptyState.setVisibility(View.VISIBLE);
        rvPaymentCards.setVisibility(View.GONE);

        findViewById(R.id.fabAddCard).setOnClickListener(v ->
                showCustomToast("Add card — coming soon")
        );
    }
}

package com.example.saive.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.InventoryAdapter;
import com.example.saive.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FlashSaleManagementActivity extends AppCompatActivity {

    private TextView tvTimer;
    private RecyclerView rvFlashSale;
    private InventoryAdapter adapter;
    private List<Product> flashProducts;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_sale_management);

        tvTimer = findViewById(R.id.tvAdminTimer);
        rvFlashSale = findViewById(R.id.rvFlashSaleAdmin);

        setupHeader();
        setupTimer();
        setupList();
    }

    private void setupHeader() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCreateFlashSale).setOnClickListener(v -> 
            Toast.makeText(this, "Tạo chiến dịch Flash Sale mới", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupTimer() {
        long duration = TimeUnit.HOURS.toMillis(14) + TimeUnit.MINUTES.toMillis(20);
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d : %02d : %02d", hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00 : 00 : 00");
            }
        }.start();
    }

    private void setupList() {
        flashProducts = new ArrayList<>();
        flashProducts.add(new Product("Structured Wool Coat", "840.000 ₫", R.mipmap.jacket1, "Jacket"));
        flashProducts.add(new Product("Modern Aviators", "147.000 ₫", R.mipmap.sunglass1, "Sunglasses"));

        adapter = new InventoryAdapter(flashProducts, product -> {
            Toast.makeText(this, "Sửa Flash Sale cho: " + product.getName(), Toast.LENGTH_SHORT).show();
        });

        rvFlashSale.setLayoutManager(new LinearLayoutManager(this));
        rvFlashSale.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
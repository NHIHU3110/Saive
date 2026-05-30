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

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.example.saive.base.BaseActivity;
import com.example.saive.utils.DataManager;
import com.example.saive.utils.ToastUtils;

public class FlashSaleManagementActivity extends BaseActivity {

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
        findViewById(R.id.btnCreateFlashSale).setOnClickListener(v -> showCreateFlashSaleDialog());
    }

    private void showCreateFlashSaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.SaiveDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_flash_sale, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btnActivateFlashSale).setOnClickListener(v -> {
            String discount = ((EditText) view.findViewById(R.id.etFlashDiscount)).getText().toString();
            String durationStr = ((EditText) view.findViewById(R.id.etFlashDuration)).getText().toString();

            if (!discount.isEmpty() && !durationStr.isEmpty()) {
                long durationMillis = TimeUnit.HOURS.toMillis(Long.parseLong(durationStr));
                long endTime = System.currentTimeMillis() + durationMillis;
                
                DataManager.getInstance(this).setFlashSaleEndTime(endTime);
                
                // For demo, we sync current flash products to DataManager
                DataManager.getInstance(this).saveFlashSaleProducts(flashProducts);
                
                startNewTimer(durationMillis);
                dialog.dismiss();
                Toast.makeText(this, "Flash Sale Activated", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void startNewTimer(long durationMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(durationMillis, 1000) {
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
                DataManager.getInstance(FlashSaleManagementActivity.this).setFlashSaleEndTime(0);
            }
        }.start();
    }

    private void setupTimer() {
        long endTime = DataManager.getInstance(this).getFlashSaleEndTime();
        long currentTime = System.currentTimeMillis();
        
        if (endTime > currentTime) {
            long duration = endTime - currentTime;
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
        } else {
            tvTimer.setText("00 : 00 : 00");
        }
    }

    private void setupList() {
        flashProducts = DataManager.getInstance(this).getFlashSaleProducts();
        if (flashProducts.isEmpty()) {
            flashProducts.add(new Product("Structured Wool Coat", "840.000 ₫", R.mipmap.jacket1, "Jacket"));
            flashProducts.add(new Product("Modern Aviators", "147.000 ₫", R.mipmap.sunglass1, "Sunglasses"));
            DataManager.getInstance(this).saveFlashSaleProducts(flashProducts);
        }

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
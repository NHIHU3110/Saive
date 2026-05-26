package com.example.saive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;

public class MyOrdersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View orderItem1 = findViewById(R.id.orderItem1);
        View orderItem2 = findViewById(R.id.orderItem2);

        View.OnClickListener toTracking = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyOrdersActivity.this, OrderTrackingActivity.class));
            }
        };

        if (orderItem1 != null) orderItem1.setOnClickListener(toTracking);
        if (orderItem2 != null) orderItem2.setOnClickListener(toTracking);
    }
}

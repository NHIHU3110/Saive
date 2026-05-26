package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;

public class OrderTrackingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

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
    }
}

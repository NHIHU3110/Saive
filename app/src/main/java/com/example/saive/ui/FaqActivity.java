package com.example.saive.ui;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.FaqAdapter;
import com.example.saive.models.Faqitem;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AppCompatActivity {

    private RecyclerView rvFaq;
    private FaqAdapter adapter;
    private List<Faqitem> faqList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faq);

        setupWindowInsets();
        initData();
        setupRecyclerView();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void initData() {
        for (int i = 1; i <= 10; i++) {
            int qResId = getResources().getIdentifier("faq_q" + i, "string", getPackageName());
            int aResId = getResources().getIdentifier("faq_a" + i, "string", getPackageName());

            if (qResId != 0 && aResId != 0) {
                faqList.add(new Faqitem(getString(qResId), getString(aResId)));
            }
        }
    }

    private void setupRecyclerView() {
        rvFaq = findViewById(R.id.rvFaq);
        rvFaq.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FaqAdapter(faqList);
        rvFaq.setAdapter(adapter);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
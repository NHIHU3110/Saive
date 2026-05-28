package com.example.saive.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.saive.R;
import com.example.saive.adapters.CollectionFullAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.CollectionItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionsListActivity extends BaseActivity {

    private RecyclerView rvCollections;
    private LinearLayout layoutDotsOverlay;
    private CollectionFullAdapter adapter;
    private List<CollectionItem> collectionItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections_list);

        initViews();
        setupRecyclerView();
        setupDots();
    }

    private void initViews() {
        rvCollections = findViewById(R.id.rvCollections);
        layoutDotsOverlay = findViewById(R.id.layoutDotsOverlay);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupNavigation();
        applyWindowInsets();

        collectionItems = new ArrayList<>();
        
        // Cấu hình 3 bộ sưu tập, mỗi bộ sưu tập là 1 màn hình duy nhất
        collectionItems.add(new CollectionItem(
                "THE MONOCHROME SERIES",
                "ARCHIVE VOL. 1",
                Arrays.asList(R.mipmap.model2, R.mipmap.jacket1, R.mipmap.pant1),
                getResources().getColor(R.color.colorNoirBlack)));

        collectionItems.add(new CollectionItem(
                "THE SILK STORY",
                "CURATED VOL. 2",
                Arrays.asList(R.mipmap.banner2, R.mipmap.tshirt1, R.mipmap.tshirt2),
                getResources().getColor(R.color.colorCollectionMaroon)));

        collectionItems.add(new CollectionItem(
                "THE AUTUMN COLLECTION",
                "ESSENTIALS VOL. 3",
                Arrays.asList(R.mipmap.model1, R.mipmap.atumncollection1, R.mipmap.atumncollection2),
                getResources().getColor(R.color.colorCollectionSand)));
    }

    private void setupRecyclerView() {
        adapter = new CollectionFullAdapter(collectionItems);
        adapter.setOnCollectionClickListener(item -> {
            Intent intent = new Intent(this, CollectionDetailActivity.class);
            intent.putExtra("COLLECTION_TITLE", item.getName());
            intent.putExtra("COLLECTION_COLOR", item.getColor());
            startActivity(intent);
        });
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvCollections.setLayoutManager(layoutManager);
        rvCollections.setAdapter(adapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvCollections);

        rvCollections.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = layoutManager.findFirstVisibleItemPosition();
                updateDots(position);
            }
        });
    }

    private void setupDots() {
        layoutDotsOverlay.removeAllViews();
        int totalPages = adapter.getItemCount();
        
        int inactiveSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        
        for (int i = 0; i < totalPages; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(inactiveSize, inactiveSize);
            params.setMargins(0, 0, 16, 0);
            dot.setLayoutParams(params);
            dot.setImageResource(R.drawable.bg_dot);
            dot.setAlpha(0.3f);
            layoutDotsOverlay.addView(dot);
        }
        updateDots(0);
    }

    private void setupNavigation() {
        findViewById(R.id.navFavorite).setOnClickListener(v -> navigateToMain("SHOW_FAVORITES"));
        findViewById(R.id.navWardrobe).setOnClickListener(v -> navigateToMain("SHOW_WARDROBE"));
        findViewById(R.id.navNotify).setOnClickListener(v -> navigateToMain("SHOW_NOTIFICATIONS"));
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        findViewById(R.id.centerActionButton).setOnClickListener(v -> navigateToMain("SHOW_HOME"));
    }

    private void applyWindowInsets() {
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            findViewById(R.id.bottomNav).setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
    }

    private void updateDots(int currentPosition) {
        int activeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        int inactiveSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        for (int i = 0; i < layoutDotsOverlay.getChildCount(); i++) {
            ImageView dot = (ImageView) layoutDotsOverlay.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
            
            if (i == currentPosition) {
                params.width = activeWidth;
                dot.setAlpha(1.0f);
            } else {
                params.width = inactiveSize;
                dot.setAlpha(0.3f);
            }
            dot.setLayoutParams(params);
        }
    }
}
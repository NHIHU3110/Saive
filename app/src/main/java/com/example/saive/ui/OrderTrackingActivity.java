package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.saive.R;
import com.example.saive.base.BaseActivity;

import android.widget.TextView;
import java.util.List;
import com.example.saive.models.AdminOrder;
import com.example.saive.models.OrderItem;
import com.example.saive.utils.DataManager;

public class OrderTrackingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
            getWindow().getDecorView().setSystemUiVisibility(0);
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        String orderId = getIntent().getStringExtra("orderId");
        if (orderId != null) {
            AdminOrder order = DataManager.getInstance(this).getOrderById(orderId);
            if (order != null) {
                TextView tvOrderId = findViewById(R.id.orderId);
                TextView tvPaymentMethod = findViewById(R.id.paymentMethod);
                TextView tvShippingAddress = findViewById(R.id.shippingAddress);
                LinearLayout itemsContainer = findViewById(R.id.itemsContainer);

                tvOrderId.setText(order.getOrderId());
                tvPaymentMethod.setText(order.getPaymentMethod());
                tvShippingAddress.setText(order.getShippingAddress());

                // Cập nhật ngày dự kiến (ví dụ: 3 ngày sau khi đặt)
                TextView tvExpectedDelivery = findViewById(R.id.expectedDelivery);
                if (tvExpectedDelivery != null) {
                    tvExpectedDelivery.setText("Dự kiến: " + (order.getTimeAgo().equals("Just now") ? "Trong 3 ngày tới" : "Đang giao"));
                }

                // Cập nhật thời gian trong timeline
                TextView tvTimePlaced = findViewById(R.id.textPlaced);
                if (tvTimePlaced != null && tvTimePlaced.getParent() instanceof LinearLayout) {
                    LinearLayout parent = (LinearLayout) tvTimePlaced.getParent();
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        View child = parent.getChildAt(i);
                        if (child instanceof TextView && child != tvTimePlaced) {
                            ((TextView) child).setText(order.getTimeAgo().equals("Just now") ? "Hôm nay" : order.getTimeAgo());
                        }
                    }
                }

                itemsContainer.removeAllViews();
                TextView tvSeeMore = findViewById(R.id.tvSeeMore);

                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    List<OrderItem> items = order.getItems();
                    for (int i = 0; i < items.size(); i++) {
                        OrderItem item = items.get(i);
                        View itemView = getLayoutInflater().inflate(R.layout.item_order_detail, itemsContainer, false);
                        
                        ImageView ivItem = itemView.findViewById(R.id.ivItemImage);
                        TextView tvName = itemView.findViewById(R.id.tvItemName);
                        TextView tvAttributes = itemView.findViewById(R.id.tvItemAttributes);
                        TextView tvPrice = itemView.findViewById(R.id.tvItemPrice);

                        tvName.setText(item.getName());
                        tvPrice.setText(item.getPrice());
                        String attributes = "Size: " + item.getSize();
                        if (item.getColor() != null && !item.getColor().isEmpty()) {
                            attributes += " | Color: " + item.getColor();
                        }
                        attributes += " | Qty: " + item.getQuantity();
                        
                        tvAttributes.setText(attributes);
                        ivItem.setImageResource(item.getImageResId() != 0 ? item.getImageResId() : R.mipmap.model1);

                        if (i > 0) {
                            itemView.setVisibility(View.GONE);
                        }
                        itemsContainer.addView(itemView);
                    }

                    if (items.size() > 1) {
                        tvSeeMore.setVisibility(View.VISIBLE);
                        tvSeeMore.setText("Xem thêm (+" + (items.size() - 1) + " sản phẩm)");
                        tvSeeMore.setOnClickListener(v -> {
                            boolean isExpanded = itemsContainer.getChildAt(1).getVisibility() == View.VISIBLE;
                            if (isExpanded) {
                                // Thu gọn
                                for (int i = 1; i < itemsContainer.getChildCount(); i++) {
                                    itemsContainer.getChildAt(i).setVisibility(View.GONE);
                                }
                                tvSeeMore.setText("Xem thêm (+" + (items.size() - 1) + " sản phẩm)");
                            } else {
                                // Mở rộng
                                for (int i = 1; i < itemsContainer.getChildCount(); i++) {
                                    itemsContainer.getChildAt(i).setVisibility(View.VISIBLE);
                                }
                                tvSeeMore.setText("Thu gọn");
                            }
                        });
                    } else {
                        tvSeeMore.setVisibility(View.GONE);
                    }
                } else {
                    tvSeeMore.setVisibility(View.GONE);
                    // Fallback for legacy orders without items list
                    View itemView = getLayoutInflater().inflate(R.layout.item_order_detail, itemsContainer, false);
                    ImageView ivItem = itemView.findViewById(R.id.ivItemImage);
                    TextView tvName = itemView.findViewById(R.id.tvItemName);
                    TextView tvAttributes = itemView.findViewById(R.id.tvItemAttributes);
                    TextView tvPrice = itemView.findViewById(R.id.tvItemPrice);

                    tvName.setText(order.getItemsSummary());
                    tvPrice.setText(order.getTotalAmount());
                    String attributes = "Size: " + order.getSize();
                    if (order.getColor() != null && !order.getColor().isEmpty()) {
                        attributes += " | Color: " + order.getColor();
                    }
                    attributes += " | Qty: " + order.getQuantity();
                    
                    tvAttributes.setText(attributes);
                    ivItem.setImageResource(order.getProductImageResId() != 0 ? order.getProductImageResId() : R.mipmap.model1);
                    
                    itemsContainer.addView(itemView);
                }

                // Update timeline UI based on status
                updateTimeline(order.getStatus());
            }
        }
    }

    private void updateTimeline(String status) {
        if (status == null) return;
        String s = status.toUpperCase();

        ImageView circlePlaced = findViewById(R.id.circlePlaced);
        View linePlaced = findViewById(R.id.linePlaced);
        TextView textPlaced = findViewById(R.id.textPlaced);

        ImageView circleProgress = findViewById(R.id.circleProgress);
        View lineProgress = findViewById(R.id.lineProgress);
        TextView textProgress = findViewById(R.id.textProgress);

        ImageView circleShipped = findViewById(R.id.circleShipped);
        View lineShipped = findViewById(R.id.lineShipped);
        TextView textShipped = findViewById(R.id.textShipped);

        ImageView circleDelivered = findViewById(R.id.circleDelivered);
        TextView textDelivered = findViewById(R.id.textDelivered);

        int activeColor = getResources().getColor(R.color.colorMaroon);
        int inactiveColor = getResources().getColor(R.color.colorLightGray);
        int activeTextColor = getResources().getColor(R.color.colorNoirBlack);
        int inactiveTextColor = getResources().getColor(R.color.colorGrayText);

        // Reset all to inactive
        circlePlaced.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        linePlaced.setBackgroundColor(inactiveColor);
        textPlaced.setTextColor(inactiveTextColor);

        circleProgress.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        lineProgress.setBackgroundColor(inactiveColor);
        textProgress.setTextColor(inactiveTextColor);

        circleShipped.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        lineShipped.setBackgroundColor(inactiveColor);
        textShipped.setTextColor(inactiveTextColor);

        circleDelivered.setBackgroundTintList(android.content.res.ColorStateList.valueOf(inactiveColor));
        textDelivered.setTextColor(inactiveTextColor);

        // Set active based on status
        if (s.equals("PENDING") || s.equals("IN PROGRESS") || s.equals("SHIPPED") || s.equals("COMPLETED") || s.equals("DELIVERED")) {
            circlePlaced.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
            textPlaced.setTextColor(activeTextColor);
        }

        if (s.equals("IN PROGRESS") || s.equals("SHIPPED") || s.equals("COMPLETED") || s.equals("DELIVERED")) {
            linePlaced.setBackgroundColor(activeColor);
            circleProgress.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
            textProgress.setTextColor(activeTextColor);
        }

        if (s.equals("SHIPPED") || s.equals("COMPLETED") || s.equals("DELIVERED")) {
            lineProgress.setBackgroundColor(activeColor);
            circleShipped.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
            textShipped.setTextColor(activeTextColor);
        }

        if (s.equals("COMPLETED") || s.equals("DELIVERED")) {
            lineShipped.setBackgroundColor(activeColor);
            circleDelivered.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeColor));
            textDelivered.setTextColor(activeTextColor);
        }
    }
}

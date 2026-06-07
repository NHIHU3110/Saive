package com.example.saive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.PaymentCardAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.PaymentCard;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class PaymentCardsActivity extends BaseActivity {

    private RecyclerView rvPaymentCards;
    private PaymentCardAdapter adapter;
    private List<PaymentCard> cardList;
    private View emptyState;

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

        initViews();
        setupRecyclerView();
        updateUI();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.fabAddCard).setOnClickListener(v -> showAddCardSheet());
    }

    private void initViews() {
        rvPaymentCards = findViewById(R.id.rvPaymentCards);
        emptyState = findViewById(R.id.emptyState);
        cardList = new ArrayList<>();
        
        // Add a demo card
        cardList.add(new PaymentCard("1234567890123456", "THAO NHI HUYNH", "12/26", "123", "VISA"));
    }

    private void setupRecyclerView() {
        adapter = new PaymentCardAdapter(cardList);
        rvPaymentCards.setLayoutManager(new LinearLayoutManager(this));
        rvPaymentCards.setAdapter(adapter);
    }

    private void updateUI() {
        if (cardList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvPaymentCards.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvPaymentCards.setVisibility(View.VISIBLE);
        }
    }

    private void showAddCardSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_add_card, null);
        bottomSheetDialog.setContentView(sheetView);

        EditText etNumber = sheetView.findViewById(R.id.etCardNumber);
        EditText etHolder = sheetView.findViewById(R.id.etCardHolder);
        EditText etExpiry = sheetView.findViewById(R.id.etExpiryDate);
        EditText etCvv = sheetView.findViewById(R.id.etCvv);

        // Card Number Formatting (XXXX XXXX XXXX XXXX)
        etNumber.addTextChangedListener(new android.text.TextWatcher() {
            private boolean isDeleting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isDeleting = count > after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String source = s.toString().replaceAll(" ", "");
                if (source.length() > 16) source = source.substring(0, 16);
                
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < source.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        sb.append(" ");
                    }
                    sb.append(source.charAt(i));
                }
                
                String formatted = sb.toString();
                if (!formatted.equals(s.toString())) {
                    etNumber.removeTextChangedListener(this);
                    etNumber.setText(formatted);
                    etNumber.setSelection(formatted.length());
                    etNumber.addTextChangedListener(this);
                }
            }
        });

        // Expiry Date Formatting (MM/YY)
        etExpiry.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (s.length() == 2 && !s.toString().contains("/")) {
                    s.append("/");
                }
            }
        });

        sheetView.findViewById(R.id.btnAddCardConfirm).setOnClickListener(v -> {
            String rawNumber = etNumber.getText().toString().replaceAll(" ", "");
            String holder = etHolder.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();
            String cvv = etCvv.getText().toString().trim();

            if (rawNumber.isEmpty() || holder.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                showCustomToast(getString(R.string.error_required_field));
                return;
            }

            if (!isValidLuhn(rawNumber)) {
                showCustomToast(getString(R.string.error_invalid_card));
                return;
            }

            if (!isValidExpiry(expiry)) {
                showCustomToast(getString(R.string.error_invalid_expiry));
                return;
            }

            if (cvv.length() < 3) {
                showCustomToast(getString(R.string.error_invalid_cvv));
                return;
            }

            // Determine card type simply for demo
            String type = rawNumber.startsWith("4") ? "VISA" : "MASTERCARD";

            cardList.add(new PaymentCard(rawNumber, holder.toUpperCase(), expiry, cvv, type));
            adapter.notifyItemInserted(cardList.size() - 1);
            updateUI();
            
            showCustomToast(getString(R.string.toast_card_added));
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private boolean isValidLuhn(String number) {
        if (number == null || number.length() < 13) return false;
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private boolean isValidExpiry(String expiry) {
        if (!expiry.matches("(0[1-9]|1[0-2])/[0-9]{2}")) return false;
        
        String[] parts = expiry.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt("20" + parts[1]);
        
        java.util.Calendar now = java.util.Calendar.getInstance();
        int currentYear = now.get(java.util.Calendar.YEAR);
        int currentMonth = now.get(java.util.Calendar.MONTH) + 1;
        
        if (year < currentYear) return false;
        if (year == currentYear && month < currentMonth) return false;
        
        return true;
    }
}

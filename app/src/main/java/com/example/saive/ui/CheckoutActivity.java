package com.example.saive.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.CheckoutAddressAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Address;
import com.example.saive.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends BaseActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_NAME = "full_name";
    private static final String KEY_PHONE = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_ADDRESS = "address";

    private EditText etFullName, etPhone, etEmail, etAddress;
    private TextView tvSelectedCountry, tvSelectedCity, tvSelectedDistrict, tvSelectedWard;
    private View btnCountrySelector, btnCitySelector, btnDistrictSelector, btnWardSelector;
    private CheckBox cbSaveInfo;
    private LinearLayout containerShipping, containerPayment, containerAddressSelection, llCountrySelector;
    private RecyclerView rvCheckoutAddresses;
    private View btnAddFromCheckout;
    private TextView sectionTitle;
    private Button btnAction;
    private RadioGroup rgPaymentMethods;
    private View layoutCod, layoutBank, layoutMomo, layoutZaloPay;
    private RadioButton rbCod, rbBank, rbMomo, rbZaloPay, rbDefaultCard;
    private TextView tvSummaryFullName, tvSummaryAddress, tvSummaryPhone, tvSummarySize, tvSummaryTotal;
    private com.google.android.material.card.MaterialCardView cardOrderSummary;
    private TextView tvDefaultAddressLabel, tvDefaultName, tvDefaultAddress, tvDefaultPhone;
    private View btnChangeAddress, btnAddAddress, layoutDefaultAddress;
    private View layoutDefaultPaymentCard;
    private TextView tvDefaultCardNumber, tvDefaultCardHolder;
    private View btnAddPaymentCard, btnChangePaymentCard;
    private CheckoutAddressAdapter addressAdapter;
    private com.example.saive.adapters.PaymentCardAdapter paymentCardAdapter;
    private List<Address> addressList = new ArrayList<>();
    private List<com.example.saive.models.PaymentCard> savedCards = new ArrayList<>();
    private Address selectedAddress;
    private com.example.saive.models.PaymentCard selectedCard;
    private boolean isPaymentStep = false;
    private String totalPrice, selectedSize;
    private double discountRate;
    private String couponCode;
    private TextView tvSummarySubtotal, tvSummaryDiscountLabel, tvSummaryDiscountValue;
    private View layoutSummaryDiscount;
    private static final String ADDRESS_PREFS = "address_prefs";
    private static final String ADDRESS_KEY = "saved_addresses";

    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //loginLauncher register
        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Login thành công hoặc Guest bypass → tiếp tục setup checkout
                        setContentView(R.layout.activity_checkout);
                        initViews();
                        loadIntentData();
                        loadAddresses();
                        checkExistingAddress();
                        setupListeners();
                    } else {
                        // Bấm back ở login → quay lại Cart
                        finish();
                    }
                }
        );
        super.onCreate(savedInstanceState);

        updateStatusBar();
        //auth guard
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("is_logged_in", false)) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("return_result", true);
            loginLauncher.launch(loginIntent);
            return;
        }
        setContentView(R.layout.activity_checkout);

        initViews();
        loadIntentData();
        loadAddresses();
        checkExistingAddress();
        setupListeners();
    }

    private void updateStatusBar() {
        boolean isDarkMode = (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;

        // Setup status bar for full-bleed header
        getWindow().setStatusBarColor(androidx.core.content.ContextCompat.getColor(this, R.color.colorHeaderBg));
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        // In Dark Mode, colorHeaderBg becomes beige, so we need dark icons
        if (isDarkMode) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private void loadIntentData() {
        totalPrice = getIntent().getStringExtra("total_price");
        selectedSize = getIntent().getStringExtra("selected_size");
        discountRate = getIntent().getDoubleExtra("discount_rate", 0);
        couponCode = getIntent().getStringExtra("coupon_code");
        
        if (totalPrice != null) {
            btnAction.setText(getString(R.string.btn_continue_payment) + " (" + totalPrice + ")");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
        if (!addressList.isEmpty()) {
            if (addressAdapter == null) {
                // Nếu trước đó chưa có adapter (danh sách trống), hãy thiết lập mới
                checkExistingAddress();
            } else {
                addressAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadAddresses() {
        SharedPreferences prefs = getSharedPreferences(ADDRESS_PREFS, MODE_PRIVATE);
        String json = prefs.getString(ADDRESS_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Address>>() {}.getType();
            List<Address> loaded = gson.fromJson(json, type);
            if (loaded != null) {
                addressList.clear();
                addressList.addAll(loaded);
            }
        }
    }

    private void checkExistingAddress() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (isLoggedIn && addressList != null && !addressList.isEmpty()) {
            // Pick default address or first one
            selectedAddress = addressList.get(0);
            for (Address a : addressList) {
                if (a.isDefault()) {
                    selectedAddress = a;
                    break;
                }
            }
            showAddressSummaryStep();
        } else {
            // Show shipping manual entry if no addresses OR if guest
            showShippingStep();
            if (llCountrySelector != null) llCountrySelector.setVisibility(View.GONE);
            sectionTitle.setVisibility(View.VISIBLE);
            sectionTitle.setText(R.string.checkout_shipping_title);

            // If guest, clear any previously entered info to be safe
            if (!isLoggedIn) {
                clearShippingFields();
            } else {
                loadSavedInfo();
            }
        }
    }

    private void clearShippingFields() {
        if (etFullName != null) etFullName.setText("");
        if (etPhone != null) etPhone.setText("");
        if (etEmail != null) etEmail.setText("");
        if (etAddress != null) etAddress.setText("");
        if (tvSelectedCity != null) tvSelectedCity.setText(R.string.hint_choose_city);
        if (tvSelectedDistrict != null) tvSelectedDistrict.setText(R.string.hint_choose_district);
        if (tvSelectedWard != null) tvSelectedWard.setText(R.string.hint_choose_ward);
    }

    private void showShippingStep() {
        isPaymentStep = false;
        containerShipping.setVisibility(View.VISIBLE);
        containerPayment.setVisibility(View.GONE);
        containerAddressSelection.setVisibility(View.GONE);
        layoutDefaultAddress.setVisibility(View.GONE);
        if (sectionTitle != null) {
            sectionTitle.setVisibility(View.VISIBLE);
            sectionTitle.setText(R.string.checkout_shipping_title);
        }
        btnAction.setVisibility(View.VISIBLE);
        if (totalPrice != null) {
            btnAction.setText(getString(R.string.btn_continue_payment) + " (" + totalPrice + ")");
        } else {
            btnAction.setText(R.string.btn_continue_payment);
        }
    }

    private void showAddressSummaryStep() {
        isPaymentStep = false;
        containerAddressSelection.setVisibility(View.GONE);
        containerShipping.setVisibility(View.GONE);
        containerPayment.setVisibility(View.GONE);
        layoutDefaultAddress.setVisibility(View.VISIBLE);

        // Update the default card UI
        updateDefaultAddressUI();

        // Hide large selectors
        if (llCountrySelector != null) llCountrySelector.setVisibility(View.GONE);
        if (sectionTitle != null) sectionTitle.setVisibility(View.GONE);

        if (totalPrice != null) {
            btnAction.setText(getString(R.string.btn_continue_payment) + " (" + totalPrice + ")");
        } else {
            btnAction.setText(R.string.btn_continue_payment);
        }
    }

    private void updateDefaultAddressUI() {
        if (selectedAddress != null) {
            tvDefaultAddressLabel.setText(selectedAddress.getLabel().toUpperCase());
            tvDefaultName.setText(selectedAddress.getFullName());
            tvDefaultAddress.setText(selectedAddress.getFullDisplayAddress());
            tvDefaultPhone.setText(selectedAddress.getPhoneNumber());
        }
    }

    private void showAddressSelectionBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_address_selection_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView rvAddresses = view.findViewById(R.id.rvBottomSheetAddresses);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));

        CheckoutAddressAdapter adapter = new CheckoutAddressAdapter(addressList, selectedAddress, address -> {
            selectedAddress = address;
            updateDefaultAddressUI();
            bottomSheetDialog.dismiss();
        });
        rvAddresses.setAdapter(adapter);

        view.findViewById(R.id.btnAddNewAddress).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, AddAddressActivity.class);
            startActivity(intent);
        });

        bottomSheetDialog.show();
    }

    private void showAddCardBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_add_card_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        android.widget.EditText etCardNumber = view.findViewById(R.id.etCardNumber);
        android.widget.EditText etCardHolder = view.findViewById(R.id.etCardHolder);
        android.widget.EditText etExpiry = view.findViewById(R.id.etExpiry);

        TextView tvPreviewNumber = view.findViewById(R.id.tvCardNumber);
        TextView tvPreviewHolder = view.findViewById(R.id.tvCardHolder);
        TextView tvPreviewExpiry = view.findViewById(R.id.tvExpiryDate);

        etCardNumber.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String val = s.toString().replaceAll(" ", "");
                if (val.isEmpty()) {
                    tvPreviewNumber.setText("**** **** **** ****");
                } else {
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < val.length(); i++) {
                        if (i > 0 && i % 4 == 0) formatted.append(" ");
                        formatted.append(val.charAt(i));
                    }
                    tvPreviewNumber.setText(formatted.toString());
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etCardHolder.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPreviewHolder.setText(s.toString().isEmpty() ? "CARD HOLDER" : s.toString().toUpperCase());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etExpiry.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (input.length() == 2 && before < count && !input.contains("/")) {
                    etExpiry.setText(input + "/");
                    etExpiry.setSelection(etExpiry.getText().length());
                }
                tvPreviewExpiry.setText(s.toString().isEmpty() ? "MM/YY" : s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        view.findViewById(R.id.btnSaveCard).setOnClickListener(v -> {
            String number = etCardNumber.getText().toString().trim();
            String holder = etCardHolder.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();

            if (number.length() < 12) {
                ToastUtils.showCustomToast(this, "Invalid card number");
                return;
            }

            com.example.saive.models.PaymentCard card = new com.example.saive.models.PaymentCard(
                    String.valueOf(System.currentTimeMillis()),
                    number,
                    holder,
                    expiry,
                    "VISA"
            );

            com.example.saive.utils.DataManager.getInstance(this).addPaymentCard(card);
            bottomSheetDialog.dismiss();
            ToastUtils.showCustomToast(this, "Card added successfully");
            loadSavedCards();
        });

        bottomSheetDialog.show();
    }

    private void setupAddressList() {
        addressAdapter = new CheckoutAddressAdapter(addressList, selectedAddress, address -> {
            selectedAddress = address;
        });
        rvCheckoutAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutAddresses.setAdapter(addressAdapter);
    }

    private View layoutAddCard;
    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);

        tvSelectedCity = findViewById(R.id.tvSelectedCity);
        btnCitySelector = findViewById(R.id.btnCitySelector);

        tvSelectedDistrict = findViewById(R.id.tvSelectedDistrict);
        btnDistrictSelector = findViewById(R.id.btnDistrictSelector);

        tvSelectedWard = findViewById(R.id.tvSelectedWard);
        btnWardSelector = findViewById(R.id.btnWardSelector);

        cbSaveInfo = findViewById(R.id.cbSaveInfo);

        setupSelectors();

        containerShipping = findViewById(R.id.containerShipping);
        containerPayment = findViewById(R.id.containerPayment);
        containerAddressSelection = findViewById(R.id.containerAddressSelection);
        rvCheckoutAddresses = findViewById(R.id.rvCheckoutAddresses);
        btnAddFromCheckout = findViewById(R.id.btnAddFromCheckout);

        sectionTitle = findViewById(R.id.sectionTitle);
        btnAction = findViewById(R.id.btnAction);
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);

        layoutCod = findViewById(R.id.layoutCod);
        layoutBank = findViewById(R.id.layoutBank);
        layoutMomo = findViewById(R.id.layoutMomo);
        layoutZaloPay = findViewById(R.id.layoutZaloPay);

        rbCod = findViewById(R.id.rbCod);
        rbBank = findViewById(R.id.rbBank);
        rbMomo = findViewById(R.id.rbMomo);
        rbZaloPay = findViewById(R.id.rbZaloPay);
        rbDefaultCard = findViewById(R.id.rbDefaultCard);

        layoutAddCard = findViewById(R.id.layoutAddCard);

        layoutDefaultPaymentCard = findViewById(R.id.layoutDefaultPaymentCard);
        tvDefaultCardNumber = findViewById(R.id.tvDefaultCardNumber);
        tvDefaultCardHolder = findViewById(R.id.tvDefaultCardHolder);
        btnAddPaymentCard = findViewById(R.id.btnAddPaymentCard);
        btnChangePaymentCard = findViewById(R.id.btnChangePaymentCard);

        tvSummaryFullName = findViewById(R.id.tvSummaryFullName);
        tvSummaryAddress = findViewById(R.id.tvSummaryAddress);
        tvSummaryPhone = findViewById(R.id.tvSummaryPhone);
        tvSummarySize = findViewById(R.id.tvSummarySize);
        tvSummaryTotal = findViewById(R.id.tvSummaryTotal);
        tvSummarySubtotal = findViewById(R.id.tvSummarySubtotal);
        tvSummaryDiscountLabel = findViewById(R.id.tvSummaryDiscountLabel);
        tvSummaryDiscountValue = findViewById(R.id.tvSummaryDiscountValue);
        layoutSummaryDiscount = findViewById(R.id.layoutSummaryDiscount);
        cardOrderSummary = findViewById(R.id.cardOrderSummary);

        layoutDefaultAddress = findViewById(R.id.layoutDefaultAddress);
        tvDefaultAddressLabel = findViewById(R.id.tvDefaultAddressLabel);
        tvDefaultName = findViewById(R.id.tvDefaultName);
        tvDefaultAddress = findViewById(R.id.tvDefaultAddress);
        tvDefaultPhone = findViewById(R.id.tvDefaultPhone);
        btnChangeAddress = findViewById(R.id.btnChangeAddress);
        btnAddAddress = findViewById(R.id.btnAddAddress);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            if (isPaymentStep) {
                if (containerAddressSelection.getVisibility() == View.VISIBLE) {
                    showAddressSummaryStep();
                } else {
                    showShippingStep();
                }
            } else if (containerAddressSelection.getVisibility() == View.VISIBLE) {
                finish();
            } else {
                finish();
            }
        });
    }

    private void setupSelectors() {
        // City Selector
        btnCitySelector.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            List<String> cities = com.example.saive.utils.LocationProvider.getProvinces(this);
            showOptionsBottomSheet(getString(R.string.hint_choose_city),
                    cities.toArray(new String[0]),
                    selection -> {
                        tvSelectedCity.setText(selection);
                        tvSelectedDistrict.setText(R.string.hint_choose_district);
                        tvSelectedWard.setText(R.string.hint_choose_ward);
                    });
        });
        tvSelectedCity.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            btnCitySelector.performClick();
        });

        // District Selector
        btnDistrictSelector.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            String selectedCity = tvSelectedCity.getText().toString();
            List<String> districts = com.example.saive.utils.LocationProvider.getDistricts(this, selectedCity);

            if (districts.isEmpty()) {
                // Fallback for other cities
                districts = new ArrayList<>();
                districts.add(selectedCity + " District 1");
                districts.add(selectedCity + " District 2");
            }

            showOptionsBottomSheet(getString(R.string.hint_choose_district),
                    districts.toArray(new String[0]),
                    selection -> {
                        tvSelectedDistrict.setText(selection);
                        tvSelectedWard.setText(R.string.hint_choose_ward);
                    });
        });
        tvSelectedDistrict.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            btnDistrictSelector.performClick();
        });

        // Ward Selector
        btnWardSelector.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            String selectedCity = tvSelectedCity.getText().toString();
            String selectedDistrict = tvSelectedDistrict.getText().toString();
            List<String> wards = com.example.saive.utils.LocationProvider.getWards(this, selectedCity, selectedDistrict);

            if (wards.isEmpty()) {
                // Generic mock wards for other districts
                wards = new ArrayList<>();
                wards.add(selectedDistrict + " Ward 1");
                wards.add(selectedDistrict + " Ward 2");
                wards.add(selectedDistrict + " Ward 3");
            }

            showOptionsBottomSheet(getString(R.string.hint_choose_ward),
                    wards.toArray(new String[0]),
                    selection -> tvSelectedWard.setText(selection));
        });
        tvSelectedWard.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            btnWardSelector.performClick();
        });
    }

    private interface OnOptionSelected {
        void onSelected(String selection);
    }

    private void showOptionsBottomSheet(String title, String[] options, OnOptionSelected callback) {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null, false);

        TextView tvTitle = view.findViewById(R.id.tvSheetTitle);
        tvTitle.setText(title);

        EditText etSearch = view.findViewById(R.id.etSearchOption);
        etSearch.setVisibility(View.VISIBLE);

        androidx.recyclerview.widget.RecyclerView rvOptions = view.findViewById(R.id.rvSheetOptions);
        rvOptions.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        List<String> originalOptions = java.util.Arrays.asList(options);
        List<String> filteredOptions = new ArrayList<>(originalOptions);

        // Simple adapter for the BottomSheet
        androidx.recyclerview.widget.RecyclerView.Adapter adapter = new androidx.recyclerview.widget.RecyclerView.Adapter<OptionViewHolder>() {
            @androidx.annotation.NonNull
            @Override
            public OptionViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.item_bottom_sheet_option, parent, false);
                return new OptionViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@androidx.annotation.NonNull OptionViewHolder holder, int position) {
                String option = filteredOptions.get(position);
                holder.tvName.setText(option);
                holder.itemView.setOnClickListener(v -> {
                    callback.onSelected(option);
                    bottomSheetDialog.dismiss();
                });
            }

            @Override
            public int getItemCount() {
                return filteredOptions.size();
            }
        };

        rvOptions.setAdapter(adapter);

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                filteredOptions.clear();
                for (String option : originalOptions) {
                    if (option.toLowerCase().contains(query)) {
                        filteredOptions.add(option);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private static class OptionViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TextView tvName;
        OptionViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvOptionName);
        }
    }

    private void loadSavedInfo() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        etFullName.setText(prefs.getString(KEY_NAME, ""));
        etPhone.setText(prefs.getString(KEY_PHONE, ""));
        etEmail.setText(prefs.getString(KEY_EMAIL, ""));

        String savedCity = prefs.getString("city", "");
        if (!TextUtils.isEmpty(savedCity)) {
            tvSelectedCity.setText(savedCity);
        }

        String savedDistrict = prefs.getString(KEY_DISTRICT, "");
        if (!TextUtils.isEmpty(savedDistrict)) {
            tvSelectedDistrict.setText(savedDistrict);
        }

        String savedWard = prefs.getString("ward", "");
        if (!TextUtils.isEmpty(savedWard)) {
            tvSelectedWard.setText(savedWard);
        }

        etAddress.setText(prefs.getString(KEY_ADDRESS, ""));

        // Pre-fill if name is not empty
        if (!TextUtils.isEmpty(etFullName.getText())) {
            cbSaveInfo.setChecked(true);
        }
    }

    private void setupListeners() {
        if (btnAddAddress != null) {
            btnAddAddress.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(this, AddAddressActivity.class);
                startActivity(intent);
            });
        }

        btnChangeAddress.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            showAddressSummaryStep();
        });

        btnAddPaymentCard.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            showAddCardBottomSheet();
        });

        btnChangePaymentCard.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            showCardSelectionBottomSheet();
        });

        layoutAddCard.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            showAddCardBottomSheet();
        });

        layoutDefaultPaymentCard.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbDefaultCard);
        });

        rbDefaultCard.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbDefaultCard);
        });

        layoutCod.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbCod);
        });
        layoutBank.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbBank);
        });
        layoutMomo.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbMomo);
        });
        layoutZaloPay.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbZaloPay);
        });

        if (layoutAddCard != null) {
            layoutAddCard.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showAddCardBottomSheet();
            });
        }

        rbCod.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbCod);
        });
        rbBank.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbBank);
        });
        rbMomo.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbMomo);
        });
        rbZaloPay.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectPaymentMethod(rbZaloPay);
        });

        btnAddFromCheckout.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            containerAddressSelection.setVisibility(View.GONE);
            showShippingStep();

            // Show large selectors back
            sectionTitle.setVisibility(View.VISIBLE);
        });

        if (cardOrderSummary != null) {
            cardOrderSummary.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showAddressSummaryStep();
            });
        }

        btnAction.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            if (!isPaymentStep) {
                if (layoutDefaultAddress.getVisibility() == View.VISIBLE) {
                    showPaymentStep();
                } else if (validateShippingInfo()) {
                    if (cbSaveInfo.isChecked()) {
                        saveInfo();
                    }
                    showPaymentStep();
                }
            } else {
                processOrder();
            }
        });
    }

    private void selectPaymentMethod(RadioButton selectedRb) {
        rbCod.setChecked(selectedRb == rbCod);
        rbBank.setChecked(selectedRb == rbBank);
        rbMomo.setChecked(selectedRb == rbMomo);
        rbZaloPay.setChecked(selectedRb == rbZaloPay);
        rbDefaultCard.setChecked(selectedRb == rbDefaultCard);

        if (paymentCardAdapter != null) {
            if (selectedRb != rbDefaultCard) {
                selectedCard = null;
                paymentCardAdapter.setSelectedCard(null);
            }
        }
    }

    private boolean validateShippingInfo() {
        if (TextUtils.isEmpty(etFullName.getText())) {
            etFullName.setError(getString(R.string.error_required_field));
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getText())) {
            etPhone.setError(getString(R.string.error_required_field));
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError(getString(R.string.error_required_field));
            return false;
        }
        if (tvSelectedCity.getText().toString().contains("Choose")) {
            ToastUtils.showCustomToast(this, "Please select a city");
            return false;
        }
        if (tvSelectedDistrict.getText().toString().contains("Choose")) {
            ToastUtils.showCustomToast(this, "Please select a district");
            return false;
        }
        if (tvSelectedWard.getText().toString().contains("Choose")) {
            ToastUtils.showCustomToast(this, "Please select a ward");
            return false;
        }
        if (TextUtils.isEmpty(etAddress.getText())) {
            etAddress.setError(getString(R.string.error_required_field));
            return false;
        }
        return true;
    }

    private void saveInfo() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NAME, etFullName.getText().toString());
        editor.putString(KEY_PHONE, etPhone.getText().toString());
        editor.putString(KEY_EMAIL, etEmail.getText().toString());
        editor.putString("city", tvSelectedCity.getText().toString());
        editor.putString(KEY_DISTRICT, tvSelectedDistrict.getText().toString());
        editor.putString("ward", tvSelectedWard.getText().toString());
        editor.putString(KEY_ADDRESS, etAddress.getText().toString());
        editor.apply();
    }

    private void showAddressSelectionStep() {
        isPaymentStep = true;
        containerShipping.setVisibility(View.GONE);
        containerPayment.setVisibility(View.GONE);
        layoutDefaultAddress.setVisibility(View.GONE);
        containerAddressSelection.setVisibility(View.VISIBLE);
        if (sectionTitle != null) {
            sectionTitle.setVisibility(View.VISIBLE);
            sectionTitle.setText(R.string.checkout_shipping_to);
        }
        btnAction.setVisibility(View.GONE);
    }

    private void showPaymentStep() {
        isPaymentStep = true;
        containerShipping.setVisibility(View.GONE);
        containerAddressSelection.setVisibility(View.GONE);
        layoutDefaultAddress.setVisibility(View.GONE);
        containerPayment.setVisibility(View.VISIBLE);

        // Load and show saved cards
        loadSavedCards();

        // Hide the top section title as it's now inside containerPayment
        if (sectionTitle != null) sectionTitle.setVisibility(View.GONE);

        // Update Order Summary
        if (selectedAddress != null) {
            tvSummaryFullName.setText(selectedAddress.getFullName());
            tvSummaryAddress.setText(selectedAddress.getFullDisplayAddress());
            tvSummaryPhone.setText(selectedAddress.getPhoneNumber());
        } else {
            // Manually entered info
            tvSummaryFullName.setText(etFullName.getText().toString());
            String addressText = etAddress.getText().toString() + ", " +
                    tvSelectedWard.getText().toString() + ", " +
                    tvSelectedDistrict.getText().toString() + ", " +
                    tvSelectedCity.getText().toString();
            tvSummaryAddress.setText(addressText);
            tvSummaryPhone.setText(etPhone.getText().toString());
        }

        if (selectedSize != null && !selectedSize.isEmpty()) {
            tvSummarySize.setVisibility(View.VISIBLE);
            tvSummarySize.setText(getString(R.string.label_size) + ": " + selectedSize);
        } else {
            tvSummarySize.setVisibility(View.GONE);
        }

        updatePriceSummary();

        if (totalPrice != null) {
            btnAction.setText(getString(R.string.btn_place_order) + " (" + totalPrice + ")");
        } else {
            btnAction.setText(R.string.btn_place_order);
        }
    }

    private void showCardSelectionBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_address_selection_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvBottomSheetTitle);
        if (tvTitle != null) tvTitle.setText(R.string.payment_card_select_title);

        RecyclerView rvCards = view.findViewById(R.id.rvBottomSheetAddresses);
        rvCards.setLayoutManager(new LinearLayoutManager(this));

        com.example.saive.adapters.PaymentCardAdapter adapter = new com.example.saive.adapters.PaymentCardAdapter(savedCards, card -> {
            selectedCard = card;
            updateDefaultCardUI();
            selectPaymentMethod(rbDefaultCard);
            bottomSheetDialog.dismiss();
        });
        adapter.setSelectedCard(selectedCard);
        rvCards.setAdapter(adapter);

        androidx.appcompat.widget.AppCompatButton btnAdd = view.findViewById(R.id.btnAddNewAddress);
        if (btnAdd != null) {
            btnAdd.setText(R.string.payment_method_add_card);
            btnAdd.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                showAddCardBottomSheet();
            });
        }

        bottomSheetDialog.show();
    }

    private void updateDefaultCardUI() {
        if (selectedCard != null) {
            layoutDefaultPaymentCard.setVisibility(View.VISIBLE);
            layoutAddCard.setVisibility(View.GONE);
            btnChangePaymentCard.setVisibility(View.VISIBLE);
            tvDefaultCardNumber.setText(selectedCard.getCardNumber());
            tvDefaultCardHolder.setText(selectedCard.getCardHolderName());
        } else {
            layoutDefaultPaymentCard.setVisibility(View.GONE);
            layoutAddCard.setVisibility(View.VISIBLE);
            btnChangePaymentCard.setVisibility(View.GONE);
        }
    }

    private void loadSavedCards() {
        savedCards = com.example.saive.utils.DataManager.getInstance(this).getPaymentCards();
        if (savedCards != null && !savedCards.isEmpty()) {
            if (selectedCard == null) {
                selectedCard = savedCards.get(0);
            }
            updateDefaultCardUI();
        } else {
            layoutDefaultPaymentCard.setVisibility(View.GONE);
            layoutAddCard.setVisibility(View.VISIBLE);
            btnChangePaymentCard.setVisibility(View.GONE);
        }
    }

    private void updatePriceSummary() {
        double subtotal = com.example.saive.utils.CartManager.getInstance(this).getTotalPrice();
        double discountAmount = subtotal * discountRate;
        double finalTotal = subtotal - discountAmount;

        if (tvSummarySubtotal != null) {
            tvSummarySubtotal.setText(com.example.saive.utils.PriceFormatter.formatPrice(subtotal));
        }

        if (discountRate > 0 && layoutSummaryDiscount != null) {
            layoutSummaryDiscount.setVisibility(View.VISIBLE);
            if (tvSummaryDiscountLabel != null) {
                String discountText = getString(R.string.label_discount);
                if (discountText.endsWith(":")) {
                    discountText = discountText.substring(0, discountText.length() - 1);
                }
                tvSummaryDiscountLabel.setText(discountText + " (" + couponCode + ")");
            }
            if (tvSummaryDiscountValue != null) {
                tvSummaryDiscountValue.setText("-" + com.example.saive.utils.PriceFormatter.formatPrice(discountAmount));
            }
        } else if (layoutSummaryDiscount != null) {
            layoutSummaryDiscount.setVisibility(View.GONE);
        }

        String formattedTotal = com.example.saive.utils.PriceFormatter.formatPrice(finalTotal);
        tvSummaryTotal.setText(formattedTotal);
        totalPrice = formattedTotal; // Sync back to totalPrice used for button and order
    }

    private void processOrder() {
        if (!rbCod.isChecked() && !rbBank.isChecked() && !rbMomo.isChecked() && !rbZaloPay.isChecked() && !rbDefaultCard.isChecked()) {
            com.example.saive.utils.ToastUtils.showCustomToast(this, "Please select a payment method");
            return;
        }

        // 1. Chuẩn bị dữ liệu đơn hàng từ giỏ hàng
        com.example.saive.utils.CartManager cartManager = com.example.saive.utils.CartManager.getInstance(this);
        List<com.example.saive.models.Product> cartItems = cartManager.getCartItems();

        if (cartItems.isEmpty()) {
            com.example.saive.utils.ToastUtils.showCustomToast(this, "Giỏ hàng trống!");
            return;
        }

        List<com.example.saive.models.OrderItem> orderItems = new ArrayList<>();
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append(cartItems.size()).append(" Items: ");

        for (int i = 0; i < cartItems.size(); i++) {
            com.example.saive.models.Product p = cartItems.get(i);
            String itemSize = p.getSelectedSize();
            if (itemSize == null || itemSize.isEmpty()) {
                itemSize = (p.getCategory() != null && p.getCategory().toLowerCase().contains("glasses")) ? "One Size" : "M";
            }

            orderItems.add(new com.example.saive.models.OrderItem(
                    p.getName(),
                    itemSize,
                    p.getQuantity(),
                    p.getPrice(),
                    p.getImageResId()
            ));

            summaryBuilder.append(p.getName());
            if (i < cartItems.size() - 1) summaryBuilder.append(", ");
        }

        // 2. Tạo đối tượng AdminOrder
        String orderId = "#SA-" + (System.currentTimeMillis() % 1000000);
        String customerName = selectedAddress != null ? selectedAddress.getFullName() : etFullName.getText().toString();
        String shippingAddr = selectedAddress != null ? selectedAddress.getFullDisplayAddress() :
                (etAddress.getText().toString() + ", " + tvSelectedWard.getText() + ", " + tvSelectedDistrict.getText() + ", " + tvSelectedCity.getText());

        String paymentMethod = "COD";
        if (rbBank.isChecked()) paymentMethod = "Bank Transfer";
        else if (rbMomo.isChecked()) paymentMethod = "Momo";
        else if (rbZaloPay.isChecked()) paymentMethod = "ZaloPay";
        else if (rbDefaultCard.isChecked() && selectedCard != null)
            paymentMethod = "Card (**** " + selectedCard.getCardNumber().substring(Math.max(0, selectedCard.getCardNumber().length() - 4)) + ")";

        // Lấy giá trị tổng tiền từ TextView nếu biến totalPrice bị null
        String finalPrice = (totalPrice != null) ? totalPrice : tvSummaryTotal.getText().toString();

        String itemSizeLegacy = (selectedSize != null && !selectedSize.isEmpty()) ? selectedSize : cartItems.get(0).getSelectedSize();
        if (itemSizeLegacy == null || itemSizeLegacy.isEmpty()) itemSizeLegacy = "M";

        com.example.saive.models.AdminOrder newOrder = new com.example.saive.models.AdminOrder(
                orderId,
                customerName,
                summaryBuilder.toString(),
                finalPrice,
                "PENDING",
                "Just now",
                cartItems.get(0).getImageResId(),
                itemSizeLegacy,
                cartManager.getItemCount(),
                paymentMethod,
                shippingAddr
        );
        newOrder.setItems(orderItems);

        // 3. Lưu đơn hàng và xóa giỏ hàng
        com.example.saive.utils.DataManager.getInstance(this).addOrder(newOrder);
        cartManager.clearCart();

        if (rbBank.isChecked()) {
            // Lưu order trước rồi mở QR screen
            Intent qrIntent = new Intent(CheckoutActivity.this, BankTransferActivity.class);
            startActivity(qrIntent);
            finish();
        } else {
            showSuccessDialog();
        }
    }

    private void showSuccessDialog() {
        Intent intent = new Intent(CheckoutActivity.this, PaymentSuccessActivity.class);
        startActivity(intent);
        finish();
    }
}
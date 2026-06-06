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
    private TextView tvSelectedCountry, tvSelectedCity, tvSelectedDistrict;
    private View btnCountrySelector, btnCitySelector, btnDistrictSelector;
    private CheckBox cbSaveInfo;
    private LinearLayout containerShipping, containerPayment, containerAddressSelection, llCountrySelector;
    private RecyclerView rvCheckoutAddresses;
    private View btnAddFromCheckout;
    private TextView sectionTitle;
    private Button btnAction;
    private RadioGroup rgPaymentMethods;
    private View layoutCod, layoutBank, layoutMomo, layoutZaloPay;
    private RadioButton rbCod, rbBank, rbMomo, rbZaloPay;
    private TextView tvSummaryFullName, tvSummaryAddress, tvSummaryPhone, tvSummaryTotal;
    private CheckoutAddressAdapter addressAdapter;
    private List<Address> addressList = new ArrayList<>();
    private Address selectedAddress;
    private boolean isPaymentStep = false;
    private String totalPrice;
    private double discount;
    private static final String ADDRESS_PREFS = "address_prefs";
    private static final String ADDRESS_KEY = "saved_addresses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        updateStatusBar();

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
        
        // Setup status bar for full-bleed maroon header
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        
        // In Dark Mode, colorMaroon becomes light beige, so we need dark icons
        if (isDarkMode) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private void loadIntentData() {
        totalPrice = getIntent().getStringExtra("total_price");
        discount = getIntent().getDoubleExtra("discount_rate", 0);
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
        if (addressList != null && !addressList.isEmpty()) {
            // Pick default address or first one
            selectedAddress = addressList.get(0);
            for (Address a : addressList) {
                if (a.isDefault()) {
                    selectedAddress = a;
                    break;
                }
            }
            showAddressSelectionStep();
        } else {
            showShippingStep();
            loadSavedInfo();
        }
    }

    private void showAddressSelectionStep() {
        isPaymentStep = false;
        containerAddressSelection.setVisibility(View.VISIBLE);
        containerShipping.setVisibility(View.GONE);
        containerPayment.setVisibility(View.GONE);
        
        // Hide large selectors in selection mode to focus on saved address
        if (llCountrySelector != null) llCountrySelector.setVisibility(View.GONE);
        sectionTitle.setVisibility(View.GONE);

        setupAddressList();
        if (totalPrice != null) {
            btnAction.setText(getString(R.string.btn_continue_payment) + " (" + totalPrice + ")");
        } else {
            btnAction.setText(R.string.btn_continue_payment);
        }
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
        
        tvSelectedCountry = findViewById(R.id.tvSelectedCountry);
        btnCountrySelector = findViewById(R.id.btnCountrySelector);
        
        tvSelectedCity = findViewById(R.id.tvSelectedCity);
        btnCitySelector = findViewById(R.id.btnCitySelector);
        
        tvSelectedDistrict = findViewById(R.id.tvSelectedDistrict);
        btnDistrictSelector = findViewById(R.id.btnDistrictSelector);
        
        cbSaveInfo = findViewById(R.id.cbSaveInfo);
        
        setupSelectors();
        
        containerShipping = findViewById(R.id.containerShipping);
        containerPayment = findViewById(R.id.containerPayment);
        containerAddressSelection = findViewById(R.id.containerAddressSelection);
        rvCheckoutAddresses = findViewById(R.id.rvCheckoutAddresses);
        btnAddFromCheckout = findViewById(R.id.btnAddFromCheckout);
        llCountrySelector = findViewById(R.id.llCountrySelector);

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

        layoutAddCard = findViewById(R.id.layoutAddCard);

        tvSummaryFullName = findViewById(R.id.tvSummaryFullName);
        tvSummaryAddress = findViewById(R.id.tvSummaryAddress);
        tvSummaryPhone = findViewById(R.id.tvSummaryPhone);
        tvSummaryTotal = findViewById(R.id.tvSummaryTotal);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            if (isPaymentStep) {
                if (containerAddressSelection.getVisibility() == View.VISIBLE) {
                    showAddressSelectionStep();
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
        // Country Selector
        btnCountrySelector.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            showOptionsBottomSheet(getString(R.string.hint_choose_country),
                getResources().getStringArray(R.array.countries_array),
                selection -> {
                    tvSelectedCountry.setText(selection);
                    // Clear city and district when country changes
                    tvSelectedCity.setText(R.string.hint_choose_city);
                    tvSelectedDistrict.setText(R.string.hint_choose_district);
                });
        });
        tvSelectedCountry.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            btnCountrySelector.performClick();
        });

        // City Selector
        btnCitySelector.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            String selectedCountry = tvSelectedCountry.getText().toString();
            // Currently we only have city data for Vietnam
            if (selectedCountry.equals("Vietnam") || selectedCountry.equals("Việt Nam") || selectedCountry.equals("越南")) {
                showOptionsBottomSheet(getString(R.string.hint_choose_city),
                        getResources().getStringArray(R.array.cities_vn_array),
                        selection -> {
                            tvSelectedCity.setText(selection);
                            tvSelectedDistrict.setText(R.string.hint_choose_district);
                        });
            } else {
                ToastUtils.showCustomToast(this, "Cities not available for selected country");
            }
        });
        tvSelectedCity.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            btnCitySelector.performClick();
        });

        // District Selector
        btnDistrictSelector.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            String selectedCity = tvSelectedCity.getText().toString();
            String[] districts;
            
            if (selectedCity.equals("Ho Chi Minh City") || selectedCity.equals("Hồ Chí Minh") || selectedCity.equals("胡志明市")) {
                districts = getResources().getStringArray(R.array.districts_hcm_array);
            } else if (selectedCity.equals("Hanoi") || selectedCity.equals("Hà Nội") || selectedCity.equals("河内")) {
                districts = getResources().getStringArray(R.array.districts_hanoi_array);
            } else {
                // Fallback for other cities
                districts = new String[]{"District 1", "District 2", "District 3", "District 4", "District 5"};
            }

            showOptionsBottomSheet(getString(R.string.hint_choose_district),
                    districts,
                    selection -> tvSelectedDistrict.setText(selection));
        });
        tvSelectedDistrict.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            btnDistrictSelector.performClick();
        });
    }

    private interface OnOptionSelected {
        void onSelected(String selection);
    }

    private void showOptionsBottomSheet(String title, String[] options, OnOptionSelected callback) {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = 
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null, false);
        
        TextView tvTitle = view.findViewById(R.id.tvSheetTitle);
        tvTitle.setText(title);
        
        androidx.recyclerview.widget.RecyclerView rvOptions = view.findViewById(R.id.rvSheetOptions);
        rvOptions.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        
        // Simple adapter for the BottomSheet
        rvOptions.setAdapter(new androidx.recyclerview.widget.RecyclerView.Adapter<OptionViewHolder>() {
            @androidx.annotation.NonNull
            @Override
            public OptionViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.item_bottom_sheet_option, parent, false);
                return new OptionViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@androidx.annotation.NonNull OptionViewHolder holder, int position) {
                holder.tvName.setText(options[position]);
                holder.itemView.setOnClickListener(v -> {
                    callback.onSelected(options[position]);
                    bottomSheetDialog.dismiss();
                });
            }

            @Override
            public int getItemCount() {
                return options.length;
            }
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
        
        String savedCountry = prefs.getString(KEY_COUNTRY, "");
        if (!TextUtils.isEmpty(savedCountry)) {
            tvSelectedCountry.setText(savedCountry);
        }

        String savedCity = prefs.getString("city", "");
        if (!TextUtils.isEmpty(savedCity)) {
            tvSelectedCity.setText(savedCity);
        }

        String savedDistrict = prefs.getString(KEY_DISTRICT, "");
        if (!TextUtils.isEmpty(savedDistrict)) {
            tvSelectedDistrict.setText(savedDistrict);
        }

        etAddress.setText(prefs.getString(KEY_ADDRESS, ""));
        
        // Pre-fill if name is not empty
        if (!TextUtils.isEmpty(etFullName.getText())) {
            cbSaveInfo.setChecked(true);
        }
    }

    private void setupListeners() {
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
                startActivity(new Intent(CheckoutActivity.this, PaymentCardsActivity.class));
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
            if (llCountrySelector != null) llCountrySelector.setVisibility(View.VISIBLE);
            sectionTitle.setVisibility(View.VISIBLE);
        });

        btnAction.setOnClickListener(v -> {
            if (!isPaymentStep) {
                if (containerAddressSelection.getVisibility() == View.VISIBLE) {
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
        if (tvSelectedCountry.getText().toString().contains("Choose")) {
            ToastUtils.showCustomToast(this, "Please select a country");
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
        editor.putString(KEY_COUNTRY, tvSelectedCountry.getText().toString());
        editor.putString("city", tvSelectedCity.getText().toString());
        editor.putString(KEY_DISTRICT, tvSelectedDistrict.getText().toString());
        editor.putString(KEY_ADDRESS, etAddress.getText().toString());
        editor.apply();
    }

    private void showPaymentStep() {
        isPaymentStep = true;
        containerShipping.setVisibility(View.GONE);
        containerAddressSelection.setVisibility(View.GONE);
        containerPayment.setVisibility(View.VISIBLE);
        
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
                    tvSelectedDistrict.getText().toString() + ", " +
                    tvSelectedCity.getText().toString();
            tvSummaryAddress.setText(addressText);
            tvSummaryPhone.setText(etPhone.getText().toString());
        }
        tvSummaryTotal.setText(totalPrice != null ? totalPrice : "");

        if (totalPrice != null) {
            btnAction.setText(getString(R.string.btn_place_order) + " (" + totalPrice + ")");
        } else {
            btnAction.setText(R.string.btn_place_order);
        }
    }

    private void showShippingStep() {
        isPaymentStep = false;
        containerShipping.setVisibility(View.VISIBLE);
        containerAddressSelection.setVisibility(View.GONE);
        containerPayment.setVisibility(View.GONE);
        sectionTitle.setVisibility(View.VISIBLE);
        sectionTitle.setText(R.string.checkout_shipping_title);
        if (totalPrice != null) {
            btnAction.setText(getString(R.string.btn_continue_payment) + " (" + totalPrice + ")");
        } else {
            btnAction.setText(R.string.btn_continue_payment);
        }
    }

    private void processOrder() {
        if (!rbCod.isChecked() && !rbBank.isChecked() && !rbMomo.isChecked() && !rbZaloPay.isChecked()) {
            ToastUtils.showCustomToast(this, "Please select a payment method");
            return;
        }

        showSuccessDialog();
    }

    private void showSuccessDialog() {
        Intent intent = new Intent(CheckoutActivity.this, PaymentSuccessActivity.class);
        startActivity(intent);
        finish();
    }
}
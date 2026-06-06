package com.example.saive.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Address;
import com.example.saive.utils.ToastUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AddAddressActivity extends BaseActivity {

    private EditText etName, etPhone, etStreet;
    private AutoCompleteTextView etCity, etDistrict;
    private TextView chipHome, chipOffice, chipOther, tvTitle, tvSelectedCountry;
    private View btnCountrySelector;
    private CheckBox cbDefault;
    private String selectedLabel = "Home";
    private String selectedCountry = "Vietnam";
    private Address editAddress;
    
    private static final String PREFS_NAME = "address_prefs";
    private static final String ADDRESS_KEY = "saved_addresses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMaroon));
            getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }

        tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etStreet = findViewById(R.id.etStreet);
        etCity = findViewById(R.id.etCity);
        etDistrict = findViewById(R.id.etDistrict);
        tvSelectedCountry = findViewById(R.id.tvSelectedCountry);
        btnCountrySelector = findViewById(R.id.btnCountrySelector);
        cbDefault = findViewById(R.id.cbDefault);
        
        chipHome = findViewById(R.id.chipHome);
        chipOffice = findViewById(R.id.chipOffice);
        chipOther = findViewById(R.id.chipOther);

        setupAutocomplete();
        setupCountrySelector();

        editAddress = (Address) getIntent().getSerializableExtra("edit_address");
        if (editAddress != null) {
            tvTitle.setText(R.string.address_edit_title);
            fillData(editAddress);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        chipHome.setOnClickListener(v -> selectLabel("Home"));
        chipOffice.setOnClickListener(v -> selectLabel("Office"));
        chipOther.setOnClickListener(v -> selectLabel("Other"));

        findViewById(R.id.btnSaveAddress).setOnClickListener(v -> saveAddress());
    }

    private void setupAutocomplete() {
        String[] cities = getResources().getStringArray(R.array.cities_vn_array);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        etCity.setAdapter(cityAdapter);

        // Update districts based on city selection
        etCity.setOnItemClickListener((parent, view, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            updateDistrictAdapter(selection);
        });
    }

    private void updateDistrictAdapter(String city) {
        String[] districts;
        if (city.contains("Ho Chi Minh") || city.contains("Hồ Chí Minh")) {
            districts = getResources().getStringArray(R.array.districts_hcm_array);
        } else if (city.contains("Hanoi") || city.contains("Hà Nội")) {
            districts = getResources().getStringArray(R.array.districts_hanoi_array);
        } else {
            districts = new String[]{"District 1", "District 2", "District 3"};
        }
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, districts);
        etDistrict.setAdapter(districtAdapter);
    }

    private void setupCountrySelector() {
        if (btnCountrySelector != null) {
            btnCountrySelector.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showCountryDialog();
            });
        }
    }

    private void showCountryDialog() {
        String[] countries = getResources().getStringArray(R.array.countries_array);
        
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null, false);
        
        TextView tvTitle = view.findViewById(R.id.tvSheetTitle);
        tvTitle.setText(R.string.label_country);
        
        RecyclerView rvOptions = view.findViewById(R.id.rvSheetOptions);
        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        
        rvOptions.setAdapter(new RecyclerView.Adapter<OptionViewHolder>() {
            @androidx.annotation.NonNull
            @Override
            public OptionViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.item_bottom_sheet_option, parent, false);
                return new OptionViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@androidx.annotation.NonNull OptionViewHolder holder, int position) {
                holder.tvName.setText(countries[position]);
                holder.itemView.setOnClickListener(v -> {
                    selectedCountry = countries[position];
                    tvSelectedCountry.setText(selectedCountry);
                    bottomSheetDialog.dismiss();
                });
            }

            @Override
            public int getItemCount() {
                return countries.length;
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private static class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        OptionViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvOptionName);
        }
    }

    private void fillData(Address address) {
        etName.setText(address.getFullName());
        
        // Handle +84 prefix
        String phone = address.getPhoneNumber();
        if (phone != null && phone.startsWith("+84")) {
            etPhone.setText(phone.substring(3));
        } else {
            etPhone.setText(phone);
        }

        etStreet.setText(address.getStreetAddress());
        etCity.setText(address.getCity());
        etDistrict.setText(address.getDistrict());
        cbDefault.setChecked(address.isDefault());
        selectLabel(address.getLabel());
        
        if (address.getCountry() != null) {
            selectedCountry = address.getCountry();
            tvSelectedCountry.setText(selectedCountry);
        }
    }

    private void selectLabel(String label) {
        selectedLabel = label;
        updateChips();
    }

    private void updateChips() {
        int activeBg = getResources().getColor(R.color.colorMaroon);
        int inactiveBg = getResources().getColor(R.color.colorLinen);
        int activeText = getResources().getColor(R.color.white);
        int inactiveText = getResources().getColor(R.color.colorSand);

        chipHome.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedLabel.equals("Home") ? activeBg : inactiveBg));
        chipHome.setTextColor(selectedLabel.equals("Home") ? activeText : inactiveText);

        chipOffice.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedLabel.equals("Office") ? activeBg : inactiveBg));
        chipOffice.setTextColor(selectedLabel.equals("Office") ? activeText : inactiveText);

        chipOther.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedLabel.equals("Other") ? activeBg : inactiveBg));
        chipOther.setTextColor(selectedLabel.equals("Other") ? activeText : inactiveText);
    }

    private void saveAddress() {
        String name = etName.getText().toString().trim();
        String phonePart = etPhone.getText().toString().trim();
        String street = etStreet.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        boolean isDefault = cbDefault.isChecked();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.error_required_field));
            return;
        }
        if (TextUtils.isEmpty(phonePart)) {
            etPhone.setError(getString(R.string.error_required_field));
            return;
        }
        if (phonePart.length() < 9) {
            etPhone.setError("Vui lòng nhập 9 chữ số");
            return;
        }
        if (TextUtils.isEmpty(city)) {
            etCity.setError(getString(R.string.error_required_field));
            return;
        }
        if (TextUtils.isEmpty(district)) {
            etDistrict.setError(getString(R.string.error_required_field));
            return;
        }
        if (TextUtils.isEmpty(street)) {
            etStreet.setError(getString(R.string.error_required_field));
            return;
        }

        String fullPhone = "+84" + phonePart;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(ADDRESS_KEY, null);
        List<Address> addressList = new ArrayList<>();
        Gson gson = new Gson();
        
        if (json != null) {
            Type type = new TypeToken<ArrayList<Address>>() {}.getType();
            addressList = gson.fromJson(json, type);
        }

        if (isDefault) {
            for (Address a : addressList) {
                a.setDefault(false);
            }
        }

        if (editAddress != null) {
            // Update existing
            for (int i = 0; i < addressList.size(); i++) {
                if (addressList.get(i).getId().equals(editAddress.getId())) {
                    Address updated = new Address(editAddress.getId(), selectedLabel, name, fullPhone, street, city, district, isDefault);
                    updated.setCountry(selectedCountry);
                    addressList.set(i, updated);
                    break;
                }
            }
        } else {
            // Add new
            String id = UUID.randomUUID().toString();
            Address newAddr = new Address(id, selectedLabel, name, fullPhone, street, city, district, isDefault);
            newAddr.setCountry(selectedCountry);
            addressList.add(newAddr);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ADDRESS_KEY, gson.toJson(addressList));
        editor.commit(); // Sử dụng commit để đảm bảo dữ liệu được lưu ngay lập tức trước khi finish

        setResult(RESULT_OK);
        finish();
    }
}
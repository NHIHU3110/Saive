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
    private TextView tvSelectedCity, tvSelectedDistrict, tvSelectedWard;
    private TextView chipHome, chipOffice, chipOther, tvTitle;
    private View btnCitySelector, btnDistrictSelector, btnWardSelector;
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
        tvSelectedCity = findViewById(R.id.tvSelectedCity);
        tvSelectedDistrict = findViewById(R.id.tvSelectedDistrict);
        tvSelectedWard = findViewById(R.id.tvSelectedWard);
        btnCitySelector = findViewById(R.id.btnCitySelector);
        btnDistrictSelector = findViewById(R.id.btnDistrictSelector);
        btnWardSelector = findViewById(R.id.btnWardSelector);
        cbDefault = findViewById(R.id.cbDefault);
        
        chipHome = findViewById(R.id.chipHome);
        chipOffice = findViewById(R.id.chipOffice);
        chipOther = findViewById(R.id.chipOther);

        setupSelectors();

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

    private void setupSelectors() {
        btnCitySelector.setOnClickListener(v -> showLocationDialog("City", com.example.saive.utils.LocationProvider.getProvinces(this)));

        btnDistrictSelector.setOnClickListener(v -> {
            String city = tvSelectedCity.getText().toString();
            if (city.equals("Chọn tỉnh thành")) {
                ToastUtils.showCustomToast(this, "Vui lòng chọn tỉnh thành trước");
                return;
            }
            showLocationDialog("District", com.example.saive.utils.LocationProvider.getDistricts(this, city));
        });

        btnWardSelector.setOnClickListener(v -> {
            String city = tvSelectedCity.getText().toString();
            String district = tvSelectedDistrict.getText().toString();
            if (district.equals("Chọn quận huyện")) {
                ToastUtils.showCustomToast(this, "Vui lòng chọn quận huyện trước");
                return;
            }
            showLocationDialog("Ward", com.example.saive.utils.LocationProvider.getWards(this, city, district));
        });
    }

    private void showLocationDialog(String type, List<String> options) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null, false);

        TextView tvTitle = view.findViewById(R.id.tvSheetTitle);
        if (type.equals("City")) tvTitle.setText("Chọn tỉnh thành");
        else if (type.equals("District")) tvTitle.setText("Chọn quận huyện");
        else tvTitle.setText("Chọn phường xã");

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
                String option = options.get(position);
                holder.tvName.setText(option);
                holder.itemView.setOnClickListener(v -> {
                    if (type.equals("City")) {
                        tvSelectedCity.setText(option);
                        tvSelectedDistrict.setText("Chọn quận huyện");
                        tvSelectedWard.setText("Choose your ward/commune");
                    } else if (type.equals("District")) {
                        tvSelectedDistrict.setText(option);
                        tvSelectedWard.setText("Choose your ward/commune");
                    } else {
                        tvSelectedWard.setText(option);
                    }
                    bottomSheetDialog.dismiss();
                });
            }

            @Override
            public int getItemCount() {
                return options.size();
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
        tvSelectedCity.setText(address.getCity());
        tvSelectedDistrict.setText(address.getDistrict());
        tvSelectedWard.setText(address.getWard());
        cbDefault.setChecked(address.isDefault());
        selectLabel(address.getLabel());
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
        String city = tvSelectedCity.getText().toString().trim();
        String district = tvSelectedDistrict.getText().toString().trim();
        String ward = tvSelectedWard.getText().toString().trim();
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
        if (city.equals("Chọn tỉnh thành")) {
            ToastUtils.showCustomToast(this, "Vui lòng chọn tỉnh thành");
            return;
        }
        if (district.equals("Chọn quận huyện")) {
            ToastUtils.showCustomToast(this, "Vui lòng chọn quận huyện");
            return;
        }
        if (ward.equals("Choose your ward/commune")) {
            ToastUtils.showCustomToast(this, "Vui lòng chọn phường xã");
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
                    Address updated = new Address(editAddress.getId(), selectedLabel, name, fullPhone, street, ward, district, city, isDefault);
                    updated.setCountry(selectedCountry);
                    addressList.set(i, updated);
                    break;
                }
            }
        } else {
            // Add new
            String id = UUID.randomUUID().toString();
            Address newAddr = new Address(id, selectedLabel, name, fullPhone, street, ward, district, city, isDefault);
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
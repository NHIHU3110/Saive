package com.example.saive.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.Address;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddAddressActivity extends BaseActivity {

    private EditText etName, etPhone, etStreet, etCity, etDistrict;
    private TextView chipHome, chipOffice, chipOther, tvTitle;
    private CheckBox cbDefault;
    private String selectedLabel = "Home";
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
        cbDefault = findViewById(R.id.cbDefault);
        
        chipHome = findViewById(R.id.chipHome);
        chipOffice = findViewById(R.id.chipOffice);
        chipOther = findViewById(R.id.chipOther);

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

    private void fillData(Address address) {
        etName.setText(address.getFullName());
        etPhone.setText(address.getPhoneNumber());
        etStreet.setText(address.getStreetAddress());
        etCity.setText(address.getCity());
        etDistrict.setText(address.getDistrict());
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
        String phone = etPhone.getText().toString().trim();
        String street = etStreet.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        boolean isDefault = cbDefault.isChecked();

        if (name.isEmpty() || phone.isEmpty() || street.isEmpty()) {
            showCustomToast("Please fill all required fields");
            return;
        }

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
                    addressList.set(i, new Address(editAddress.getId(), selectedLabel, name, phone, street, city, district, isDefault));
                    break;
                }
            }
        } else {
            // Add new
            String id = UUID.randomUUID().toString();
            addressList.add(new Address(id, selectedLabel, name, phone, street, city, district, isDefault));
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ADDRESS_KEY, gson.toJson(addressList));
        editor.commit(); // Sử dụng commit để đảm bảo dữ liệu được lưu ngay lập tức trước khi finish

        setResult(RESULT_OK);
        finish();
    }
}
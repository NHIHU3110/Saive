package com.example.saive.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.LocationSuggestionAdapter;
import com.example.saive.base.BaseActivity;
import com.example.saive.models.LocationSuggestion;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class LocationPickerActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "LocationPickerActivity";
    private EditText etSearch;
    private ImageView btnClear;
    private RecyclerView rvSearchResults;
    private LocationSuggestionAdapter adapter;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private String currentSelectedAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        // Initialize Places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);
        sessionToken = AutocompleteSessionToken.newInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        etSearch = findViewById(R.id.etSearch);
        btnClear = findViewById(R.id.btnClear);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocationSuggestionAdapter();
        rvSearchResults.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            rvSearchResults.setVisibility(View.GONE);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No-op
    }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                if (s.length() > 2) {
                    searchPlaces(s.toString());
                } else {
                    rvSearchResults.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
        // No-op
    }
        });

        adapter.setOnItemClickListener(suggestion -> {
            rvSearchResults.setVisibility(View.GONE);
            fetchPlaceDetails(suggestion.getPlaceId());
        });

        findViewById(R.id.btnCurrentLocation).setOnClickListener(v -> getCurrentLocation());

        findViewById(R.id.btnConfirmLocation).setOnClickListener(v -> {
            if (!currentSelectedAddress.isEmpty()) {
                returnLocation(currentSelectedAddress);
            } else {
                Toast.makeText(this, R.string.error_select_location, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Mặc định di chuyển đến Việt Nam
        LatLng vietnam = new LatLng(10.762622, 106.660172); 
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vietnam, 15f));

        mMap.setOnCameraIdleListener(() -> {
            LatLng center = mMap.getCameraPosition().target;
            getAddressFromLatLng(center);
        });
    }

    private void searchPlaces(String query) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            List<LocationSuggestion> suggestions = new ArrayList<>();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                suggestions.add(new LocationSuggestion(
                        prediction.getPlaceId(),
                        prediction.getPrimaryText(null).toString(),
                        prediction.getSecondaryText(null).toString()
                ));
            }
            adapter.setSuggestions(suggestions);
            rvSearchResults.setVisibility(suggestions.isEmpty() ? View.GONE : View.VISIBLE);
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Autocomplete error: " + exception.getMessage());
        });
    }

    private void fetchPlaceDetails(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
            Place place = response.getPlace();
            if (place.getLatLng() != null && mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17f));
                currentSelectedAddress = place.getAddress();
                etSearch.setText(place.getAddress());
            }
        });
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                currentSelectedAddress = addresses.get(0).getAddressLine(0);
                etSearch.setText(currentSelectedAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null && mMap != null) {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 17f));
            }
        });
    }

    private void returnLocation(String address) {
        Intent data = new Intent();
        data.putExtra("selected_address", address);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }
}

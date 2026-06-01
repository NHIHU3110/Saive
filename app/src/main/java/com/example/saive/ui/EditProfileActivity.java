package com.example.saive.ui;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.adapters.BottomSheetOptionAdapter;
import com.example.saive.base.BaseActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class EditProfileActivity extends BaseActivity {

    private static final String USER_PREFS = "user_prefs";

    private ShapeableImageView ivAvatar;
    private TextView tvDob, tvGender;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}

                    selectedImageUri = uri;

                    try {
                        android.graphics.Bitmap bmp = decodeSampledBitmap(uri, 300, 300);
                        ivAvatar.setImageBitmap(bmp);
                    } catch (Exception e) {
                        ivAvatar.setImageURI(uri);
                    }

                    getSharedPreferences(USER_PREFS, MODE_PRIVATE)
                            .edit()
                            .putString("avatar_uri", uri.toString())
                            .apply();
                }
            });

    private android.graphics.Bitmap decodeSampledBitmap(Uri uri, int reqW, int reqH) throws Exception {
        android.graphics.BitmapFactory.Options opt = new android.graphics.BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        try (java.io.InputStream is = getContentResolver().openInputStream(uri)) {
            android.graphics.BitmapFactory.decodeStream(is, null, opt);
        }
        int sample = 1;
        if (opt.outHeight > reqH || opt.outWidth > reqW) {
            int hh = opt.outHeight / 2, hw = opt.outWidth / 2;
            while ((hh / sample) >= reqH && (hw / sample) >= reqW) sample *= 2;
        }
        opt.inSampleSize = sample;
        opt.inJustDecodeBounds = false;
        try (java.io.InputStream is = getContentResolver().openInputStream(uri)) {
            return android.graphics.BitmapFactory.decodeStream(is, null, opt);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerContainer), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(),
                    sys.top + (int)(14 * getResources().getDisplayMetrics().density),
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ivAvatar = findViewById(R.id.ivAvatar);
        tvDob    = findViewById(R.id.tvDob);
        tvGender = findViewById(R.id.tvGender);

        com.example.saive.utils.ImageUtils.setSafeImage(ivAvatar, R.mipmap.model1);
        loadSavedData();

        // Back
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            finish();
        });

        // Save bottom — dob/gender đã lưu realtime, chỉ cần finish
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Avatar
        android.view.View.OnClickListener openGallery = v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            pickImageLauncher.launch("image/*");
        };
        findViewById(R.id.btnEditAvatar).setOnClickListener(openGallery);
        findViewById(R.id.tvChangePhoto).setOnClickListener(openGallery);

        // ── MaterialDatePicker thay DatePickerDialog ──────
        findViewById(R.id.btnPickDob).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            // Tính selection mặc định
            long selectedMs = System.currentTimeMillis();
            String saved = getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString("dob", "");
            if (!saved.isEmpty()) {
                try {
                    String[] p = saved.split("/");
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    cal.set(Integer.parseInt(p[2]),
                            Integer.parseInt(p[1]) - 1,
                            Integer.parseInt(p[0]), 0, 0, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    selectedMs = cal.getTimeInMillis();
                } catch (Exception ignored) {}
            }

            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Birthday")
                    .setSelection(selectedMs)
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.setTimeInMillis(selection);
                String dob = String.format("%02d/%02d/%d",
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.YEAR));

                // Cập nhật UI
                tvDob.setText(dob);
                tvDob.setTextColor(getColor(R.color.colorNoirBlack));

                // Lưu ngay
                getSharedPreferences(USER_PREFS, MODE_PRIVATE)
                        .edit()
                        .putString("dob", dob)
                        .apply();

                Toast.makeText(this, "Birthday saved: " + dob, Toast.LENGTH_SHORT).show();
            });

            picker.show(getSupportFragmentManager(), "DOB_PICKER");
        });

        // Gender Picker
        findViewById(R.id.btnPickGender).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            List<String> options = Arrays.asList("Male", "Female", "Other", "Prefer not to say");
            String current = tvGender.getText().toString();

            BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
            android.view.View sheet = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null);
            dialog.setContentView(sheet);

            ((TextView) sheet.findViewById(R.id.tvSheetTitle)).setText(getString(R.string.profile_gender));

            RecyclerView rv = sheet.findViewById(R.id.rvSheetOptions);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new BottomSheetOptionAdapter(options, current, option -> {
                tvGender.setText(option);
                tvGender.setTextColor(getColor(R.color.colorNoirBlack));

                getSharedPreferences(USER_PREFS, MODE_PRIVATE)
                        .edit()
                        .putString("gender", option)
                        .apply();

                dialog.dismiss();
            }));

            dialog.show();
        });
    }

    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);

        String dob = prefs.getString("dob", "");
        if (!dob.isEmpty()) {
            tvDob.setText(dob);
            tvDob.setTextColor(getColor(R.color.colorNoirBlack));
        }

        String gender = prefs.getString("gender", "");
        if (!gender.isEmpty()) {
            tvGender.setText(gender);
            tvGender.setTextColor(getColor(R.color.colorNoirBlack));
        }

        String avatarUri = prefs.getString("avatar_uri", "");
        if (!avatarUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(avatarUri);
                android.graphics.Bitmap bmp = decodeSampledBitmap(uri, 300, 300);
                ivAvatar.setImageBitmap(bmp);
            } catch (Exception ignored) {}
        }
    }

    protected void navigateToMain(String sectionExtra) {}
}
package com.example.saive.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.saive.adapters.BottomSheetOptionAdapter;
import java.util.Arrays;
import java.util.List;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.saive.R;
import com.example.saive.base.BaseActivity;
import com.example.saive.utils.DialogUtils;

import java.util.Locale;

public class ProfileActivity extends BaseActivity {

    private View notificationBadge;
    private com.google.android.material.button.MaterialButton btnLogoutNew;
    private TextView tvCurrentLanguage;
    private static final String PREFS_NAME = "notification_prefs";
    private static final String USER_PREFS = "user_prefs";
    private static final String LANG_PREFS = "language_prefs";
    private static final String LANG_KEY = "selected_language";

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
        updateAuthUI();
    }

    private void updateAuthUI() {
        SharedPreferences prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (btnLogoutNew != null) {
            if (isLoggedIn) {
                btnLogoutNew.setText(R.string.menu_logout);
            } else {
                btnLogoutNew.setText(R.string.menu_login);
            }
        }

        // Update current language display
        if (tvCurrentLanguage != null) {
            SharedPreferences langPrefs = getSharedPreferences(LANG_PREFS, MODE_PRIVATE);
            String currentLang = langPrefs.getString(LANG_KEY, "en");
            if (currentLang.equals("vi")) tvCurrentLanguage.setText(R.string.lang_vi);
            else if (currentLang.equals("zh")) tvCurrentLanguage.setText(R.string.lang_zh);
            else tvCurrentLanguage.setText(R.string.lang_en);
        }
    }

    private void updateNotificationBadge() {
        if (notificationBadge == null) return;
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        
        String[] ids = {"drop_1", "order_1", "capsule_1", "reminder_1"};
        boolean[] defaultRead = {false, false, true, true};
        
        boolean hasUnread = false;
        for (int i = 0; i < ids.length; i++) {
            if (!prefs.getBoolean("read_" + ids[i], defaultRead[i])) {
                hasUnread = true;
                break;
            }
        }
        notificationBadge.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
    }

    private void showLanguageDialog() {
        List<String> languages = Arrays.asList(getString(R.string.lang_en), getString(R.string.lang_vi), getString(R.string.lang_zh));
        List<String> langCodes = Arrays.asList("en", "vi", "zh");

        SharedPreferences prefs = getSharedPreferences(LANG_PREFS, MODE_PRIVATE);
        String currentLang = prefs.getString(LANG_KEY, "en");
        
        String currentLangName = getString(R.string.lang_en);
        if (currentLang.equals("vi")) currentLangName = getString(R.string.lang_vi);
        else if (currentLang.equals("zh")) currentLangName = getString(R.string.lang_zh);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView tvTitle = sheetView.findViewById(R.id.tvSheetTitle);
        tvTitle.setText(R.string.menu_language);

        RecyclerView rvOptions = sheetView.findViewById(R.id.rvSheetOptions);
        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        
        BottomSheetOptionAdapter adapter = new BottomSheetOptionAdapter(languages, currentLangName, option -> {
            int index = languages.indexOf(option);
            String selectedLang = langCodes.get(index);
            if (!selectedLang.equals(currentLang)) {
                setLocale(selectedLang);
            }
            bottomSheetDialog.dismiss();
        });
        rvOptions.setAdapter(adapter);

        bottomSheetDialog.show();
    }

    private void setLocale(String langCode) {
        SharedPreferences.Editor editor = getSharedPreferences(LANG_PREFS, MODE_PRIVATE).edit();
        editor.putString(LANG_KEY, langCode);
        editor.apply();

        // Show loading screen while switching language
        Intent intent = new Intent(this, LanguageLoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void navigateToMain(String sectionExtra) {
        Intent intent = new Intent(this, MainActivity.class);
        if (sectionExtra != null) {
            intent.putExtra(sectionExtra, true);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(LANG_PREFS, MODE_PRIVATE);
        String lang = prefs.getString(LANG_KEY, "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);
        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        View profileScroll = findViewById(R.id.profileScroll);
        ViewCompat.setOnApplyWindowInsetsListener(profileScroll, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            View headerContainer = findViewById(R.id.headerContainer);
            if (headerContainer != null) {
                headerContainer.setPadding(headerContainer.getPaddingLeft(), 
                        systemBars.top + (int)(14 * getResources().getDisplayMetrics().density),
                        headerContainer.getPaddingRight(), 
                        headerContainer.getPaddingBottom());
            }

            View bottomNav = findViewById(R.id.bottomNav);
            if (bottomNav != null) {
                bottomNav.setPadding(0, 0, 0, systemBars.bottom);
            }

            return insets;
        });

        // Navigation Items
        notificationBadge = findViewById(R.id.notificationBadge);
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);

        // Notifications Toggle
        SwitchCompat switchNotifications = findViewById(R.id.switchNotifications);
        if (switchNotifications != null) {
            switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
                buttonView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                String message = isChecked ? getString(R.string.notifications_enabled) : getString(R.string.notifications_disabled);
                showCustomToast(message);
            });
        }


        // Logout Action
        btnLogoutNew = findViewById(R.id.btnLogoutNew);

        if (btnLogoutNew != null) {
            btnLogoutNew.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                SharedPreferences prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

                if (isLoggedIn) {
                    DialogUtils.showCustomAlertDialog(
                            this,
                            getString(R.string.logout_title),
                            getString(R.string.logout_message),
                            getString(R.string.yes),
                            getString(R.string.no),
                            () -> {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("is_logged_in", false);
                                editor.apply();
                                showCustomToast(getString(R.string.toast_logged_out));
                                updateAuthUI();
                            }
                    );
                } else {
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Center Action Button (Now acts as Home button on Profile page)
        View centerActionButton = findViewById(R.id.centerActionButton);
        if (centerActionButton != null) {
            centerActionButton.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                navigateToMain("SHOW_HOME");
            });
        }

        // Notify Navigation Item
        View navNotify = findViewById(R.id.navNotify);
        if (navNotify != null) {
            navNotify.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                navigateToMain("SHOW_NOTIFICATIONS");
            });
        }

        // Wardrobe Navigation Item
        View navWardrobe = findViewById(R.id.navWardrobe);
        if (navWardrobe != null) {
            navWardrobe.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                navigateToMain("SHOW_WARDROBE");
            });
        }

        // Language Menu Item
        View btnLanguage = findViewById(R.id.btnLanguageNew);
        if (btnLanguage != null) {
            btnLanguage.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showLanguageDialog();
            });
        }

        // About SAIVE Menu Item
        View btnAbout = findViewById(R.id.btnAbout);
        if (btnAbout != null) {
            btnAbout.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(ProfileActivity.this, AboutActivity.class);
                startActivity(intent);
            });
        }

        // Delete Account Menu Item
        View btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        if (btnDeleteAccount != null) {
            btnDeleteAccount.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                DialogUtils.showCustomAlertDialog(
                        this,
                        getString(R.string.dialog_delete_account_title),
                        getString(R.string.dialog_delete_account_message),
                        getString(R.string.dialog_delete_confirm),
                        getString(R.string.dialog_cancel),
                        () -> {
                            SharedPreferences prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.clear(); // Clear all user data
                            editor.apply();

                            // Also clear address data
                            getSharedPreferences("address_prefs", MODE_PRIVATE).edit().clear().apply();

                            showCustomToast(getString(R.string.toast_account_deleted));
                            updateAuthUI();

                            // Return to login or splash
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                );
            });
        }

        // Shipping Address Menu Item
        View btnAddress = findViewById(R.id.btnAddressNew);
        if (btnAddress != null) {
            btnAddress.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(ProfileActivity.this, AddressListActivity.class);
                startActivity(intent);
            });
        }

        // My Orders Menu Item
        View btnMyOrders = findViewById(R.id.btnMyOrdersNew);
        if (btnMyOrders != null) {
            btnMyOrders.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(ProfileActivity.this, MyOrdersActivity.class);
                startActivity(intent);
            });
        }

        // My Coupons Menu Item
        View btnMyCoupons = findViewById(R.id.btnMyCoupons);
        if (btnMyCoupons != null) {
            btnMyCoupons.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(ProfileActivity.this, CouponActivity.class);
                startActivity(intent);
            });
        }

        // Saved Collections Menu Item
        View btnSavedCollections = findViewById(R.id.btnSavedCollections);
        if (btnSavedCollections != null) {
            btnSavedCollections.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                navigateToMain("SHOW_FAVORITES");
            });
        }

        // Favorite Navigation Item
        View navFavorite = findViewById(R.id.navFavorite);
        if (navFavorite != null) {
            navFavorite.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                navigateToMain("SHOW_FAVORITES");
            });
        }
    }
}
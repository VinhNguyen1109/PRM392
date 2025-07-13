package com.example.familynoteapp.feture.theme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.familynoteapp.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);

        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setChecked(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked != isDarkMode) {
                // Lưu lại
                prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();

                // Thông báo
                Toast.makeText(this, "Đang áp dụng giao diện...", Toast.LENGTH_SHORT).show();

                // Áp dụng theme mới
                AppCompatDelegate.setDefaultNightMode(
                        isChecked ? AppCompatDelegate.MODE_NIGHT_YES
                                : AppCompatDelegate.MODE_NIGHT_NO
                );

                // Kết thúc activity để apply
                finish();
            }
        });
    }
}

package com.example.ultranote.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ultranote.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import settings.UserSettings;

public class Settings extends AppCompatActivity {

    private View settingsParent;
    private SwitchMaterial themeSwitch;
    private TextView settingsText, themeText;

    private UserSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        settings = (UserSettings) getApplication();

        initComponents();
        initSwitchListener();
        loadSharedPreferences();
        updateApplication();
    }

    private void initComponents() {
        themeText = findViewById(R.id.themeText);
        settingsText = findViewById(R.id.settingsText);
        themeSwitch = findViewById(R.id.themeSwitch);
        settingsParent = findViewById(R.id.settings);
    }

    private void initSwitchListener() {
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    settings.setCustomTheme(UserSettings.LIGHT_THEME);
                } else {
                    settings.setCustomTheme(UserSettings.DARK_THEME);
                }

                SharedPreferences.Editor editor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
                editor.apply();
                updateApplication();
            }
        });
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
        String theme = sharedPreferences.getString(UserSettings.CUSTOM_THEME, UserSettings.DARK_THEME);
        settings.setCustomTheme(theme);
    }

    private void updateApplication() {
        final int dark = ContextCompat.getColor(this, R.color.primaryColour);
        final int black = ContextCompat.getColor(this, R.color.black);
        final int white = ContextCompat.getColor(this, R.color.white);

        if (settings.getCustomTheme().equals(UserSettings.LIGHT_THEME)) {
            settingsText.setTextColor(black);
            themeText.setTextColor(black);
            themeText.setText("Light mode");
            settingsParent.setBackgroundColor(white);
            themeSwitch.setChecked(true);
            return;
        }

        settingsText.setTextColor(white);
        themeText.setTextColor(white);
        themeText.setText("Dark mode");
        settingsParent.setBackgroundColor(dark);
        themeSwitch.setChecked(false);
    }
}
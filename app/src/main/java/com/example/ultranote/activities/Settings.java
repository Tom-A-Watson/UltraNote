package com.example.ultranote.activities;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.ultranote.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import settings.UserSettings;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    private View settingsParent;
    private SwitchMaterial themeSwitch;
    private TextView settingsText, themeText, displaySettingsText;
    private ImageView backButton, separator;

    private UserSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        initComponents();
        initSwitchListener();
        loadSharedPreferences();
        updateView();

        findViewById(R.id.backButton).setOnClickListener(this);
    }

    private void initComponents() {
        settings = (UserSettings) getApplication();
        displaySettingsText = findViewById(R.id.displaySettingsText);
        separator = findViewById(R.id.separator);
        themeText = findViewById(R.id.themeText);
        settingsText = findViewById(R.id.settingsText);
        themeSwitch = findViewById(R.id.themeSwitch);
        backButton = findViewById(R.id.backButton);
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

                SharedPreferences.Editor sharedPrefEditor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
                sharedPrefEditor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
                sharedPrefEditor.apply();
                updateView();
            }
        });
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
        String theme = sharedPreferences.getString(UserSettings.CUSTOM_THEME, UserSettings.DARK_THEME);
        settings.setCustomTheme(theme);
    }

    private void updateView() {
        final int darkGrey = ContextCompat.getColor(this, R.color.primaryColour);
        final int offWhite = ContextCompat.getColor(this, R.color.offWhite);
        final int black = ContextCompat.getColor(this, R.color.black);
        final int white = ContextCompat.getColor(this, R.color.white);

        if (settings.getCustomTheme().equals(UserSettings.LIGHT_THEME)) {
            backButton.setColorFilter(black);
            settingsText.setTextColor(black);
            displaySettingsText.setTextColor(darkGrey);
            separator.setBackgroundColor(darkGrey);
            themeText.setTextColor(black);
            themeText.setText("Light Mode");
            settingsParent.setBackgroundColor(white);
            themeSwitch.setChecked(true);
            return;
        }

        backButton.setColorFilter(white);
        settingsText.setTextColor(white);
        displaySettingsText.setTextColor(offWhite);
        separator.setBackgroundColor(offWhite);
        themeText.setTextColor(white);
        themeText.setText("Dark Mode");
        settingsParent.setBackgroundColor(darkGrey);
        themeSwitch.setChecked(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Simple 1-line implementations
            case R.id.backButton: onBackPressed(); break;
        }
    }
}
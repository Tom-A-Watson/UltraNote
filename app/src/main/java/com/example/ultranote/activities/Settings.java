package com.example.ultranote.activities;

import android.content.SharedPreferences;
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
    private TextView settingsText, themeText;
    private ImageView backButton;

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

                SharedPreferences.Editor editor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
                editor.apply();
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
        final int dark = ContextCompat.getColor(this, R.color.primaryColour);
        final int black = ContextCompat.getColor(this, R.color.black);
        final int white = ContextCompat.getColor(this, R.color.white);

        if (settings.getCustomTheme().equals(UserSettings.LIGHT_THEME)) {
            settingsText.setTextColor(black);
            themeText.setTextColor(black);
            themeText.setText("Light mode");
            backButton.setColorFilter(black);
            settingsParent.setBackgroundColor(white);
            themeSwitch.setChecked(true);
            return;
        }

        settingsText.setTextColor(white);
        themeText.setTextColor(white);
        themeText.setText("Dark mode");
        backButton.setColorFilter(white);
        settingsParent.setBackgroundColor(dark);
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
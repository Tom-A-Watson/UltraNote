package settings;

import android.app.Application;

public class UserSettings extends Application {

    public static final String PREFERENCES = "preferences";
    public static final String CUSTOM_THEME = "customTheme";
    public static final String DARK_THEME = "darkTheme";
    public static final String LIGHT_THEME = "lightTheme";

    private String customTheme;

    public String getCustomTheme() {
        return customTheme;
    }

    public void setCustomTheme(String customTheme) {
        this.customTheme = customTheme;
    }

}

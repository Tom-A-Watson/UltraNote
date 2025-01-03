package utilities;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import com.example.ultranote.R;

public class Utilities {

    Application app;

    public Utilities(Application app) {
        this.app = app;
    }

    public static Drawable[] initDarkColourButtons(Context context) {
        return new Drawable[] {
                ContextCompat.getDrawable(context, R.drawable.grey_btn),
                ContextCompat.getDrawable(context, R.drawable.red_btn),
                ContextCompat.getDrawable(context, R.drawable.orange_btn),
                ContextCompat.getDrawable(context, R.drawable.lightorange_btn),
                ContextCompat.getDrawable(context, R.drawable.yellow_btn),
                ContextCompat.getDrawable(context, R.drawable.lightgreen_btn),
                ContextCompat.getDrawable(context, R.drawable.green_btn),
                ContextCompat.getDrawable(context, R.drawable.lightblue_btn),
                ContextCompat.getDrawable(context, R.drawable.blue_btn),
                ContextCompat.getDrawable(context, R.drawable.indigo_btn),
                ContextCompat.getDrawable(context, R.drawable.purple_btn),
                ContextCompat.getDrawable(context, R.drawable.violet_btn),
                ContextCompat.getDrawable(context, R.drawable.lightmaroon_btn)
        };
    }

    public static Drawable[] initLightColourButtons(Context context) {
        return new Drawable[] {
                ContextCompat.getDrawable(context, R.drawable.grey_btn2),
                ContextCompat.getDrawable(context, R.drawable.red_btn2),
                ContextCompat.getDrawable(context, R.drawable.orange_btn2),
                ContextCompat.getDrawable(context, R.drawable.lightorange_btn2),
                ContextCompat.getDrawable(context, R.drawable.yellow_btn2),
                ContextCompat.getDrawable(context, R.drawable.lightgreen_btn2),
                ContextCompat.getDrawable(context, R.drawable.green_btn2),
                ContextCompat.getDrawable(context, R.drawable.lightblue_btn2),
                ContextCompat.getDrawable(context, R.drawable.blue_btn2),
                ContextCompat.getDrawable(context, R.drawable.indigo_btn2),
                ContextCompat.getDrawable(context, R.drawable.purple_btn2),
                ContextCompat.getDrawable(context, R.drawable.violet_btn2),
                ContextCompat.getDrawable(context, R.drawable.maroon_btn2)
        };
    }

    public String getPath(Uri contentUri) {
        String filePath;
        Cursor cursor = app.getContentResolver()
                .query(contentUri, null, null, null, null);

        if (cursor == null) { filePath = contentUri.getPath(); }
        else {
            cursor.moveToFirst();
            int i = cursor.getColumnIndex("_data");
            filePath = cursor.getString(i);
            cursor.close();
        }

        return filePath;
    }
}
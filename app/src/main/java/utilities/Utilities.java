package utilities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import com.example.ultranote.R;
import com.example.ultranote.activities.CreateNote;

public class Utilities {

    public static int darkGrey, lightGrey, offWhite, black, white, lightModeAccent, darkModeAccent;
    public static Drawable lightBlue, blue, lightInput, darkInput, saveBtnLight, saveBtnDark, light, dark;

    Application app;

    public Utilities(Application app) { 
        this.app = app;
    }

    public static void initGlobalColours(Context context) {
        darkGrey = ContextCompat.getColor(context, R.color.primaryColour);
        lightGrey = ContextCompat.getColor(context, R.color.primaryColourLight);
        offWhite = ContextCompat.getColor(context, R.color.offWhite);
        black = ContextCompat.getColor(context, R.color.black);
        white = ContextCompat.getColor(context, R.color.white);
        lightModeAccent = ContextCompat.getColor(context, R.color.noteColour9);
        darkModeAccent = ContextCompat.getColor(context, R.color.accent);
    }

    public static void initCreateNoteDrawables(Context context) {
        saveBtnLight = ContextCompat.getDrawable(context, R.drawable.done_button_light);
        saveBtnDark = ContextCompat.getDrawable(context, R.drawable.done_button);
        blue = ContextCompat.getDrawable(context, R.drawable.subtitle_indicator_light);
        lightBlue = ContextCompat.getDrawable(context, R.drawable.subtitle_indicator);
        light = ContextCompat.getDrawable(context, R.drawable.noteoptions_light_bg);
        dark = ContextCompat.getDrawable(context, R.drawable.noteoptions_bg);
    }

    public static void initHomeDrawables(Context context) {
        lightBlue = ContextCompat.getDrawable(context, R.drawable.add_note_button);
        blue = ContextCompat.getDrawable(context, R.drawable.add_note_button_light);
        lightInput = ContextCompat.getDrawable(context, R.drawable.quick_note_light_background);
        darkInput = ContextCompat.getDrawable(context, R.drawable.quick_note_background);
    }

    public static ImageView[] initColourButtons(Activity a) {
        return new ImageView[] {
                a.findViewById(R.id.imageColour1), a.findViewById(R.id.imageColour2),
                a.findViewById(R.id.imageColour3), a.findViewById(R.id.imageColour4),
                a.findViewById(R.id.imageColour5), a.findViewById(R.id.imageColour6),
                a.findViewById(R.id.imageColour7), a.findViewById(R.id.imageColour8),
                a.findViewById(R.id.imageColour9),  a.findViewById(R.id.imageColour10),
                a.findViewById(R.id.imageColour11), a.findViewById(R.id.imageColour12),
                a.findViewById(R.id.imageColour13)
        };
    }

    public static Drawable[] initDarkBGColourButtons(Context context) {
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

    public static Drawable[] initLightBGColourButtons(Context context) {
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

    public void selectImage(Activity activity, int requestCode) {
        Intent selectedImg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (selectedImg.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(selectedImg, requestCode);
        }
    }

    public void buildNoteWithImage(Uri uri, Context context, Activity activity, int requestCode) {
        String path = getPath(uri);
        Intent noteWithImage = new Intent(context, CreateNote.class);
        noteWithImage.putExtra("isFromQuickActions", true);
        noteWithImage.putExtra("quickActionType", "image");
        noteWithImage.putExtra("imagePath", path);
        activity.startActivityForResult(noteWithImage, requestCode);
    }

    public boolean compareToURLRegex(EditText e) {
        return !Patterns.WEB_URL.matcher(e.getText().toString()).matches();
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
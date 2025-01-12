package utilities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.example.ultranote.R;
import com.example.ultranote.activities.CreateNote;
import entities.Note;

public class Utilities {

    Application app;

    public static int darkGrey, lightGrey, offWhite, black, white, lightModeAccent, darkModeAccent;
    public static Drawable lightBlue, blue, lightInput, darkInput, saveBtnLight, saveBtnDark, light, dark;

    public static AlertDialog urlDialog;

    // States
    private static final int VISIBLE = View.VISIBLE;

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

    public static void initListeners(Activity a, View.OnClickListener listener, TextWatcher watcher) {
        EditText title = a.findViewById(R.id.noteTitleInput);
        EditText subtitle = a.findViewById(R.id.noteSubtitleInput);
        EditText content = a.findViewById(R.id.noteContent);
        a.findViewById(R.id.backButton).setOnClickListener(listener);
        a.findViewById(R.id.saveButton).setOnClickListener(listener);
        a.findViewById(R.id.removeTitle).setOnClickListener(listener);
        a.findViewById(R.id.addURL).setOnClickListener(listener);
        a.findViewById(R.id.deleteURL).setOnClickListener(listener);
        a.findViewById(R.id.addImage).setOnClickListener(listener);
        a.findViewById(R.id.deleteImage).setOnClickListener(listener);
        title.addTextChangedListener(watcher);
        subtitle.addTextChangedListener(watcher);
        content.addTextChangedListener(watcher);
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

    public void setNoteColourToSelected(CreateNote cn) {
        ImageView[] colours = initColourButtons(cn);

        for (int i = 0; i < colours.length; i++) {
            final int j = i;
            colours[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cn.selectColour(colours, j + 1);
                    switch (j) {
                      case 0: cn.setColourTo("#333333"); break;  case 1: cn.setColourTo("#FF2929"); break;
                      case 2: cn.setColourTo("#FF5722"); break;  case 3: cn.setColourTo("#FF9800"); break;
                      case 4: cn.setColourTo("#FFE719"); break;  case 5: cn.setColourTo("#8BC34A"); break;
                      case 6: cn.setColourTo("#4CAF50"); break;  case 7: cn.setColourTo("#00BCD4"); break;
                      case 8: cn.setColourTo("#2196F3"); break;  case 9: cn.setColourTo("#3F51B5"); break;
                      case 10: cn.setColourTo("#673AB7"); break; case 11: cn.setColourTo("#9C27B0"); break;
                      case 12: cn.setColourTo("#E91E63"); break;
                    }
                }
            });
        }
    }

    public void initColourPickerFor(Note note, CreateNote activity) {
        boolean existingNoteHasAColour = note != null && note.getColour() != null
                && !note.getColour().trim().isEmpty();

        if (existingNoteHasAColour) {
            switch (note.getColour()) {
                case "#333333": activity.setColourTo("#333333"); activity.switchTickToBtn(0); break;
                case "#FF2929": activity.setColourTo("#FF2929"); activity.switchTickToBtn(1); break;
                case "#FF5722": activity.setColourTo("#FF5722"); activity.switchTickToBtn(2); break;
                case "#FF9800": activity.setColourTo("#FF9800"); activity.switchTickToBtn(3); break;
                case "#FFE719": activity.setColourTo("#FFE719"); activity.switchTickToBtn(4); break;
                case "#8BC34A": activity.setColourTo("#8BC34A"); activity.switchTickToBtn(5); break;
                case "#4CAF50": activity.setColourTo("#4CAF50"); activity.switchTickToBtn(6); break;
                case "#00BCD4": activity.setColourTo("#00BCD4"); activity.switchTickToBtn(7); break;
                case "#2196F3": activity.setColourTo("#2196F3"); activity.switchTickToBtn(8); break;
                case "#3F51B5": activity.setColourTo("#3F51B5"); activity.switchTickToBtn(9); break;
                case "#673AB7": activity.setColourTo("#673AB7"); activity.switchTickToBtn(10); break;
                case "#9C27B0": activity.setColourTo("#9C27B0"); activity.switchTickToBtn(11); break;
                case "#E91E63": activity.setColourTo("#E91E63"); activity.switchTickToBtn(12); break;
            }
        }
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

    public static boolean inputDoesNotMatchURLRegex(EditText e) {
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

    public static AlertDialog showDialog(View view, AlertDialog.Builder builder, View.OnClickListener listener) {
        view.findViewById(R.id.confirmAddURL).setOnClickListener(listener);
        view.findViewById(R.id.cancelAddURL).setOnClickListener(listener);

        if (urlDialog == null) {
            builder.setView(view);
            urlDialog = builder.create();
            final Window addURLWindow = urlDialog.getWindow();

            if (addURLWindow != null) { addURLWindow.setBackgroundDrawable(new ColorDrawable(0)); }
        }

        urlDialog.show();
        return urlDialog;
    }

    public static void validateURL(Activity a, EditText e, AlertDialog dialog, int requestCode) {
        final TextView url = a.findViewById(R.id.webUrl);
        final LinearLayout urlLayout = a.findViewById(R.id.urlLayout);
        final Intent noteWithURL = new Intent(a.getApplicationContext(), CreateNote.class);
        e.requestFocus();

        if (e.getText().toString().trim().isEmpty()) {
            Toast.makeText(a,"Empty URL!", Toast.LENGTH_SHORT).show(); return;
        }
        if (inputDoesNotMatchURLRegex(e)) {
            Toast.makeText(a,"Invalid URL!", Toast.LENGTH_SHORT).show(); return;
        }

        dialog.dismiss();

        if (requestCode == 3) { // Perform URL embedding from within CreateNote
            url.setText(e.getText().toString()); urlLayout.setVisibility(VISIBLE); return;
        }

        // Otherwise do it from Home via Intent
        noteWithURL.putExtra("isFromQuickActions", true);
        noteWithURL.putExtra("quickActionType", "URL");
        noteWithURL.putExtra("URL", e.getText().toString());
        a.startActivityForResult(noteWithURL, requestCode);
    }
}
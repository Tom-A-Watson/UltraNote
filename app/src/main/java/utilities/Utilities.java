package utilities;

import static com.example.ultranote.activities.Home.ADD_NOTE;
import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.ultranote.R;
import com.example.ultranote.activities.CreateNote;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import adapters.NotesAdapter;
import entities.Note;
import settings.UserSettings;

public class Utilities {

    Application app;

    public static String singleLine, multiLineDate;
    private static String readExtStoragePerm;

    public static int darkGrey, lightGrey, offWhite, black, white, lightModeAccent, darkModeAccent;
    public static Drawable lightBlue, blue, lightInput, darkInput, saveBtnLight, saveBtnDark, light, dark;

    public static AlertDialog urlDialog, deleteDialog;
    public static AlertDialog.Builder builder;

    // Application constants
    public static final int GRANTED = PackageManager.PERMISSION_GRANTED;
    public static final int VISIBLE = View.VISIBLE;
    public static final int GONE = View.GONE;
    public static final int VERTICAL = StaggeredGridLayoutManager.VERTICAL;
    public static final int SHORT  = Toast.LENGTH_SHORT;
    public static final int LONG  = Toast.LENGTH_LONG;
    public static final int EXPANDED = BottomSheetBehavior.STATE_EXPANDED;
    public static final int COLLAPSED = BottomSheetBehavior.STATE_COLLAPSED;
    public static final int ENTER = KeyEvent.KEYCODE_ENTER;

    public Utilities(Application app) {
        this.app = app;
        readExtStoragePerm = Manifest.permission.READ_EXTERNAL_STORAGE;
        singleLine = new SimpleDateFormat(
                "EEEE dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date());
        multiLineDate = new SimpleDateFormat(
                "EEEE dd MMMM yyyy \nHH:mm a", Locale.getDefault()).format(new Date());
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

    public static void initCreateNoteListeners(Activity a, View.OnClickListener l, TextWatcher w) {
        EditText title = a.findViewById(R.id.noteTitleInput);
        EditText subtitle = a.findViewById(R.id.noteSubtitleInput);
        EditText content = a.findViewById(R.id.noteContent);
        a.findViewById(R.id.backButton).setOnClickListener(l);
        a.findViewById(R.id.saveButton).setOnClickListener(l);
        a.findViewById(R.id.removeTitle).setOnClickListener(l);
        a.findViewById(R.id.addURL).setOnClickListener(l);
        a.findViewById(R.id.deleteURL).setOnClickListener(l);
        a.findViewById(R.id.addImage).setOnClickListener(l);
        a.findViewById(R.id.deleteImage).setOnClickListener(l);
        title.addTextChangedListener(w);
        subtitle.addTextChangedListener(w);
        content.addTextChangedListener(w);
    }

    public static void initHomeListeners(Activity a, View.OnClickListener l, TextWatcher w) {
        EditText searchInput = a.findViewById(R.id.searchNotesInput);
        a.findViewById(R.id.backButton).setOnClickListener(l);
        a.findViewById(R.id.qAddImg).setOnClickListener(l);
        a.findViewById(R.id.addURL).setOnClickListener(l);
        searchInput.addTextChangedListener(w);
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

    public void notifyAdapterOfUpdateOrDelete(NotesAdapter a, List<Note> list, List<Note> db,
                                              boolean noteIsDeleted, int clickedNoteIndex) {
        list.remove(clickedNoteIndex);
        if (noteIsDeleted) { a.notifyItemRemoved(clickedNoteIndex); }
        else {
            list.add(clickedNoteIndex, db.get(clickedNoteIndex));
            a.notifyItemChanged(clickedNoteIndex);
        }
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

    public boolean appThemeIsLightTheme(UserSettings s) {return s.theme().equals(UserSettings.LIGHT_THEME);}

    public static boolean inputDoesNotMatchURLRegex(EditText e) {
        return !Patterns.WEB_URL.matcher(e.getText().toString()).matches();
    }

    public static boolean galleryAccessIsNotGranted(Context c) {
        return ContextCompat.checkSelfPermission(c, readExtStoragePerm) != PackageManager.PERMISSION_GRANTED;
    }

    public void selectImage(Activity activity, int requestCode) {
        Intent selectedImg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (selectedImg.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(selectedImg, requestCode);
        }
    }

    public void reqPermOrSelectImg(Activity a, Context c, int storagePermCode, int selectImgCode) {
        if (galleryAccessIsNotGranted(c)) {
            ActivityCompat.requestPermissions(a, new String[] {readExtStoragePerm}, storagePermCode);
        }
        else { selectImage(a, selectImgCode); }
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

    public void attemptEmbedImage(Activity activity, Context context, Uri uri, ImageView image) {
        try {
            InputStream inputStream = app.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            image.setImageBitmap(bitmap); image.setVisibility(VISIBLE);
            CreateNote.selectedImagePath = getPath(uri);
            activity.findViewById(R.id.deleteImage).setVisibility(VISIBLE);
        } catch (Exception e) { Toast.makeText(context, e.getMessage(), LONG).show(); }
    }

    public void buildNoteWithImage(Uri uri, Context context, Activity activity, int requestCode) {
        CreateNote.selectedImagePath = getPath(uri);
        Intent noteWithImage = new Intent(context, CreateNote.class);
        noteWithImage.putExtra("isFromQuickActions", true);
        noteWithImage.putExtra("quickActionType", "image");
        noteWithImage.putExtra("imagePath", CreateNote.selectedImagePath);
        activity.startActivityForResult(noteWithImage, requestCode);
    }

    public void attemptBuildNoteWithImg(@Nullable Intent data, Activity activity, Context context) {
        assert data != null;
        Uri uri = data.getData();

        try { buildNoteWithImage(uri, context, activity, ADD_NOTE); }
        catch (Exception e) { Toast.makeText(context, e.getMessage(), LONG).show(); }
    }

    public void removeImage(Activity a, ImageView image) {
        image.setImageBitmap(null); image.setVisibility(GONE);
        a.findViewById(R.id.deleteImage).setVisibility(GONE); CreateNote.selectedImagePath = "";
    }

    public static AlertDialog showDialog(Context c, View.OnClickListener l, View view,
                                         BottomSheetBehavior<LinearLayout> noteOptions) {
        if (deleteDialog == null) {
            view.findViewById(R.id.confirmDeleteNote).setOnClickListener(l);
            view.findViewById(R.id.cancelDeleteNote).setOnClickListener(l);
            builder = new AlertDialog.Builder(c);
            builder.setView(view);
            deleteDialog = builder.create();
            final Window deleteNoteWindow = deleteDialog.getWindow();

            if (deleteNoteWindow != null) { deleteNoteWindow.setBackgroundDrawable(new ColorDrawable(0)); }
        }

        noteOptions.setState(BottomSheetBehavior.STATE_COLLAPSED);
        deleteDialog.show();
        return deleteDialog;
    }

    public View initURLView(Activity a, Context c) {
        return LayoutInflater.from(c).inflate(R.layout.addurl, a.findViewById(R.id.urlLayout));
    }

    public static AlertDialog showDialog(Activity a, Context c, View.OnClickListener l, View view) {
        if (urlDialog == null) {
            final EditText urlInput = view.findViewById(R.id.inputURL);
            final TextView url = a.findViewById(R.id.webUrl);
            view.findViewById(R.id.confirmAddURL).setOnClickListener(l);
            view.findViewById(R.id.cancelAddURL).setOnClickListener(l);
            builder = new AlertDialog.Builder(c);
            builder.setView(view);
            urlDialog = builder.create();
            final Window addURLWindow = urlDialog.getWindow();

            if (a.getLocalClassName().equals("activities.CreateNote")) {
                if (!url.getText().toString().isEmpty()) { urlInput.setText(url.getText().toString()); }
            }

            if (addURLWindow != null) { addURLWindow.setBackgroundDrawable(new ColorDrawable(0)); }
        }

        urlDialog.show();
        return urlDialog;
    }

    public static void validateURL(Activity a, EditText e) {
        final TextView url = a.findViewById(R.id.webUrl);
        final LinearLayout urlLayout = a.findViewById(R.id.noteURL);
        final Intent noteWithURL = new Intent(a.getApplicationContext(), CreateNote.class);
        e.requestFocus();

        if (e.getText().toString().trim().isEmpty()) {
            Toast.makeText(a,"Empty URL!", Toast.LENGTH_SHORT).show(); return;
        }
        if (inputDoesNotMatchURLRegex(e)) {
            Toast.makeText(a,"Invalid URL!", Toast.LENGTH_SHORT).show(); return;
        }

        urlDialog.dismiss();

        if (a.getLocalClassName().equals("activities.CreateNote")) { // Embed URL from within CreateNote
            url.setText(e.getText().toString()); urlLayout.setVisibility(VISIBLE); return;
        }

        // Otherwise do it from Home via Intent
        noteWithURL.putExtra("isFromQuickActions", true);
        noteWithURL.putExtra("quickActionType", "URL");
        noteWithURL.putExtra("URL", e.getText().toString());
        a.startActivityForResult(noteWithURL, 1);
    }
}
package com.example.ultranote.activities;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.database.Cursor;
import database.NotesDatabase;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.io.InputStream;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ultranote.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import entities.Note;
import settings.UserSettings;

public class CreateNote extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private Note existingNote;
    final Note note = new Note();
    private EditText title, subtitle, content;
    private TextView dateTime, webURL, createNoteText, noteOptionsText, colourPickerText;
    private View colourIndicator, subtitleIndicator, greyNoteBtn;
    private View[] noteColourButtons;
    private ImageView image, backBtn, addURL, addImg, saveBtn, removeTitle, removeSubtitle, removeContent;
    private ImageView[] colours;
    private Drawable[] colourButtonsDBG, colourButtonsLBG;
    private String selectedImagePath;
    private LinearLayout webURLLayout, noteOptionsLayout;
    private CoordinatorLayout createNoteView;
    private AlertDialog addURLDialog, deleteNoteDialog;
    private final SimpleDateFormat singleLineDate = new SimpleDateFormat(
            "EEEE dd MMMM yyyy HH:mm a", Locale.getDefault());

    private UserSettings settings;

    // Request codes
    private static final int STORAGE_PERMISSION = 1;
    private static final int SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnote);
        initNoteOptions();
        initComponents();
        updateView();

        dateTime.setText(singleLineDate.format(new Date()));
        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.saveButton).setOnClickListener(this);
        findViewById(R.id.removeTitle).setOnClickListener(this);
        findViewById(R.id.addURL).setOnClickListener(this);
        findViewById(R.id.deleteURL).setOnClickListener(this);
        findViewById(R.id.addImage).setOnClickListener(this);
        findViewById(R.id.deleteImage).setOnClickListener(this);

        title.addTextChangedListener(this);
        subtitle.addTextChangedListener(this);
        content.addTextChangedListener(this);

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            existingNote = (Note) getIntent().getSerializableExtra("note");
            viewOrUpdateNote();
        }

        if (getIntent().getBooleanExtra("isFromQuickActions", false)) {
            String type = getIntent().getStringExtra("quickActionType");

            if (type != null) {
                switch (type) {
                    case "title": title.setText((getIntent().getStringExtra("quickTitle"))); break;
                    case "image": selectedImagePath = getIntent().getStringExtra("imagePath");
                        image.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                        image.setVisibility(View.VISIBLE);
                        findViewById(R.id.deleteImage).setVisibility(View.VISIBLE); break;
                    case "URL": webURL.setText(getIntent().getStringExtra("URL"));
                        webURLLayout.setVisibility(View.VISIBLE); break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dateTime = findViewById(R.id.textDateTime);
        dateTime.setText(singleLineDate.format(new Date()));
        initNoteOptions();
        updateView();
    }

    private void initComponents() {
        settings = (UserSettings) getApplication();
        createNoteView = findViewById(R.id.createNoteView);
        createNoteText = findViewById(R.id.createNoteText);
        backBtn = findViewById(R.id.backButton);
        addURL = findViewById(R.id.addURL);
        addImg = findViewById(R.id.addImage);
        saveBtn = findViewById(R.id.saveButton);
        title = findViewById(R.id.noteTitleInput);
        subtitle = findViewById(R.id.noteSubtitleInput);
        subtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        content = findViewById(R.id.noteContent);
        dateTime = findViewById(R.id.textDateTime);
        colourIndicator = findViewById(R.id.colourIndicator);
        image = findViewById(R.id.noteImage);
        selectedImagePath = "";
        webURL = findViewById(R.id.webUrl);
        webURLLayout = findViewById(R.id.webUrlLayout);
        removeTitle = findViewById(R.id.removeTitle);
        removeSubtitle = findViewById(R.id.removeSubtitle);
        removeContent = findViewById(R.id.removeContent);
        noteOptionsLayout = findViewById(R.id.noteOptionsLayout);
        noteOptionsText = findViewById(R.id.noteOptionsText);
        colourPickerText = findViewById(R.id.colourPickerText);

        // Note colour buttons and list
        final View greyBtn = findViewById(R.id.viewColour1);
        final View redBtn = findViewById(R.id.viewColour2);
        final View orangeBtn = findViewById(R.id.viewColour3);
        final View lightOrangeBtn = findViewById(R.id.viewColour4);
        final View yellowBtn = findViewById(R.id.viewColour5);
        final View lightGreenBtn = findViewById(R.id.viewColour6);
        final View greenBtn = findViewById(R.id.viewColour7);
        final View lightBlueBtn = findViewById(R.id.viewColour8);
        final View blueBtn = findViewById(R.id.viewColour9);
        final View indigoBtn = findViewById(R.id.viewColour10);
        final View purpleBtn = findViewById(R.id.viewColour11);
        final View violetBtn = findViewById(R.id.viewColour12);
        final View maroonBtn = findViewById(R.id.viewColour13);
        noteColourButtons = new View[] { greyBtn, redBtn, orangeBtn, lightOrangeBtn, yellowBtn, lightGreenBtn,
            greenBtn, lightBlueBtn, blueBtn, indigoBtn, purpleBtn, violetBtn, maroonBtn };

        // Colour buttons dark backgrounds and list:
        final Drawable greyBtnDBG = ContextCompat.getDrawable(this, R.drawable.grey_note_btn);
        final Drawable redBtnDBG = ContextCompat.getDrawable(this, R.drawable.red_note_btn);
        final Drawable orangeBtnDBG = ContextCompat.getDrawable(this, R.drawable.orange_note_btn);
        final Drawable lightOrangeBtnDBG = ContextCompat.getDrawable(this, R.drawable.lightorange_note_btn);
        final Drawable yellowBtnDBG = ContextCompat.getDrawable(this, R.drawable.yellow_note_btn);
        final Drawable lightGreenBtnDBG = ContextCompat.getDrawable(this, R.drawable.lightgreen_note_btn);
        final Drawable greenBtnDBG = ContextCompat.getDrawable(this, R.drawable.green_note_btn);
        final Drawable lightBlueBtnDBG = ContextCompat.getDrawable(this, R.drawable.lightblue_note_btn);
        final Drawable blueBtnDBG = ContextCompat.getDrawable(this, R.drawable.blue_note_btn);
        final Drawable indigoBtnDBG = ContextCompat.getDrawable(this, R.drawable.indigo_note_btn);
        final Drawable purpleBtnDBG = ContextCompat.getDrawable(this, R.drawable.purple_note_btn);
        final Drawable violetBtnDBG = ContextCompat.getDrawable(this, R.drawable.violet_note_btn);
        final Drawable maroonBtnDBG = ContextCompat.getDrawable(this, R.drawable.maroon_note_btn);
        colourButtonsDBG = new Drawable[] { greyBtnDBG, redBtnDBG, orangeBtnDBG, lightOrangeBtnDBG, yellowBtnDBG, lightGreenBtnDBG,
            greenBtnDBG, lightBlueBtnDBG, blueBtnDBG, indigoBtnDBG, purpleBtnDBG, violetBtnDBG, maroonBtnDBG };

        // Colour buttons light backgrounds and list;
        final Drawable greyBtnLBG = ContextCompat.getDrawable(this, R.drawable.grey_note_btn_light);
        final Drawable redBtnLBG = ContextCompat.getDrawable(this, R.drawable.red_note_btn_light);
        final Drawable orangeBtnLBG = ContextCompat.getDrawable(this, R.drawable.orange_note_btn_light);
        final Drawable lightOrangeBtnLBG = ContextCompat.getDrawable(this, R.drawable.lightorange_note_btn_light);
        final Drawable yellowBtnLBG = ContextCompat.getDrawable(this, R.drawable.yellow_note_btn_light);
        final Drawable lightGreenBtnLBG = ContextCompat.getDrawable(this, R.drawable.lightgreen_note_btn_light);
        final Drawable greenBtnLBG = ContextCompat.getDrawable(this, R.drawable.green_note_btn_light);
        final Drawable lightBlueBtnLBG = ContextCompat.getDrawable(this, R.drawable.lightblue_note_btn_light);
        final Drawable blueBtnLBG = ContextCompat.getDrawable(this, R.drawable.blue_note_btn_light);
        final Drawable indigoBtnLBG = ContextCompat.getDrawable(this, R.drawable.indigo_note_btn_light);
        final Drawable purpleBtnLBG = ContextCompat.getDrawable(this, R.drawable.purple_note_btn_light);
        final Drawable violetBtnLBG = ContextCompat.getDrawable(this, R.drawable.violet_note_btn_light);
        final Drawable maroonBtnLBG = ContextCompat.getDrawable(this, R.drawable.maroon_note_btn_light);
        colourButtonsLBG = new Drawable[] { greyBtnLBG, redBtnLBG, orangeBtnLBG, lightOrangeBtnLBG, yellowBtnLBG, lightGreenBtnLBG,
                greenBtnLBG, lightBlueBtnLBG, blueBtnLBG, indigoBtnLBG, purpleBtnLBG, violetBtnLBG, maroonBtnLBG };
    }

    private void updateView() {
        final int darkGrey = ContextCompat.getColor(this, R.color.primaryColour);
        final int lightGrey = ContextCompat.getColor(this, R.color.primaryColourLight);
        final int offWhite = ContextCompat.getColor(this, R.color.offWhite);
        final int black = ContextCompat.getColor(this, R.color.black);
        final int white = ContextCompat.getColor(this, R.color.white);
        final int lightModeAccent = ContextCompat.getColor(this, R.color.noteColour9);
        final int darkModeAccent = ContextCompat.getColor(this, R.color.accent);
        final Drawable saveBtnLightBG = ContextCompat.getDrawable(this, R.drawable.done_button_light);
        final Drawable saveBtnDarkBG = ContextCompat.getDrawable(this, R.drawable.done_button);
        final Drawable lightSubtitleIndicator =
                ContextCompat.getDrawable(this, R.drawable.subtitle_indicator_light);
        final Drawable defaultSubtitleIndicator =
                ContextCompat.getDrawable(this, R.drawable.subtitle_indicator);
        final Drawable noteOptionsLightBG =
                ContextCompat.getDrawable(this, R.drawable.noteoptions_light_background);
        final Drawable noteOptionsDefaultBG =
                ContextCompat.getDrawable(this, R.drawable.noteoptions_background);

        if (settings.getCurrentTheme().equals(UserSettings.LIGHT_THEME)) {
            // Components are set to their specified light mode colours
            createNoteView.setBackgroundColor(white);
            createNoteText.setTextColor(black);
            backBtn.setColorFilter(black);
            addURL.setColorFilter(lightGrey);
            addImg.setColorFilter(lightGrey);
            saveBtn.setBackground(saveBtnLightBG);
            title.setHintTextColor(darkGrey);
            title.setTextColor(darkGrey);
            subtitle.setHintTextColor(darkGrey);
            subtitle.setTextColor(darkGrey);
            subtitleIndicator.setBackground(lightSubtitleIndicator);
            content.setHintTextColor(black);
            content.setTextColor(black);
            dateTime.setTextColor(black);
            webURL.setLinkTextColor(lightModeAccent);
            noteOptionsLayout.setBackground(noteOptionsLightBG);
            noteOptionsText.setTextColor(black);
            colourPickerText.setTextColor(darkGrey);

            for (int i = 0; i < noteColourButtons.length; i++) {
                noteColourButtons[i].setBackground(colourButtonsLBG[i]);
            }

            return;
        }

        // Components are reverted to their default colours
        createNoteView.setBackgroundColor(darkGrey);
        createNoteText.setTextColor(white);
        backBtn.setColorFilter(white);
        addURL.setColorFilter(offWhite);
        addImg.setColorFilter(offWhite);
        saveBtn.setBackground(saveBtnDarkBG);
        title.setHintTextColor(offWhite);
        title.setTextColor(white);
        subtitle.setHintTextColor(offWhite);
        subtitle.setTextColor(white);
        subtitleIndicator.setBackground(defaultSubtitleIndicator);
        content.setHintTextColor(offWhite);
        content.setTextColor(white);
        dateTime.setTextColor(white);
        webURL.setLinkTextColor(darkModeAccent);
        noteOptionsLayout.setBackground(noteOptionsDefaultBG);
        noteOptionsText.setTextColor(white);
        colourPickerText.setTextColor(offWhite);

        for (int i = 0; i < colourButtonsDBG.length; i++) {
            noteColourButtons[i].setBackground(colourButtonsDBG[i]);
        }
    }

    private void saveNote() {
        final LinearLayout noteOptionsLayout = findViewById(R.id.noteOptionsLayout);
        final BottomSheetBehavior<LinearLayout> options = BottomSheetBehavior.from(noteOptionsLayout);

        if (title.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title is empty!", Toast.LENGTH_SHORT).show(); return;
        }
        if (content.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "The note requires content!", Toast.LENGTH_SHORT).show(); return;
        }

        if (webURLLayout.getVisibility() == View.VISIBLE) { note.setWebLink(webURL.getText().toString()); }
        if (existingNote != null) { note.setId(existingNote.getId()); }
        if (options.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            options.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note); return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                Intent homePage = new Intent(CreateNote.this, Home.class);

                setResult(RESULT_OK, intent);
                homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(homePage);
            }
        }

        TextView multiLineDate = findViewById(R.id.textDateTime);
        multiLineDate.setText(new SimpleDateFormat("EEEE dd MMMM yyyy \nHH:mm a",
                Locale.getDefault()).format((new Date())));

        note.setTitle(title.getText().toString());
        note.setSubtitle(subtitle.getText().toString());
        note.setNoteText(content.getText().toString());
        note.setDateTime(multiLineDate.getText().toString());
        note.setImagePath(selectedImagePath);

        new SaveNoteTask().execute();
    }

    private void deleteNote() {
        if (deleteNoteDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNote.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.delete_note_layout,
                    (ViewGroup) findViewById(R.id.deleteNoteLayout)
            );
            builder.setView(view);
            deleteNoteDialog = builder.create();

            if (deleteNoteDialog.getWindow() != null) {
                deleteNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.deleteNote).setOnClickListener(this);
            view.findViewById(R.id.cancelDeleteNote).setOnClickListener(this);
        }

        deleteNoteDialog.show();
    }

    private void initNoteOptions() {
        findViewById(R.id.noteOptionsText).setOnClickListener(this);
        findViewById(R.id.colourIndicator).setOnClickListener(this);

        final ImageView grey = findViewById(R.id.imageColour1);
        final ImageView red = findViewById(R.id.imageColour2);
        final ImageView orange = findViewById(R.id.imageColour3);
        final ImageView lightOrange = findViewById(R.id.imageColour4);
        final ImageView yellow = findViewById(R.id.imageColour5);
        final ImageView lightGreen = findViewById(R.id.imageColour6);
        final ImageView green = findViewById(R.id.imageColour7);
        final ImageView lightBlue = findViewById(R.id.imageColour8);
        final ImageView blue = findViewById(R.id.imageColour9);
        final ImageView indigo = findViewById(R.id.imageColour10);
        final ImageView purple = findViewById(R.id.imageColour11);
        final ImageView violet = findViewById(R.id.imageColour12);
        final ImageView lightMaroon = findViewById(R.id.imageColour13);
        colours = new ImageView[] { grey, red, orange, lightOrange, yellow, lightGreen,
                green, lightBlue, blue, indigo, purple, violet, lightMaroon };

        // Loop sets all colour buttons' image resources (ticks) to 0 upon resuming the activity, prior to the
        // tick being correctly assigned later in this method. This fixes the onResume() duplicate tick bug
        // Index starts at 1 so that the default button's tick is not removed when creating a new note.
        for (int i = 1; i < colours.length; i++) {
            colours[i].setImageResource(0);
        }

        if (existingNote != null) {
            findViewById(R.id.deleteBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.deleteBtn).setOnClickListener(this);
        }

        for (int i = 0; i < colours.length; i++) {
            final int j = i;

            colours[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectColour(colours, j + 1);
                    switch (j) {
                        case 0: setNoteColour("#333333"); break;
                        case 1: setNoteColour("#FF2929"); break;
                        case 2: setNoteColour("#FF5722"); break;
                        case 3: setNoteColour("#FF9800"); break;
                        case 4: setNoteColour("#FFE719"); break;
                        case 5: setNoteColour("#8BC34A"); break;
                        case 6: setNoteColour("#4CAF50"); break;
                        case 7: setNoteColour("#00BCD4"); break;
                        case 8: setNoteColour("#2196F3"); break;
                        case 9: setNoteColour("#3F51B5"); break;
                        case 10: setNoteColour("#673AB7"); break;
                        case 11: setNoteColour("#9C27B0"); break;
                        case 12: setNoteColour("#E91E63"); break;
                    }
                }
            });
        }

        if (existingNote != null && existingNote.getColour() != null
                && !existingNote.getColour().trim().isEmpty()) {
            switch (existingNote.getColour()) {
                case "#FF2929": setNoteColour("#FF2929"); switchTickToButton(1); break;
                case "#FF5722": setNoteColour("#FF5722"); switchTickToButton(2); break;
                case "#FF9800": setNoteColour("#FF9800"); switchTickToButton(3); break;
                case "#FFE719": setNoteColour("#FFE719"); switchTickToButton(4); break;
                case "#8BC34A": setNoteColour("#8BC34A"); switchTickToButton(5); break;
                case "#4CAF50": setNoteColour("#4CAF50"); switchTickToButton(6); break;
                case "#00BCD4": setNoteColour("#00BCD4"); switchTickToButton(7); break;
                case "#2196F3": setNoteColour("#2196F3"); switchTickToButton(8); break;
                case "#3F51B5": setNoteColour("#3F51B5"); switchTickToButton(9); break;
                case "#673AB7": setNoteColour("#673AB7"); switchTickToButton(10); break;
                case "#9C27B0": setNoteColour("#9C27B0"); switchTickToButton(11); break;
                case "#E91E63": setNoteColour("#E91E63"); switchTickToButton(12); break;
            }
        }
    }

    private void toggleNoteOptions(BottomSheetBehavior<LinearLayout> bsb) {
        if (bsb.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void selectColour(ImageView[] colours, int colourNumber) {
        for (int i = 0; i < colours.length; i++) {
            if (i == colourNumber - 1) {
                colours[i].setImageResource(R.drawable.ic_done);
                continue;
            }

            colours[i].setImageResource(0);
        }
    }

    private void setNoteColour(String colour) {
        note.setColour(colour);
        colourIndicator.setBackgroundColor(Color.parseColor(colour));
    }

    private void switchTickToButton(int btnNumber) {
        colours[0].setImageResource(0);
        colours[btnNumber].setImageResource(R.drawable.ic_done);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, perms, results);

        if (requestCode == STORAGE_PERMISSION && results.length > 0) {
            if (results[0] == PackageManager.PERMISSION_GRANTED) { selectImage(); }
            else { Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show(); }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        image.setImageBitmap(bitmap);
                        image.setVisibility(View.VISIBLE);
                        selectedImagePath = getPath(selectedImageUri);

                        findViewById(R.id.deleteImage).setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPath(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
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

    private void addURL() {
        if (addURLDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNote.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.add_url_layout,
                    findViewById(R.id.addURLLayout)
            );
            builder.setView(view);
            addURLDialog = builder.create();

            if (addURLDialog.getWindow() != null) {
                addURLDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.confirmAddURL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateNote.this, "Empty URL!", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateNote.this, "Invalid URL!", Toast.LENGTH_SHORT).show();
                    } else {
                        webURL.setText(inputURL.getText().toString());
                        webURLLayout.setVisibility(View.VISIBLE);
                        addURLDialog.dismiss();
                    }
                }
            });

            view.findViewById(R.id.cancelAddURL).setOnClickListener(this);
        }

        addURLDialog.show();
    }

    /**
     * TextWatcher implementations. In this class I only need 'onTextChanged()' for checking if any
     * of the text fields (title, subtitle & content) are empty. This is so that the button for
     * removing text is only visible if there is text in the field.
     */
    @Override
    public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
        if (!title.getText().toString().trim().isEmpty()) { removeTitle.setVisibility(View.VISIBLE); }
        else { removeTitle.setVisibility(View.GONE); }

        if (!subtitle.getText().toString().trim().isEmpty()) { removeSubtitle.setVisibility(View.VISIBLE); }
        else { removeSubtitle.setVisibility(View.GONE); }

        if (!content.getText().toString().trim().isEmpty()) { removeContent.setVisibility(View.VISIBLE); }
        else { removeContent.setVisibility(View.GONE); }
    }
    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {} // Method not required
    public void afterTextChanged(Editable editable) {}                       // Method not required

    private void viewOrUpdateNote() {
        title.setText(existingNote.getTitle());
        subtitle.setText(existingNote.getSubtitle());
        content.setText(existingNote.getNoteText());
        dateTime.setText(existingNote.getDateTime());

        if (existingNote.getImagePath() != null && !existingNote.getImagePath().trim().isEmpty()) {
            image.setImageBitmap(BitmapFactory.decodeFile(existingNote.getImagePath()));
            image.setVisibility(View.VISIBLE);
            selectedImagePath = existingNote.getImagePath();

            findViewById(R.id.deleteImage).setVisibility(View.VISIBLE);
        }

        if (existingNote.getWebLink() != null && !existingNote.getWebLink().trim().isEmpty()) {
            webURL.setText(existingNote.getWebLink());
            webURLLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        final LinearLayout noteOptionsLayout = findViewById(R.id.noteOptionsLayout);
        final BottomSheetBehavior<LinearLayout> options = BottomSheetBehavior.from(noteOptionsLayout);

        switch (view.getId()) {
            // Simple 1-line implementations
            case R.id.backButton: onBackPressed(); break;
            case R.id.saveButton: saveNote(); break;
            case R.id.removeTitle: title.getText().clear(); break;
            case R.id.removeSubtitle: subtitle.getText().clear(); break;
            case R.id.removeContent: content.getText().clear(); break;
            case R.id.addURL: addURL(); break;
            case R.id.cancelAddURL: addURLDialog.dismiss(); break;
            case R.id.deleteURL: webURL.setText(null); webURLLayout.setVisibility(View.GONE); break;
            case R.id.colourIndicator: case R.id.noteOptionsText: toggleNoteOptions(options); break;
            case R.id.deleteBtn: options.setState(BottomSheetBehavior.STATE_COLLAPSED); deleteNote(); break;
            case R.id.cancelDeleteNote: deleteNoteDialog.dismiss(); break;

            case R.id.addImage:   // Ask the user for permission to access the gallery
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            CreateNote.this,
                            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                            STORAGE_PERMISSION
                    );
                } else { selectImage(); } break;

            case R.id.deleteImage:   // Delete the image from the note
                image.setImageBitmap(null); image.setVisibility(View.GONE);
                findViewById(R.id.deleteImage).setVisibility(View.GONE); selectedImagePath = ""; break;

            case R.id.deleteNote:   // Delete the note, then throw a toast message to confirm this
                class DeleteNoteTask extends AsyncTask<Void, Void, Void>
                {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        NotesDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(existingNote);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Intent intent = new Intent();
                        intent.putExtra("isNoteDeleted", true);
                        setResult(RESULT_OK, intent);
                        Toast.makeText(CreateNote.this, "'" + existingNote.getTitle() + "'"
                                + " deleted", Toast.LENGTH_LONG).show(); finish();
                    }
                }

                new DeleteNoteTask().execute(); break;
        }
    }
}
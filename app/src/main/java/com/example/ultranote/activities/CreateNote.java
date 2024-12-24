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

    private boolean galleryAccessIsNotGranted;
    private int black, white, offWhite, darkGrey, lightGrey, lightModeAccent, darkModeAccent;
    private Note existingNote;
    final Note note = new Note();
    private UserSettings settings;
    private EditText title, subtitle, content, inputURL;
    private TextView dateTime, url, createNoteText, noteOptionsText, colourPickerText;
    private View colourIndicator, subtitleIndicator, addURLView, deleteNoteView;
    private ImageView image, backBtn, addURL, addImg, saveBtn, removeTitle, removeSubtitle, removeContent,
    // Colours
    grey, red, orange, lightOrange, yellow, lightGreen, green, lightBlue, blue, indigo, purple,
            violet, lightMaroon;

    private ImageView[] colours;
    private Drawable light, dark, saveBtnLight, saveBtnDark, blueIndicator, lightBlueIndicator;
    private Drawable[] darkBGColours, lightBGColours;
    private Uri selectedImageUri;
    private InputStream inputStream;
    private Bitmap bitmap;
    private BottomSheetBehavior<LinearLayout> options;
    private LinearLayout urlLayout, noteOptions;
    private CoordinatorLayout createNoteView;
    private AlertDialog addURLDialog, deleteNoteDialog;
    private AlertDialog.Builder builder;
    private final SimpleDateFormat singleLineDate = new SimpleDateFormat(
            "EEEE dd MMMM yyyy HH:mm a", Locale.getDefault());
    private String selectedImagePath, type;

    // Request codes
    private static final int STORAGE_PERMISSION = 1;
    private static final int SELECT_IMAGE = 2;

    // States
    private static final int EXPANDED = BottomSheetBehavior.STATE_EXPANDED;
    private static final int COLLAPSED = BottomSheetBehavior.STATE_COLLAPSED;
    private static final int GONE = View.GONE;
    private static final int VISIBLE = View.VISIBLE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnote);
        initComponents();
        initNoteOptions();
        loadPotentialNoteData();
        updateView();
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
        url = findViewById(R.id.webUrl);
        urlLayout = findViewById(R.id.webUrlLayout);
        removeTitle = findViewById(R.id.removeTitle);
        removeSubtitle = findViewById(R.id.removeSubtitle);
        removeContent = findViewById(R.id.removeContent);
        noteOptions = findViewById(R.id.noteOptionsLayout);
        noteOptionsText = findViewById(R.id.noteOptionsText);
        colourPickerText = findViewById(R.id.colourPickerText);
        options = BottomSheetBehavior.from(noteOptions);
        builder = new AlertDialog.Builder(CreateNote.this);
        galleryAccessIsNotGranted = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        addURLView = LayoutInflater.from(this).inflate(
                R.layout.add_url_layout,
                findViewById(R.id.addURLLayout)
        );
        deleteNoteView = LayoutInflater.from(this).inflate(
                R.layout.delete_note_layout,
                (ViewGroup) findViewById(R.id.deleteNoteLayout)
        );
        inputURL = addURLView.findViewById(R.id.inputURL);
        bitmap = BitmapFactory.decodeStream(inputStream);
        type = getIntent().getStringExtra("quickActionType");

        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.saveButton).setOnClickListener(this);
        findViewById(R.id.removeTitle).setOnClickListener(this);
        findViewById(R.id.addURL).setOnClickListener(this);
        findViewById(R.id.deleteURL).setOnClickListener(this);
        findViewById(R.id.addImage).setOnClickListener(this);
        findViewById(R.id.deleteImage).setOnClickListener(this);
        dateTime.setText(singleLineDate.format(new Date()));
        title.addTextChangedListener(this);
        subtitle.addTextChangedListener(this);
        content.addTextChangedListener(this);

        // Note colour buttons and list
        grey = findViewById(R.id.imageColour1);    red = findViewById(R.id.imageColour2);
        orange = findViewById(R.id.imageColour3);  lightOrange = findViewById(R.id.imageColour4);
        yellow = findViewById(R.id.imageColour5);  lightGreen = findViewById(R.id.imageColour6);
        green = findViewById(R.id.imageColour7);   lightBlue = findViewById(R.id.imageColour8);
        blue = findViewById(R.id.imageColour9);    indigo = findViewById(R.id.imageColour10);
        purple = findViewById(R.id.imageColour11); violet = findViewById(R.id.imageColour12);
        lightMaroon = findViewById(R.id.imageColour13);
        colours = new ImageView[] { grey, red, orange, lightOrange, yellow, lightGreen,
                green, lightBlue, blue, indigo, purple, violet, lightMaroon };

        // Colour buttons (dark backgrounds) and list:
        final Drawable grey = ContextCompat.getDrawable(this, R.drawable.grey_btn);
        final Drawable red = ContextCompat.getDrawable(this, R.drawable.red_btn);
        final Drawable orange = ContextCompat.getDrawable(this, R.drawable.orange_btn);
        final Drawable lightOrange = ContextCompat.getDrawable(this, R.drawable.lightorange_btn);
        final Drawable yellow = ContextCompat.getDrawable(this, R.drawable.yellow_btn);
        final Drawable lightGreen = ContextCompat.getDrawable(this, R.drawable.lightgreen_btn);
        final Drawable green = ContextCompat.getDrawable(this, R.drawable.green_btn);
        final Drawable lightBlue = ContextCompat.getDrawable(this, R.drawable.lightblue_btn);
        final Drawable blue = ContextCompat.getDrawable(this, R.drawable.blue_btn);
        final Drawable indigo = ContextCompat.getDrawable(this, R.drawable.indigo_btn);
        final Drawable purple = ContextCompat.getDrawable(this, R.drawable.purple_btn);
        final Drawable violet = ContextCompat.getDrawable(this, R.drawable.violet_btn);
        final Drawable maroon = ContextCompat.getDrawable(this, R.drawable.maroon_btn);
        darkBGColours = new Drawable[] { grey, red, orange, lightOrange, yellow, lightGreen,
            green, lightBlue, blue, indigo, purple, violet, maroon };

        // Colour buttons (light backgrounds) and list;
        final Drawable greyL = ContextCompat.getDrawable(this, R.drawable.grey_btn_light);
        final Drawable redL = ContextCompat.getDrawable(this, R.drawable.red_btn_light);
        final Drawable orangeL = ContextCompat.getDrawable(this, R.drawable.orange_btn_light);
        final Drawable lightOrangeL = ContextCompat.getDrawable(this, R.drawable.lightorange_btn_light);
        final Drawable yellowL = ContextCompat.getDrawable(this, R.drawable.yellow_btn_light);
        final Drawable lightGreenL = ContextCompat.getDrawable(this, R.drawable.lightgreen_btn_light);
        final Drawable greenL = ContextCompat.getDrawable(this, R.drawable.green_btn_light);
        final Drawable lightBlueL = ContextCompat.getDrawable(this, R.drawable.lightblue_btn_light);
        final Drawable blueL = ContextCompat.getDrawable(this, R.drawable.blue_btn_light);
        final Drawable indigoL = ContextCompat.getDrawable(this, R.drawable.indigo_btn_light);
        final Drawable purpleL = ContextCompat.getDrawable(this, R.drawable.purple_btn_light);
        final Drawable violetL = ContextCompat.getDrawable(this, R.drawable.violet_btn_light);
        final Drawable maroonL = ContextCompat.getDrawable(this, R.drawable.maroon_btn_light);
        lightBGColours = new Drawable[] { greyL, redL, orangeL, lightOrangeL, yellowL, lightGreenL,
                greenL, lightBlueL, blueL, indigoL, purpleL, violetL, maroonL };

        // Colours and Drawables for updating the view
        darkGrey = ContextCompat.getColor(this, R.color.primaryColour);
        lightGrey = ContextCompat.getColor(this, R.color.primaryColourLight);
        offWhite = ContextCompat.getColor(this, R.color.offWhite);
        black = ContextCompat.getColor(this, R.color.black);
        white = ContextCompat.getColor(this, R.color.white);
        lightModeAccent = ContextCompat.getColor(this, R.color.noteColour9);
        darkModeAccent = ContextCompat.getColor(this, R.color.accent);
        saveBtnLight = ContextCompat.getDrawable(this, R.drawable.done_button_light);
        saveBtnDark = ContextCompat.getDrawable(this, R.drawable.done_button);
        blueIndicator = ContextCompat.getDrawable(this, R.drawable.subtitle_indicator_light);
        lightBlueIndicator = ContextCompat.getDrawable(this, R.drawable.subtitle_indicator);
        light = ContextCompat.getDrawable(this, R.drawable.noteoptions_light_bg);
        dark = ContextCompat.getDrawable(this, R.drawable.noteoptions_bg);
    }

    private void updateView() {
        if (settings.theme().equals(UserSettings.LIGHT_THEME)) {// Set components to light mode colours
            createNoteView.setBackgroundColor(white);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            title.setTextColor(darkGrey);    createNoteText.setTextColor(black);
            subtitle.setTextColor(darkGrey); noteOptionsText.setTextColor(black);
            content.setTextColor(black);     colourPickerText.setTextColor(darkGrey);
            dateTime.setTextColor(black);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            backBtn.setColorFilter(black);    addURL.setColorFilter(lightGrey);
            addImg.setColorFilter(lightGrey);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            saveBtn.setBackground(saveBtnLight); subtitleIndicator.setBackground(blueIndicator);
            noteOptions.setBackground(light);

            for (int i = 0; i < colours.length; i++) { colours[i].setBackground(lightBGColours[i]); }
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            title.setHintTextColor(darkGrey); subtitle.setHintTextColor(darkGrey);
            content.setHintTextColor(black);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            url.setLinkTextColor(lightModeAccent);

            return;
        }
        // Revert components to their default colours
        createNoteView.setBackgroundColor(darkGrey);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        title.setTextColor(white);    createNoteText.setTextColor(white);
        subtitle.setTextColor(white); noteOptionsText.setTextColor(white);
        content.setTextColor(white);  colourPickerText.setTextColor(offWhite);
        dateTime.setTextColor(white);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        backBtn.setColorFilter(white);   addURL.setColorFilter(offWhite);
        addImg.setColorFilter(offWhite);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        saveBtn.setBackground(saveBtnDark); subtitleIndicator.setBackground(lightBlueIndicator);
        noteOptions.setBackground(dark);

        for (int i = 0; i < colours.length; i++) { colours[i].setBackground(darkBGColours[i]); }
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        title.setHintTextColor(offWhite);
        subtitle.setHintTextColor(offWhite);
        content.setHintTextColor(offWhite);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        url.setLinkTextColor(darkModeAccent);
    }

    private void loadPotentialNoteData() {
        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            existingNote = (Note) getIntent().getSerializableExtra("note");
            viewOrUpdateNote();
        }

        if (getIntent().getBooleanExtra("isFromQuickActions", false)) {
            if (type != null) {
                switch (type) {
                    case "title": title.setText((getIntent().getStringExtra("quickTitle"))); break;
                    case "image": selectedImagePath = getIntent().getStringExtra("imagePath");
                        image.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                        image.setVisibility(VISIBLE);
                        findViewById(R.id.deleteImage).setVisibility(VISIBLE); break;
                    case "URL": url.setText(getIntent().getStringExtra("URL"));
                        urlLayout.setVisibility(VISIBLE); break;
                }
            }
        }
    }

    private void saveNote() {
        if (title.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title is empty!", Toast.LENGTH_SHORT).show(); return;
        }
        if (content.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "The note requires content!", Toast.LENGTH_SHORT).show(); return;
        }
        if (urlLayout.getVisibility() == View.VISIBLE) { note.setWebLink(url.getText().toString()); }
        if (existingNote != null) { note.setId(existingNote.getId()); }
        if (options.getState() == EXPANDED) {
            options.setState(COLLAPSED);
        }

        dateTime.setText(new SimpleDateFormat("EEEE dd MMMM yyyy \nHH:mm a",
                Locale.getDefault()).format((new Date())));
        note.setTitle(title.getText().toString());
        note.setSubtitle(subtitle.getText().toString());
        note.setNoteText(content.getText().toString());
        note.setDateTime(dateTime.getText().toString());
        note.setImagePath(selectedImagePath);

        new SaveNoteTask().execute();
    }

    private void showDeleteNoteDialog() {
        if (deleteNoteDialog == null) {
            builder.setView(deleteNoteView);
            deleteNoteDialog = builder.create();

            if (deleteNoteDialog.getWindow() != null) {
                deleteNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            deleteNoteView.findViewById(R.id.deleteNote).setOnClickListener(this);
            deleteNoteView.findViewById(R.id.cancelDeleteNote).setOnClickListener(this);
        }

        deleteNoteDialog.show();
    }

    private void initNoteOptions() {
        findViewById(R.id.noteOptionsText).setOnClickListener(this);
        findViewById(R.id.colourIndicator).setOnClickListener(this);
        colours = new ImageView[] { grey, red, orange, lightOrange, yellow, lightGreen,
                green, lightBlue, blue, indigo, purple, violet, lightMaroon };

        // Loop sets all colour buttons' image resources (ticks) to 0 upon resuming the activity, prior to the
        // tick being correctly assigned later in this method. This fixes the onResume() duplicate tick bug
        // Index starts at 1 so that the default button's tick is not removed when creating a new note.
        for (int i = 1; i < colours.length; i++) {
            colours[i].setImageResource(0);
        }

        if (existingNote != null) {
            findViewById(R.id.deleteBtn).setVisibility(VISIBLE);
            findViewById(R.id.deleteBtn).setOnClickListener(this);
        }

        for (int i = 0; i < colours.length; i++) {
            final int j = i;

            colours[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectColour(colours, j + 1);
                    switch (j) {
                        case 0: setNoteColour("#333333"); break;  case 1: setNoteColour("#FF2929"); break;
                        case 2: setNoteColour("#FF5722"); break;  case 3: setNoteColour("#FF9800"); break;
                        case 4: setNoteColour("#FFE719"); break;  case 5: setNoteColour("#8BC34A"); break;
                        case 6: setNoteColour("#4CAF50"); break;  case 7: setNoteColour("#00BCD4"); break;
                        case 8: setNoteColour("#2196F3"); break;  case 9: setNoteColour("#3F51B5"); break;
                        case 10: setNoteColour("#673AB7"); break; case 11: setNoteColour("#9C27B0"); break;
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
        if (bsb.getState() != EXPANDED) { bsb.setState(EXPANDED); }
        else { bsb.setState(COLLAPSED); }
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
        note.setColour(colour); colourIndicator.setBackgroundColor(Color.parseColor(colour));
    }

    private void switchTickToButton(int btnNumber) {
        colours[0].setImageResource(0); colours[btnNumber].setImageResource(R.drawable.ic_done);
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
        assert data != null;
        selectedImageUri = data.getData();

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            if (selectedImageUri != null) {
                try {
                    inputStream = getContentResolver().openInputStream(selectedImageUri);
                    image.setImageBitmap(bitmap); image.setVisibility(VISIBLE);
                    selectedImagePath = getPath(selectedImageUri);
                    findViewById(R.id.deleteImage).setVisibility(VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            builder.setView(addURLView);
            addURLDialog = builder.create();

            if (addURLDialog.getWindow() != null) {
                addURLDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            addURLView.findViewById(R.id.confirmAddURL).setOnClickListener(this);
            addURLView.findViewById(R.id.cancelAddURL).setOnClickListener(this);
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
        if (!title.getText().toString().trim().isEmpty()) { removeTitle.setVisibility(VISIBLE); }
        else { removeTitle.setVisibility(GONE); }

        if (!subtitle.getText().toString().trim().isEmpty()) { removeSubtitle.setVisibility(VISIBLE); }
        else { removeSubtitle.setVisibility(GONE); }

        if (!content.getText().toString().trim().isEmpty()) { removeContent.setVisibility(VISIBLE); }
        else { removeContent.setVisibility(GONE); }
    }
    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {} // Method not required
    public void afterTextChanged(Editable editable) {}                       // Method not required

    private void viewOrUpdateNote() {
        title.setText(existingNote.getTitle());      subtitle.setText(existingNote.getSubtitle());
        content.setText(existingNote.getNoteText()); dateTime.setText(existingNote.getDateTime());

        if (existingNote.getImagePath() != null && !existingNote.getImagePath().trim().isEmpty()) {
            image.setImageBitmap(BitmapFactory.decodeFile(existingNote.getImagePath()));
            image.setVisibility(VISIBLE);
            selectedImagePath = existingNote.getImagePath();

            findViewById(R.id.deleteImage).setVisibility(VISIBLE);
        }

        if (existingNote.getWebLink() != null && !existingNote.getWebLink().trim().isEmpty()) {
            url.setText(existingNote.getWebLink());
            urlLayout.setVisibility(VISIBLE);
        }
    }

    @Override @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            // Simple 1-line implementations
            case R.id.backButton: onBackPressed(); break;
            case R.id.saveButton: saveNote(); break;
            case R.id.removeTitle: title.getText().clear(); break;
            case R.id.removeSubtitle: subtitle.getText().clear(); break;
            case R.id.removeContent: content.getText().clear(); break;
            case R.id.addURL: addURL(); break;
            case R.id.cancelAddURL: addURLDialog.dismiss(); break;
            case R.id.deleteURL: url.setText(null); inputURL.setText(null); urlLayout.setVisibility(GONE); break;
            case R.id.colourIndicator: case R.id.noteOptionsText: toggleNoteOptions(options); break;
            case R.id.deleteBtn: options.setState(COLLAPSED); showDeleteNoteDialog(); break;
            case R.id.cancelDeleteNote: deleteNoteDialog.dismiss(); break;
            case R.id.deleteNote: new DeleteNoteTask().execute(); break;
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            case R.id.deleteImage: image.setImageBitmap(null); image.setVisibility(GONE);
                findViewById(R.id.deleteImage).setVisibility(GONE); selectedImagePath = ""; break;
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            case R.id.confirmAddURL: inputURL.requestFocus();
                if (inputURL.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CreateNote.this,"Empty URL!", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                    Toast.makeText(CreateNote.this,"Invalid URL!", Toast.LENGTH_SHORT).show();
                } else { url.setText(inputURL.getText().toString());
                         urlLayout.setVisibility(VISIBLE);
                         addURLDialog.dismiss();
                       } break;
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            case R.id.addImage: if (galleryAccessIsNotGranted) {
                                    ActivityCompat.requestPermissions(
                                        CreateNote.this,
                                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                        STORAGE_PERMISSION
                                    );
                                } else { selectImage(); } break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SaveNoteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NotesDatabase.getDatabase(getApplicationContext()).noteDao().insert(note); return null;
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
    class DeleteNoteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NotesDatabase.getDatabase(getApplicationContext()).noteDao().delete(existingNote); return null;
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
}
package com.example.ultranote.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import database.NotesDatabase;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.graphics.drawable.Drawable;
import java.io.InputStream;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ultranote.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import entities.Note;
import settings.UserSettings;
import utilities.Utilities;

public class CreateNote extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    final Note note = new Note();
    private Note existingNote;
    private UserSettings settings;
    private Utilities u;
    private EditText title, subtitle, content, input;
    private TextView dateTime, url, createNoteText, noteOptionsText, colourPickerText;
    private View colourIndicator, urlV, sIndicator, deleteV;
    private ImageView image, backBtn, addURL, addImg, saveBtn, clearTBtn, clearSBtn, clearCBtn;
    private ImageView[] colours;
    private Context appContext;
    private Drawable[] darkBGColours, lightBGColours;
    private InputStream inputStream;
    private Bitmap bitmap;
    private BottomSheetBehavior<LinearLayout> o;
    private LinearLayout noteURL, noteOptions, deleteBtn;
    private CoordinatorLayout createNoteBG;
    private String selectedImagePath, type;

    // Request codes
    private static final int STORAGE_PERM = 1;
    private static final int SELECT_IMAGE = 2;

    // States
    private static final int EXPANDED = BottomSheetBehavior.STATE_EXPANDED;
    private static final int COLLAPSED = BottomSheetBehavior.STATE_COLLAPSED;
    private static final int GONE = View.GONE;
    private static final int VISIBLE = View.VISIBLE;
    private static final int GRANTED = PackageManager.PERMISSION_GRANTED;


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
        initNoteOptions();
        updateView();
    }

    private void initComponents() {
        Utilities.initCreateNoteListeners(this, this, this);
        Utilities.initCreateNoteDrawables(this);
        Utilities.initGlobalColours(this);
        Utilities.urlDialog = null; Utilities.deleteDialog = null;

        settings = (UserSettings) getApplication();
        u = new Utilities(getApplication());
        appContext = getApplicationContext();
        createNoteBG = findViewById(R.id.createNoteView);
        createNoteText = findViewById(R.id.createNoteText);
        backBtn = findViewById(R.id.backButton);
        addURL = findViewById(R.id.addURL);
        addImg = findViewById(R.id.addImage);
        saveBtn = findViewById(R.id.saveButton);
        deleteBtn = findViewById(R.id.delete);
        title = findViewById(R.id.noteTitleInput);
        subtitle = findViewById(R.id.noteSubtitleInput);
        sIndicator = findViewById(R.id.subtitleIndicator);
        content = findViewById(R.id.noteContent);
        dateTime = findViewById(R.id.textDateTime);
        colourIndicator = findViewById(R.id.colourIndicator);
        image = findViewById(R.id.noteImage);
        url = findViewById(R.id.webUrl);
        noteURL = findViewById(R.id.noteURL);
        clearTBtn = findViewById(R.id.removeTitle);
        clearSBtn = findViewById(R.id.removeSubtitle);
        clearCBtn = findViewById(R.id.removeContent);
        noteOptions = findViewById(R.id.noteOptionsLayout);
        noteOptionsText = findViewById(R.id.noteOptionsText);
        colourPickerText = findViewById(R.id.colourPickerText);
        o = BottomSheetBehavior.from(noteOptions);
        urlV = LayoutInflater.from(this).inflate(R.layout.addurl, findViewById(R.id.urlLayout));
        deleteV = LayoutInflater.from(this).inflate(R.layout.delete, findViewById(R.id.deleteLayout));
        input = urlV.findViewById(R.id.inputURL);
        type = getIntent().getStringExtra("quickActionType");

        // List of note colour buttons + accompanying lists of light & dark theme Drawables
        colours = Utilities.initColourButtons(this);
        darkBGColours = Utilities.initDarkBGColourButtons(this);
        lightBGColours = Utilities.initLightBGColourButtons(this);
    }

    private void updateView() {
        dateTime.setText(Utilities.singleLine);

        if (settings.theme().equals(UserSettings.LIGHT_THEME)) {// Set components to light mode colours
        //-|Component Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            createNoteBG.setBackgroundColor(Utilities.white);
        //-|Text Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -//
            title.setTextColor(Utilities.darkGrey);    createNoteText.setTextColor(Utilities.black);
            subtitle.setTextColor(Utilities.darkGrey); noteOptionsText.setTextColor(Utilities.black);
            content.setTextColor(Utilities.black);     colourPickerText.setTextColor(Utilities.darkGrey);
            dateTime.setTextColor(Utilities.black);    url.setLinkTextColor(Utilities.lightModeAccent);
        //-|Icon Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -//
            backBtn.setColorFilter(Utilities.black);    addURL.setColorFilter(Utilities.lightGrey);
            addImg.setColorFilter(Utilities.lightGrey);
        //-|Component Backgrounds|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            saveBtn.setBackground(Utilities.saveBtnLight); sIndicator.setBackground(Utilities.blue);
            noteOptions.setBackground(Utilities.light);

            for (int i = 0; i < colours.length; i++) { colours[i].setBackground(lightBGColours[i]); }
        //-|EditText Placeholder Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - -//
            title.setHintTextColor(Utilities.darkGrey); subtitle.setHintTextColor(Utilities.darkGrey);
            content.setHintTextColor(Utilities.black);

            return;
    }                                               // Revert components to their default colours \\
    //-|Component Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ||
        createNoteBG.setBackgroundColor(Utilities.darkGrey);
    //-|Text Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -||
        title.setTextColor(Utilities.white);    createNoteText.setTextColor(Utilities.white);
        subtitle.setTextColor(Utilities.white); noteOptionsText.setTextColor(Utilities.white);
        content.setTextColor(Utilities.white);  colourPickerText.setTextColor(Utilities.offWhite);
        dateTime.setTextColor(Utilities.white); url.setLinkTextColor(Utilities.darkModeAccent);
    //-|Icon Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -||
        backBtn.setColorFilter(Utilities.white);   addURL.setColorFilter(Utilities.offWhite);
        addImg.setColorFilter(Utilities.offWhite);
    //-|Component Backgrounds|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ||
        saveBtn.setBackground(Utilities.saveBtnDark); sIndicator.setBackground(Utilities.lightBlue);
        noteOptions.setBackground(Utilities.dark);

        for (int i = 0; i < colours.length; i++) { colours[i].setBackground(darkBGColours[i]); }
    //-|EditText Placeholder Colours|- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -//
        title.setHintTextColor(Utilities.offWhite);   subtitle.setHintTextColor(Utilities.offWhite);
        content.setHintTextColor(Utilities.offWhite);
    }

    private void loadPotentialNoteData() {
        if (getIntent().getBooleanExtra("viewExisting", false)) {
            existingNote = (Note) getIntent().getSerializableExtra("note");
            loadAllNoteData();
        }

        if (getIntent().getBooleanExtra("isFromQuickActions", false)) {
            if (type != null) {
                switch (type) {
                    case "title": title.setText((getIntent().getStringExtra("quickTitle"))); break;
                //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
                    case "image": selectedImagePath = getIntent().getStringExtra("imagePath");
                                  image.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                                  image.setVisibility(VISIBLE);
                                  findViewById(R.id.deleteImage).setVisibility(VISIBLE); break;
                //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
                    case "URL": url.setText(getIntent().getStringExtra("URL"));
                                noteURL.setVisibility(VISIBLE); break;
                }
            }
        }
    }

    private void loadAllNoteData() {
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
            noteURL.setVisibility(VISIBLE);
        }
    }

    private void saveNote() {
        if (title.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title is empty!", Toast.LENGTH_SHORT).show(); return;
        }
        if (content.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "The note has no content!", Toast.LENGTH_SHORT).show(); return;
        }
        if (noteURL.getVisibility() == VISIBLE) { note.setWebLink(url.getText().toString()); }
        if (existingNote != null) { note.setId(existingNote.getId()); }
        if (o.getState() == EXPANDED) { o.setState(COLLAPSED); }

        note.setTitle(title.getText().toString());      note.setSubtitle(subtitle.getText().toString());
        note.setNoteText(content.getText().toString()); note.setDateTime(dateTime.getText().toString());
        note.setImagePath(selectedImagePath);           dateTime.setText(Utilities.multiLineDate);

        new SaveNoteTask().execute();
    }

    private void initNoteOptions() {
        if (existingNote == null) { setColourTo("#333333"); switchTickToBtn(0); }
        else { deleteBtn.setVisibility(VISIBLE); deleteBtn.setOnClickListener(this); }

        // Loop solves duplicate tick bug by removing all coloured buttons' ticks when the activity resumes.
        // Index starts at 1 so that the default button's tick is not removed when creating a new note.
        for (int i = 1; i < colours.length; i++) { colours[i].setImageResource(0); }

        u.initColourPickerFor(existingNote, this);
        u.setNoteColourToSelected(this);
        findViewById(R.id.noteOptionsText).setOnClickListener(this);
        findViewById(R.id.colourIndicator).setOnClickListener(this);
    }

    private void toggleNoteOptions(BottomSheetBehavior<LinearLayout> bsb) {
        if (bsb.getState() != EXPANDED) { bsb.setState(EXPANDED); }
        else { bsb.setState(COLLAPSED); }
    }

    public void selectColour(ImageView[] colours, int colourNumber) {
        for (int i = 0; i < colours.length; i++) {
            if (i == colourNumber - 1) {
                colours[i].setImageResource(R.drawable.ic_done);
                continue;
            }

            colours[i].setImageResource(0);
        }
    }

    public void setColourTo(String colour) {
        note.setColour(colour); colourIndicator.setBackgroundColor(Color.parseColor(colour));
    }

    public void switchTickToBtn(int btn) {
        colours[0].setImageResource(0); colours[btn].setImageResource(R.drawable.ic_done);
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(reqCode, perms, results);

        if (reqCode == STORAGE_PERM && results.length > 0) {
            if (results[0] == GRANTED) { u.selectImage(this, SELECT_IMAGE); }
            else { Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show(); }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        Uri uri = data.getData();

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            if (uri != null) {
                try {
                    inputStream = getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    image.setImageBitmap(bitmap); image.setVisibility(VISIBLE);
                    selectedImagePath = u.getPath(uri);
                    findViewById(R.id.deleteImage).setVisibility(VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * TextWatcher implementations. Only 'onTextChanged()' is required, for empty field checks :)
     */
    @Override
    public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
        checkIfEmpty(title, clearTBtn); checkIfEmpty(subtitle, clearSBtn); checkIfEmpty(content, clearCBtn);
    }
    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {} // Method not required
    public void afterTextChanged(Editable s) {}                              // Method not required

    public void checkIfEmpty(EditText field, ImageView clearTextBtn) {
        if (!field.getText().toString().trim().isEmpty())  { clearTextBtn.setVisibility(VISIBLE); }
        else { clearTextBtn.setVisibility(GONE); }
    }

    @Override @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton: onBackPressed(); break;
            case R.id.saveButton: saveNote(); break;
            case R.id.removeTitle: title.getText().clear(); break;
            case R.id.removeSubtitle: subtitle.getText().clear(); break;
            case R.id.removeContent: content.getText().clear(); break;
            case R.id.addURL: Utilities.urlDialog = Utilities.showDialog(this, this, this, urlV); break;
            case R.id.cancelAddURL: Utilities.urlDialog.dismiss(); break;
            case R.id.confirmAddURL: Utilities.validateURL(this, input, Utilities.urlDialog); break;
            case R.id.deleteURL: url.setText(""); input.setText(""); noteURL.setVisibility(GONE); break;
            case R.id.colourIndicator: case R.id.noteOptionsText: toggleNoteOptions(o); break;  // 2 cases
            case R.id.delete: Utilities.deleteDialog = Utilities.showDialog(this, this, deleteV, o); break;
            case R.id.cancelDeleteNote: Utilities.deleteDialog.dismiss(); break;
            case R.id.confirmDeleteNote: new DeleteNoteTask().execute(); break;
            case R.id.addImage: u.reqPermOrSelectImg(this, appContext, STORAGE_PERM, SELECT_IMAGE); break;
            case R.id.deleteImage: image.setImageBitmap(null); image.setVisibility(GONE);
                findViewById(R.id.deleteImage).setVisibility(GONE); selectedImagePath = ""; break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SaveNoteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NotesDatabase.getDatabase(appContext).noteDao().insert(note); return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent();
            Intent homePage = new Intent(CreateNote.this, Home.class);
            setResult(RESULT_OK, intent);
            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity(homePage);
        }
    }
    class DeleteNoteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NotesDatabase.getDatabase(appContext).noteDao().delete(existingNote); return null;
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
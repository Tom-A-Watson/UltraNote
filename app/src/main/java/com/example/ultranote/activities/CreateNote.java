package com.example.ultranote.activities;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import database.NotesDatabase;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.graphics.drawable.Drawable;
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
import utilities.Utilities;

public class CreateNote extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private boolean galleryAccessIsNotGranted;
    private Note existingNote;
    final Note note = new Note();
    private UserSettings settings;
    private Utilities u;
    private EditText title, subtitle, content, input;
    private TextView dateTime, url, createNoteText, noteOptionsText, colourPickerText;
    private View colourIndicator, urlView, sIndicator, deleteNoteView;
    private ImageView image, backBtn, addURL, addImg, saveBtn, removeTitle, removeSubtitle, removeContent;
    private ImageView[] colours;
    private Drawable[] darkBGColours, lightBGColours;
    private InputStream inputStream;
    private Bitmap bitmap;
    private BottomSheetBehavior<LinearLayout> options;
    private LinearLayout urlBox, noteOptions, deleteBtn;
    private CoordinatorLayout createNoteView;
    private AlertDialog deleteNoteDialog;
    private AlertDialog.Builder builder;
    private String selectedImagePath, type, singleLineDate, multiLineDate;

    // Request codes
    private static final int STORAGE_PERMISSION = 1;
    private static final int SELECT_IMAGE = 2;
    private static final int VERIFY_URL = 3;

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
        dateTime = findViewById(R.id.textDateTime);
        dateTime.setText(singleLineDate);
        initNoteOptions();
        updateView();
    }

    private void initComponents() {
        Utilities.initListeners(this, this, this);
        Utilities.initCreateNoteDrawables(this);
        Utilities.initGlobalColours(this);
        Utilities.urlDialog = null;

        settings = (UserSettings) getApplication();
        u = new Utilities(getApplication());
        createNoteView = findViewById(R.id.createNoteView);
        createNoteText = findViewById(R.id.createNoteText);
        backBtn = findViewById(R.id.backButton);
        addURL = findViewById(R.id.addURL);
        addImg = findViewById(R.id.addImage);
        saveBtn = findViewById(R.id.saveButton);
        deleteBtn = findViewById(R.id.deleteBtnLayout);
        title = findViewById(R.id.noteTitleInput);
        subtitle = findViewById(R.id.noteSubtitleInput);
        sIndicator = findViewById(R.id.subtitleIndicator);
        content = findViewById(R.id.noteContent);
        dateTime = findViewById(R.id.textDateTime);
        dateTime.setText(singleLineDate);
        colourIndicator = findViewById(R.id.colourIndicator);
        image = findViewById(R.id.noteImage);
        url = findViewById(R.id.webUrl);
        urlBox = findViewById(R.id.urlLayout);
        removeTitle = findViewById(R.id.removeTitle);
        removeSubtitle = findViewById(R.id.removeSubtitle);
        removeContent = findViewById(R.id.removeContent);
        noteOptions = findViewById(R.id.noteOptionsLayout);
        noteOptionsText = findViewById(R.id.noteOptionsText);
        colourPickerText = findViewById(R.id.colourPickerText);
        options = BottomSheetBehavior.from(noteOptions);
        builder = new AlertDialog.Builder(CreateNote.this);
        urlView = LayoutInflater.from(this).inflate(
                R.layout.add_url_layout,
                findViewById(R.id.addURLLayout)
        );
        deleteNoteView = LayoutInflater.from(this).inflate(
                R.layout.delete_note_layout,
                findViewById(R.id.deleteNoteLayout)
        );
        input = urlView.findViewById(R.id.inputURL);
        singleLineDate = new SimpleDateFormat(
                "EEEE dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date());
        multiLineDate = new SimpleDateFormat(
                "EEEE dd MMMM yyyy \nHH:mm a", Locale.getDefault()).format(new Date());
        type = getIntent().getStringExtra("quickActionType");
        galleryAccessIsNotGranted = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;

        // List of note colour buttons + accompanying lists of light & dark theme Drawables
        colours = Utilities.initColourButtons(this);
        darkBGColours = Utilities.initDarkBGColourButtons(this);
        lightBGColours = Utilities.initLightBGColourButtons(this);
    }

    private void updateView() {
        if (settings.theme().equals(UserSettings.LIGHT_THEME)) {// Set components to light mode colours
            createNoteView.setBackgroundColor(Utilities.white);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            title.setTextColor(Utilities.darkGrey);    createNoteText.setTextColor(Utilities.black);
            subtitle.setTextColor(Utilities.darkGrey); noteOptionsText.setTextColor(Utilities.black);
            content.setTextColor(Utilities.black);     colourPickerText.setTextColor(Utilities.darkGrey);
            dateTime.setTextColor(Utilities.black);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            backBtn.setColorFilter(Utilities.black);    addURL.setColorFilter(Utilities.lightGrey);
            addImg.setColorFilter(Utilities.lightGrey);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            saveBtn.setBackground(Utilities.saveBtnLight); sIndicator.setBackground(Utilities.blue);
            noteOptions.setBackground(Utilities.light);

            for (int i = 0; i < colours.length; i++) { colours[i].setBackground(lightBGColours[i]); }
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            title.setHintTextColor(Utilities.darkGrey); subtitle.setHintTextColor(Utilities.darkGrey);
            content.setHintTextColor(Utilities.black);
            //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            url.setLinkTextColor(Utilities.lightModeAccent);

            return;
        }
        // Revert components to their default colours
        createNoteView.setBackgroundColor(Utilities.darkGrey);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        title.setTextColor(Utilities.white);    createNoteText.setTextColor(Utilities.white);
        subtitle.setTextColor(Utilities.white); noteOptionsText.setTextColor(Utilities.white);
        content.setTextColor(Utilities.white);  colourPickerText.setTextColor(Utilities.offWhite);
        dateTime.setTextColor(Utilities.white);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        backBtn.setColorFilter(Utilities.white);   addURL.setColorFilter(Utilities.offWhite);
        addImg.setColorFilter(Utilities.offWhite);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        saveBtn.setBackground(Utilities.saveBtnDark); sIndicator.setBackground(Utilities.lightBlue);
        noteOptions.setBackground(Utilities.dark);

        for (int i = 0; i < colours.length; i++) { colours[i].setBackground(darkBGColours[i]); }
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        title.setHintTextColor(Utilities.offWhite);   subtitle.setHintTextColor(Utilities.offWhite);
        content.setHintTextColor(Utilities.offWhite);
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        url.setLinkTextColor(Utilities.darkModeAccent);
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
                //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
                    case "image": selectedImagePath = getIntent().getStringExtra("imagePath");
                                  image.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                                  image.setVisibility(VISIBLE);
                                  findViewById(R.id.deleteImage).setVisibility(VISIBLE); break;
                //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
                    case "URL": url.setText(getIntent().getStringExtra("URL"));
                                urlBox.setVisibility(VISIBLE); break;
                }
            }
        }
    }

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
            urlBox.setVisibility(VISIBLE);
        }
    }

    private void saveNote() {
        if (title.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title is empty!", Toast.LENGTH_SHORT).show(); return;
        }
        if (content.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "The note has no content!", Toast.LENGTH_SHORT).show(); return;
        }
        if (urlBox.getVisibility() == VISIBLE) { note.setWebLink(url.getText().toString()); }
        if (existingNote != null) { note.setId(existingNote.getId()); }
        if (options.getState() == EXPANDED) { options.setState(COLLAPSED); }

        note.setTitle(title.getText().toString());      note.setSubtitle(subtitle.getText().toString());
        note.setNoteText(content.getText().toString()); note.setDateTime(dateTime.getText().toString());
        note.setImagePath(selectedImagePath);           dateTime.setText(multiLineDate);

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
        if (existingNote == null) { setColourTo("#333333"); switchTickToBtn(0); }
        else { deleteBtn.setVisibility(VISIBLE); deleteBtn.setOnClickListener(this); }

        // Loop fixes duplicate tick bug by nullifying all colour buttons' ticks upon the activity resuming
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

    public void switchTickToBtn(int btnIndex) {
        colours[0].setImageResource(0); colours[btnIndex].setImageResource(R.drawable.ic_done);
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(reqCode, perms, results);

        if (reqCode == STORAGE_PERMISSION && results.length > 0) {
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
        checkEmptyField(title, removeTitle); checkEmptyField(subtitle, removeSubtitle);
        checkEmptyField(content, removeContent);
    }
    public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {} // Method not required
    public void afterTextChanged(Editable s) {}                              // Method not required

    public void checkEmptyField(EditText field, ImageView clearBtn) {
        if (!field.getText().toString().trim().isEmpty())  { clearBtn.setVisibility(VISIBLE); }
        else { clearBtn.setVisibility(GONE); }
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
            case R.id.addURL: Utilities.urlDialog = Utilities.showDialog(urlView, builder, this); break;
            case R.id.cancelAddURL: Utilities.urlDialog.dismiss(); break;
            case R.id.confirmAddURL: Utilities.validateURL(this, input, Utilities.urlDialog, VERIFY_URL); break;
            case R.id.deleteURL: url.setText(null); input.setText(""); urlBox.setVisibility(GONE); break;
            case R.id.colourIndicator: case R.id.noteOptionsText: toggleNoteOptions(options); break;
            case R.id.deleteBtnLayout: options.setState(COLLAPSED); showDeleteNoteDialog(); break;
            case R.id.cancelDeleteNote: deleteNoteDialog.dismiss(); break;
            case R.id.deleteNote: new DeleteNoteTask().execute(); break;
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            case R.id.addImage: if (galleryAccessIsNotGranted) {
                                    ActivityCompat.requestPermissions(
                                            CreateNote.this,
                                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                            STORAGE_PERMISSION
                                    );
                                } else { u.selectImage(this, SELECT_IMAGE); } break;
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
            case R.id.deleteImage: image.setImageBitmap(null); image.setVisibility(GONE);
                findViewById(R.id.deleteImage).setVisibility(GONE); selectedImagePath = ""; break;
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